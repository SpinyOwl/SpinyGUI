package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.lwjgl.stb.STBTTFontinfo;

class NvgFontRegistry {
  private static final int REPLACEMENT_CODE_POINT = 0xFFFD;
  /** Maximum descriptor aliases retained by the generation-scoped NanoVG lookup fast path. */
  private static final int RESOURCE_KEY_CACHE_LIMIT = 64;

  private final Map<FontResourceKey, FontFace> loadedFontFaces = new HashMap<>();
  private final Map<FontResourceKey, ByteBuffer> fontBuffers = new HashMap<>();
  private final Map<FontResourceKey, OwnedFontInfo> fontInfos = new HashMap<>();
  private final Set<FontResourceKey> retryableFaceFailures = new HashSet<>();
  /**
   * Generation-scoped descriptor memoization for the renderer hot path. Entries are hard-capped
   * and cleared on semantic reconciliation or context teardown.
   */
  private final Map<Font, FontResourceKey> resourceKeysByFont = new HashMap<>();
  /** Direct face lookup paired with {@link #resourceKeysByFont} and cleared at the same boundaries. */
  private final Map<Font, FontFace> fontFacesByFont = new HashMap<>();
  private final NvgRenderer renderer;
  private final FaceCreator faceCreator;
  private final FontInfoAllocator fontInfoAllocator;

  private Map<SemanticFontOwner.FaceKey, SemanticFontOwner.Identity> observedIdentities = Map.of();
  private long observedGeneration;
  private long contextIdentity;
  private long faceCreationFailures;

  NvgFontRegistry() {
    this(null, FaceCreator.NATIVE, FontInfoAllocator.MANAGED);
  }

  NvgFontRegistry(NvgRenderer renderer, FaceCreator faceCreator) {
    this(renderer, faceCreator, FontInfoAllocator.OWNED);
  }

  NvgFontRegistry(
      NvgRenderer renderer, FaceCreator faceCreator, FontInfoAllocator fontInfoAllocator) {
    this.renderer = renderer;
    this.faceCreator = Objects.requireNonNull(faceCreator, "faceCreator");
    this.fontInfoAllocator = Objects.requireNonNull(fontInfoAllocator, "fontInfoAllocator");
  }

  void bindContext(long context, SemanticFontOwner.Observation observation) {
    Objects.requireNonNull(observation, "observation");
    bindContextIdentity(context);
    reconcile(observation);
  }

  String fontFace(Font font, long nanovg) {
    prepareContext(nanovg);

    FontFace face = fontFacesByFont.get(font);
    if (face != null) {
      return face.name();
    }
    FontResourceKey key = resourceKey(font);
    face = loadedFontFaces.get(key);
    if (face != null) {
      fontFacesByFont.put(font, face);
      return face.name();
    }

    ByteBuffer fontBuffer = fontBuffer(key, font);
    if (fontBuffer == null) {
      return null;
    }
    String fontFace =
        font.fontFamily()
            + "-"
            + Integer.toUnsignedString(fontKey(font).hashCode())
            + "-"
            + Integer.toUnsignedString(Long.hashCode(contextIdentity));
    int id;
    try {
      id = faceCreator.create(nanovg, fontFace, fontBuffer.duplicate());
    } catch (RuntimeException | Error failure) {
      recordFaceFailure(key);
      throw failure;
    }
    if (id == -1) {
      recordFaceFailure(key);
      return null;
    }

    face = new FontFace(key, fontFace, id);
    loadedFontFaces.put(key, face);
    fontFacesByFont.put(font, face);
    retryableFaceFailures.remove(key);
    return face.name();
  }

  void beforeReplacement(
      SemanticFontOwner.Identity previous, SemanticFontOwner.Identity replacement) {
    if (!previous.key().equals(replacement.key())) {
      throw new IllegalArgumentException("Semantic replacement preflight requires one face key");
    }
    if (loadedFontFaces.keySet().stream()
        .anyMatch(resource -> resource.faceKey().equals(previous.key()))) {
      throw new IllegalStateException(
          "Destroy the initialized NanoVG renderer before replacing an active font face");
    }
  }

  String displayText(long context, Font primaryFont, String text) {
    prepareContext(context);
    StringBuilder displayText = new StringBuilder(text.length());
    text.codePoints()
        .forEach(
            codePoint -> {
              if (Character.isISOControl(codePoint) || hasGlyph(primaryFont, codePoint)) {
                displayText.appendCodePoint(codePoint);
              } else {
                displayText.appendCodePoint(REPLACEMENT_CODE_POINT);
              }
            });
    return displayText.toString();
  }

  NvgFontResourceObservation observation() {
    reconcileCurrentGeneration(Font.semanticOwner());
    Set<SemanticFontOwner.Identity> retainedIdentities = new HashSet<>();
    collectSemanticIdentities(loadedFontFaces.keySet(), retainedIdentities);
    collectSemanticIdentities(fontBuffers.keySet(), retainedIdentities);
    collectSemanticIdentities(fontInfos.keySet(), retainedIdentities);
    collectSemanticIdentities(retryableFaceFailures, retainedIdentities);
    return new NvgFontResourceObservation(
        observedGeneration,
        contextIdentity,
        contextIdentity == 0 ? 0 : 1,
        loadedFontFaces.size(),
        fontBuffers.size(),
        fontInfos.size(),
        retryableFaceFailures.size(),
        faceCreationFailures,
        0,
        retainedIdentities);
  }

