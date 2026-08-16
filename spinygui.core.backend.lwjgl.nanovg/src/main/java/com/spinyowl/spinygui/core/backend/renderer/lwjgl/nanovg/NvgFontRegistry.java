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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.lwjgl.stb.STBTTFontinfo;

class NvgFontRegistry {
  private static final int REPLACEMENT_CODE_POINT = 0xFFFD;

  private final Map<FontResourceKey, FontFace> loadedFontFaces = new HashMap<>();
  private final Map<FontResourceKey, ByteBuffer> fontBuffers = new HashMap<>();
  private final Map<FontResourceKey, STBTTFontinfo> fontInfos = new HashMap<>();
  private final Set<FontResourceKey> retryableFaceFailures = new HashSet<>();
  private final NvgRenderer renderer;
  private final FaceCreator faceCreator;

  private Map<SemanticFontOwner.FaceKey, SemanticFontOwner.Identity> observedIdentities = Map.of();
  private long observedGeneration;
  private long contextIdentity;
  private long faceCreationFailures;

  NvgFontRegistry() {
    this(null, FaceCreator.NATIVE);
  }

  NvgFontRegistry(NvgRenderer renderer, FaceCreator faceCreator) {
    this.renderer = renderer;
    this.faceCreator = Objects.requireNonNull(faceCreator, "faceCreator");
  }

  void bindContext(long context, SemanticFontOwner.Observation observation) {
    Objects.requireNonNull(observation, "observation");
    if (context == 0) {
      throw new IllegalArgumentException("NanoVG font registry context must be non-zero");
    }
    if (contextIdentity != 0 && contextIdentity != context) {
      throw new IllegalStateException(
          "NanoVG font resources cannot migrate to a different context");
    }
    contextIdentity = context;
    reconcile(observation);
  }

  String fontFace(Font font, long nanovg) {
    prepareContext(nanovg);

    FontResourceKey key = resourceKey(font);
    FontFace face = loadedFontFaces.get(key);
    if (face == null) {
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
      retryableFaceFailures.remove(key);
    }
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
    reconcile(Font.semanticOwner().observation());
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

  private boolean hasGlyph(Font primaryFont, int codePoint) {
    return glyphIndex(primaryFont, codePoint) != 0;
  }

  private void prepareContext(long context) {
    SemanticFontOwner owner = Font.semanticOwner();
    if (renderer == null) {
      bindContext(context, owner.observation());
    } else {
      renderer.requireFontFaceUse(context);
      reconcile(owner.observation());
    }
  }

  private int glyphIndex(Font font, int codePoint) {
    FontResourceKey key = resourceKey(font);
    return stbtt_FindGlyphIndex(
        fontInfos.computeIfAbsent(key, ignored -> loadFontInfo(key, font)), codePoint);
  }

  private STBTTFontinfo loadFontInfo(FontResourceKey key, Font font) {
    ByteBuffer fontBuffer = fontBuffer(key, font);
    STBTTFontinfo fontInfo = STBTTFontinfo.create();
    if (fontBuffer == null || !stbtt_InitFont(fontInfo, fontBuffer.duplicate())) {
      throw new IllegalStateException("Failed to load font from '%s'".formatted(font.path()));
    }
    return fontInfo;
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

    fontInfos.keySet().removeIf(resource -> resource.isStale(current));
    fontBuffers.keySet().removeIf(resource -> resource.isStale(current));
    retryableFaceFailures.removeIf(resource -> resource.isStale(current));
    observedIdentities = Map.copyOf(current);
    observedGeneration = observation.generation();
  }

  private FontResourceKey resourceKey(Font font) {
    if (contextIdentity == 0) {
      throw new IllegalStateException(
          "NanoVG font resources require a bound non-zero context");
    }
    SemanticFontOwner.FaceKey faceKey = faceKey(font);
    String normalizedLocator = SemanticFontOwner.normalizeLocator(font.path());
    SemanticFontOwner.Identity semanticIdentity = observedIdentities.get(faceKey);
    if (semanticIdentity != null
        && !semanticIdentity.normalizedLocator().equals(normalizedLocator)) {
      throw new IllegalStateException(
          "NanoVG font descriptor is stale for the current semantic identity");
    }
    return new FontResourceKey(contextIdentity, faceKey, semanticIdentity, normalizedLocator);
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

  @FunctionalInterface
  interface FaceCreator {
    FaceCreator NATIVE =
        (context, name, bytes) -> nvgCreateFontMem(context, name, bytes, false);

    int create(long context, String name, ByteBuffer bytes);
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