  boolean hasBoundContext() {
    return contextIdentity != 0;
  }

  void releaseAfterContextDelete() {
    if (contextIdentity == 0) {
      return;
    }

    loadedFontFaces.clear();
    retryableFaceFailures.clear();
    recordLifecycle(NvgRenderer.LifecycleEvent.RELEASE_FACE_AND_RETRY_STATE);

    RuntimeException runtimeFailure = null;
    Error errorFailure = null;
    for (Map.Entry<FontResourceKey, OwnedFontInfo> entry : List.copyOf(fontInfos.entrySet())) {
      try {
        entry.getValue().free();
        fontInfos.remove(entry.getKey());
        recordLifecycle(NvgRenderer.LifecycleEvent.FREE_BACKEND_STB_FONT_INFO);
      } catch (RuntimeException failure) {
        if (runtimeFailure == null) {
          runtimeFailure = failure;
        } else {
          runtimeFailure.addSuppressed(failure);
        }
      } catch (Error failure) {
        if (errorFailure == null) {
          errorFailure = failure;
        } else {
          errorFailure.addSuppressed(failure);
        }
      }
    }
    if (runtimeFailure != null) {
      if (errorFailure != null) {
        runtimeFailure.addSuppressed(errorFailure);
      }
      throw runtimeFailure;
    }
    if (errorFailure != null) {
      throw errorFailure;
    }

    fontBuffers.clear();
    recordLifecycle(NvgRenderer.LifecycleEvent.DROP_FONT_BUFFER_REFERENCES);
    resourceKeysByFont.clear();
    fontFacesByFont.clear();
    observedIdentities = Map.of();
    observedGeneration = 0;
    faceCreationFailures = 0;
    contextIdentity = 0;
  }

  private boolean hasGlyph(Font primaryFont, int codePoint) {
    return glyphIndex(primaryFont, codePoint) != 0;
  }

  private void prepareContext(long context) {
    SemanticFontOwner owner = Font.semanticOwner();
    if (renderer == null) {
      bindContextIdentity(context);
    } else {
      renderer.requireFontFaceUse(context);
    }
    reconcileCurrentGeneration(owner);
  }

  private int glyphIndex(Font font, int codePoint) {
    FontResourceKey key = resourceKey(font);
    OwnedFontInfo fontInfo = fontInfos.computeIfAbsent(key, ignored -> loadFontInfo(key, font));
    return stbtt_FindGlyphIndex(fontInfo.value(), codePoint);
  }

  private OwnedFontInfo loadFontInfo(FontResourceKey key, Font font) {
    ByteBuffer fontBuffer = fontBuffer(key, font);
    STBTTFontinfo fontInfo = fontInfoAllocator.allocate();
    try {
      if (fontBuffer == null || !stbtt_InitFont(fontInfo, fontBuffer.duplicate())) {
        throw new IllegalStateException("Failed to load font from '%s'".formatted(font.path()));
      }
      return new OwnedFontInfo(fontInfo, fontInfoAllocator);
    } catch (RuntimeException | Error failure) {
      fontInfoAllocator.free(fontInfo);
      throw failure;
    }
  }

  private ByteBuffer fontBuffer(FontResourceKey key, Font font) {
    return fontBuffers.computeIfAbsent(key, ignored -> IOUtil.resourceAsByteBuffer(font.path()));
  }

  private void recordFaceFailure(FontResourceKey key) {
    retryableFaceFailures.add(key);
    faceCreationFailures++;
  }

  private void reconcile(SemanticFontOwner.Observation observation) {
    Objects.requireNonNull(observation, "observation");
    if (observation.generation() == observedGeneration) {
      return;
    }

    Map<SemanticFontOwner.FaceKey, SemanticFontOwner.Identity> current = new LinkedHashMap<>();
    for (SemanticFontOwner.Identity identity : observation.identities()) {
      current.put(identity.key(), identity);
    }

    FontResourceKey staleFace =
        loadedFontFaces.keySet().stream()
            .filter(resource -> resource.isStale(current))
            .findFirst()
            .orElse(null);
    if (staleFace != null) {
      throw new IllegalStateException(
          "NanoVG active font face is stale; destroy and replace the renderer");
    }

    List<FontResourceKey> staleInfos =
        fontInfos.keySet().stream().filter(resource -> resource.isStale(current)).toList();
    for (FontResourceKey stale : staleInfos) {
      fontInfos.get(stale).free();
      fontInfos.remove(stale);
    }
    fontBuffers.keySet().removeIf(
        resource -> resource.isStale(current) && !fontInfos.containsKey(resource));
    retryableFaceFailures.removeIf(resource -> resource.isStale(current));
    resourceKeysByFont.clear();
    fontFacesByFont.clear();
    observedIdentities = Map.copyOf(current);
    observedGeneration = observation.generation();
  }

  private FontResourceKey resourceKey(Font font) {
    if (contextIdentity == 0) {
      throw new IllegalStateException(
          "NanoVG font resources require a bound non-zero context");
    }
    FontResourceKey cached = resourceKeysByFont.get(font);
    if (cached != null) {
      return cached;
    }
    SemanticFontOwner.FaceKey faceKey = faceKey(font);
    String normalizedLocator = SemanticFontOwner.normalizeLocator(font.path());
    SemanticFontOwner.Identity semanticIdentity = observedIdentities.get(faceKey);
    if (semanticIdentity != null
        && !semanticIdentity.normalizedLocator().equals(normalizedLocator)) {
      throw new IllegalStateException(
          "NanoVG font descriptor is stale for the current semantic identity");
    }
    FontResourceKey key =
        new FontResourceKey(contextIdentity, faceKey, semanticIdentity, normalizedLocator);
    cacheResourceKey(font, key);
    return key;
  }

  private void bindContextIdentity(long context) {
    if (context == 0) {
      throw new IllegalArgumentException("NanoVG font registry context must be non-zero");
    }
    if (contextIdentity != 0 && contextIdentity != context) {
      throw new IllegalStateException("NanoVG font resources cannot migrate to a different context");
    }
    contextIdentity = context;
  }

  private void reconcileCurrentGeneration(SemanticFontOwner owner) {
    if (owner.generation() != observedGeneration) {
      reconcile(owner.observation());
    }
  }

  private void cacheResourceKey(Font font, FontResourceKey key) {
    if (resourceKeysByFont.size() >= RESOURCE_KEY_CACHE_LIMIT
        && !resourceKeysByFont.containsKey(font)) {
      resourceKeysByFont.clear();
      fontFacesByFont.clear();
    }
    resourceKeysByFont.put(font, key);
  }

  private String fontKey(Font font) {
    return font.fontFamily()
        + "|"
        + font.style()
        + "|"
        + font.weight()
        + "|"
        + font.stretch()
        + "|"
        + font.path();
  }

  private SemanticFontOwner.FaceKey faceKey(Font font) {
    return new SemanticFontOwner.FaceKey(
        font.fontFamily(),
        font.style().name(),
        font.weight().name(),
        font.stretch().name());
  }

  private static void collectSemanticIdentities(
      Set<FontResourceKey> resources, Set<SemanticFontOwner.Identity> destination) {
    resources.stream()
        .map(FontResourceKey::semanticIdentity)
        .filter(Objects::nonNull)
        .forEach(destination::add);
  }

  private void recordLifecycle(NvgRenderer.LifecycleEvent event) {
    if (renderer != null) {
      renderer.recordLifecycle(event);
    }
  }

  @FunctionalInterface
  interface FaceCreator {
    FaceCreator NATIVE =
        (context, name, bytes) -> nvgCreateFontMem(context, name, bytes, false);

    int create(long context, String name, ByteBuffer bytes);
  }

  interface FontInfoAllocator {
    FontInfoAllocator MANAGED =
        new FontInfoAllocator() {
          @Override
          public STBTTFontinfo allocate() {
            return STBTTFontinfo.create();
          }

          @Override
          public void free(STBTTFontinfo fontInfo) {}
        };
    FontInfoAllocator OWNED =
        new FontInfoAllocator() {
          @Override
          public STBTTFontinfo allocate() {
            return STBTTFontinfo.malloc();
          }

          @Override
          public void free(STBTTFontinfo fontInfo) {
            fontInfo.free();
          }
        };

    STBTTFontinfo allocate();

    void free(STBTTFontinfo fontInfo);
  }

  private static final class OwnedFontInfo {
    private final STBTTFontinfo value;
    private final FontInfoAllocator allocator;
    private boolean freed;

    private OwnedFontInfo(STBTTFontinfo value, FontInfoAllocator allocator) {
      this.value = Objects.requireNonNull(value, "value");
      this.allocator = Objects.requireNonNull(allocator, "allocator");
    }

    private STBTTFontinfo value() {
      if (freed) {
        throw new IllegalStateException("NanoVG backend STB font info is already freed");
      }
      return value;
    }

    private void free() {
      if (!freed) {
        allocator.free(value);
        freed = true;
      }
    }
  }

  private record FontResourceKey(
      long context,
      SemanticFontOwner.FaceKey faceKey,
      SemanticFontOwner.Identity semanticIdentity,
      String normalizedLocator) {
    private FontResourceKey {
      Objects.requireNonNull(faceKey, "faceKey");
      Objects.requireNonNull(normalizedLocator, "normalizedLocator");
    }

    private boolean isStale(
        Map<SemanticFontOwner.FaceKey, SemanticFontOwner.Identity> current) {
      SemanticFontOwner.Identity currentIdentity = current.get(faceKey);
      return semanticIdentity == null
          ? currentIdentity != null
          : !semanticIdentity.equals(currentIdentity);
    }
  }

  private record FontFace(FontResourceKey resourceKey, String name, int id) {}
}
