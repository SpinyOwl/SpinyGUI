package com.spinyowl.spinygui.core.system.font.impl;

import static org.lwjgl.stb.STBTruetype.STBTT_MS_EID_UNICODE_BMP;
import static org.lwjgl.stb.STBTruetype.STBTT_MS_LANG_ENGLISH;
import static org.lwjgl.stb.STBTruetype.STBTT_PLATFORM_ID_MICROSOFT;
import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontNameString;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForMappingEmToPixels;
import static org.slf4j.LoggerFactory.getLogger;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.FontLoadingException;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontResourceObservation;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.FontSemanticObservation;
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner.FaceKey;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner.FontLoadRequest;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner.FontRequest;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner.Mutation;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner.MutationOutcome;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.PreparedRange;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerCapability;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import com.spinyowl.spinygui.core.system.cache.BoundedTextCache;
import com.spinyowl.spinygui.core.system.cache.TextCacheConfiguration;
import com.spinyowl.spinygui.core.system.cache.ResolvedPrimitiveKey;
import com.spinyowl.spinygui.core.system.cache.WrappedLayoutKey;
import com.spinyowl.spinygui.core.system.cache.TextCacheAggregateObservation;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public class FontServiceImpl implements FontService, TextMeasurer, RangeTextMeasurerCapability {
  private static final Logger LOG = getLogger(FontServiceImpl.class);

  private static final String SUBINDEX_SPLIT_REGEX = "\\s+";
  private static final String SUBFEATURE_SPLIT_REGEX = "(?=\\p{Upper})";
  private static final int FONT_FAMILY_INDEX = 1;
  private static final int FONT_SUBFAMILY_INDEX = 2;
  private static final int TYPOGRAPHIC_FONT_FAMILY_INDEX = 16;
  private static final int TYPOGRAPHIC_FONT_SUBFAMILY_INDEX = 17;
  private static final int REPLACEMENT_CODE_POINT = 0xFFFD;

  @NonNull private final FontStorage fontStorage;
  private final boolean roundToPixel;
  @NonNull private final DiagnosticSession diagnostics;
  @NonNull private final ResourceLifecycleObserver resourceLifecycleObserver;
  @NonNull private final TransactionFailureInjector transactionFailureInjector;
  @NonNull private final TextCacheConfiguration cacheConfiguration;
  private Map<String, OwnedFontInfo> fontInfoMap = new HashMap<>();
  private final BoundedTextCache<GlyphProbeKey, Integer> glyphProbeCache;
  private final BoundedTextCache<AdvanceKey, BaseAdvanceMeasurement> advanceCache;
  private final BoundedTextCache<KerningKey, KerningValue> kerningCache;
  private final BoundedTextCache<ResolvedPrimitiveKey, ResolvedPrimitiveSequence> resolvedPrimitiveCache;
  private final BoundedTextCache<WrappedLayoutKey, ResolvedMeasurement> wrappedLayoutCache;
  private SemanticFontOwner semanticOwner;
  private FontServiceImpl aggregateService;

  public FontServiceImpl(@NonNull FontStorage fontStorage, boolean roundToPixel) {
    this(fontStorage, roundToPixel, DiagnosticSession.disabled());
  }

  public FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull DiagnosticSession diagnostics) {
    this(fontStorage, roundToPixel, diagnostics, ResourceLifecycleObserver.NO_OP);
  }

  /** Creates a service with explicit bounded primitive-cache mode and limits. */
  public FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull DiagnosticSession diagnostics,
      @NonNull TextCacheConfiguration cacheConfiguration) {
    this(
        fontStorage,
        roundToPixel,
        diagnostics,
        ResourceLifecycleObserver.NO_OP,
        TransactionFailureInjector.NO_OP,
        cacheConfiguration);
  }

  FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull DiagnosticSession diagnostics,
      @NonNull ResourceLifecycleObserver resourceLifecycleObserver) {
      this(
        fontStorage,
        roundToPixel,
        diagnostics,
        resourceLifecycleObserver,
        TransactionFailureInjector.NO_OP,
        TextCacheConfiguration.disabled());
  }

  FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull DiagnosticSession diagnostics,
      @NonNull ResourceLifecycleObserver resourceLifecycleObserver,
      @NonNull TransactionFailureInjector transactionFailureInjector) {
    this(
        fontStorage,
        roundToPixel,
        diagnostics,
        resourceLifecycleObserver,
        transactionFailureInjector,
        TextCacheConfiguration.disabled());
  }

  FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull DiagnosticSession diagnostics,
      @NonNull ResourceLifecycleObserver resourceLifecycleObserver,
      @NonNull TransactionFailureInjector transactionFailureInjector,
      @NonNull TextCacheConfiguration cacheConfiguration) {
    this.fontStorage = fontStorage;
    this.roundToPixel = roundToPixel;
    this.diagnostics = diagnostics;
    this.resourceLifecycleObserver = resourceLifecycleObserver;
    this.transactionFailureInjector = transactionFailureInjector;
    this.cacheConfiguration = cacheConfiguration;
    this.glyphProbeCache =
        new BoundedTextCache<>(
            cacheConfiguration.glyphEntries(),
            cacheConfiguration.glyphWeight(),
            ignored -> 1,
            cacheConfiguration.enabled());
    this.advanceCache =
        new BoundedTextCache<>(
            cacheConfiguration.advanceEntries(),
            cacheConfiguration.advanceWeight(),
            ignored -> 8,
            cacheConfiguration.enabled());
    this.kerningCache =
        new BoundedTextCache<>(
            cacheConfiguration.kerningEntries(),
            cacheConfiguration.kerningWeight(),
            ignored -> 8,
            cacheConfiguration.enabled());
    this.resolvedPrimitiveCache =
        new BoundedTextCache<>(cacheConfiguration.resolvedEntries(), cacheConfiguration.resolvedWeight(),
            FontServiceImpl::primitiveWeight, cacheConfiguration.enabled());
    this.wrappedLayoutCache =
        new BoundedTextCache<>(cacheConfiguration.wrappedEntries(), cacheConfiguration.wrappedWeight(),
            FontServiceImpl::measurementWeight, cacheConfiguration.enabled());
  }

  /**
   * @deprecated resolver selection is owned by the installed {@link SemanticFontOwner}; the
   *     argument is retained only for source compatibility and cannot replace production ownership
   */
  @Deprecated(forRemoval = false)
  public FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull FontChainResolver fontChainResolver) {
    this(fontStorage, roundToPixel, fontChainResolver, DiagnosticSession.disabled());
  }

  /**
   * @deprecated resolver selection is owned by the installed {@link SemanticFontOwner}; the
   *     argument is retained only for source compatibility and cannot replace production ownership
   */
  @Deprecated(forRemoval = false)
  public FontServiceImpl(
      @NonNull FontStorage fontStorage,
      boolean roundToPixel,
      @NonNull FontChainResolver fontChainResolver,
      @NonNull DiagnosticSession diagnostics) {
    this(fontStorage, roundToPixel, diagnostics);
    Objects.requireNonNull(fontChainResolver, "fontChainResolver");
  }

  @Override
  public DiagnosticSession diagnostics() {
    return diagnostics;
  }

  /** Package-private M7 evidence seam; it exposes counters without publishing cache ownership. */
  BoundedTextCache.Stats resolvedPrimitiveCacheStats() {
    return resolvedPrimitiveCache.stats();
  }

  /** Package-private M7 evidence seam; it exposes counters without publishing cache ownership. */
  BoundedTextCache.Stats wrappedLayoutCacheStats() {
    return wrappedLayoutCache.stats();
  }

  /** Package-private M7 seam combining cache, M3 native, and supplied M5 current-slot weights. */
  TextCacheAggregateObservation cacheAggregateObservation(List<Long> currentSnapshotWeights) {
    return cacheAggregateObservation(Map.of(), currentSnapshotWeights);
  }

  /** Package-private M7 seam allowing M4 context owners to contribute their own family stats. */
  public TextCacheAggregateObservation cacheAggregateObservation(
      Map<String, BoundedTextCache.Stats> additionalFamilies,
      List<Long> currentSnapshotWeights) {
    return cacheAggregateObservation(additionalFamilies, Map.of(), currentSnapshotWeights);
  }

  /** Package-private M7 seam combining core, M4, and backend-owned native resource classes. */
  public TextCacheAggregateObservation cacheAggregateObservation(
      Map<String, BoundedTextCache.Stats> additionalFamilies,
      Map<String, Long> nativeResourceWeights,
      List<Long> currentSnapshotWeights) {
    return cacheAggregateObservation(additionalFamilies, nativeResourceWeights, Map.of(), currentSnapshotWeights);
  }

  /** Package-private M7 seam separating native byte retention from native entry cardinality. */
  public TextCacheAggregateObservation cacheAggregateObservation(
      Map<String, BoundedTextCache.Stats> additionalFamilies,
      Map<String, Long> nativeByteWeights,
      Map<String, Long> nativeEntryCounts,
      List<Long> currentSnapshotWeights) {
    Map<String, BoundedTextCache.Stats> families = new HashMap<>(additionalFamilies);
    families.put("glyph", glyphProbeCache.stats());
    families.put("advance", advanceCache.stats());
    families.put("kerning", kerningCache.stats());
    families.put("resolved-primitive", resolvedPrimitiveCache.stats());
    families.put("wrapped-layout", wrappedLayoutCache.stats());
    return new TextCacheAggregateObservation(
        families,
        resourceObservation(),
        currentSnapshotWeights,
        mergeNativeResourceWeights(nativeByteWeights),
        nativeEntryCounts);
  }

  private Map<String, Long> mergeNativeResourceWeights(Map<String, Long> additional) {
    Map<String, Long> weights = new HashMap<>(additional);
    weights.putIfAbsent("core-font-bytes", resourceObservation().ownerByteCapacity());
    return weights;
  }

  /** Resets cache diagnostics independently while retaining warm entries. */
  void resetCacheDiagnostics() {
    glyphProbeCache.resetDiagnostics();
    advanceCache.resetDiagnostics();
    kerningCache.resetDiagnostics();
    resolvedPrimitiveCache.resetDiagnostics();
    wrappedLayoutCache.resetDiagnostics();
  }

  /** Clears every service-owned calculation family without changing configuration. */
  public void clearCacheFamilies() {
    glyphProbeCache.clear();
    advanceCache.clear();
    kerningCache.clear();
    resolvedPrimitiveCache.clear();
    wrappedLayoutCache.clear();
  }

  @Override
  public SemanticFontOwner installSemanticOwner() {
    if (semanticOwner != null) {
      semanticOwner.verifyUse();
      return semanticOwner;
    }

    FontStorageImpl storage = atomicStorage();
    if (Font.hasSemanticOwner()) {
      FontService installed = Font.semanticService();
      if (!(installed instanceof FontServiceImpl installedAggregate)) {
        throw new IllegalStateException(
            "Installed semantic font service cannot share the production resource aggregate");
      }
      SemanticFontOwner selected = Font.semanticOwner();
      semanticOwner = selected;
      aggregateService = installedAggregate.resourceAggregate();
      fontInfoMap = aggregateService.fontInfoMap;
      return selected;
    }

    List<StagedBuiltIn> staged =
        List.of(
            new StagedBuiltIn(Font.ROBOTO_LIGHT),
            new StagedBuiltIn(Font.ROBOTO_BOLD),
            new StagedBuiltIn(Font.ROBOTO_REGULAR),
            new StagedBuiltIn(Font.NOTO_SANS_CJK_SC_REGULAR));
    List<FontRequest> requests = new ArrayList<>(staged.size());
    for (int index = 0; index < staged.size(); index++) {
      requests.add(builtInRequest(storage, staged.get(index), index == staged.size() - 1));
    }

    try {
      SemanticFontOwner selected = new SemanticFontOwner(requests);
      Mutation bootstrap = selected.install().bootstrap();
      if (bootstrap.outcome() == MutationOutcome.REJECTED) {
        throw new IllegalStateException("Semantic font built-in bootstrap failed");
      }

      for (StagedBuiltIn candidate : staged) {
        ByteBuffer bytes = Objects.requireNonNull(candidate.bytes().get());
        OwnedFontInfo info = Objects.requireNonNull(candidate.info().get());
        String locator = SemanticFontOwner.normalizeLocator(candidate.font().path());
        storage.commitFontData(locator, bytes);
        fontInfoMap.put(locator, info);
        info.transferAfterPublication();
      }
      semanticOwner = selected;
      Font.installSemanticOwner(selected, this);
      storage.bindPublicReads(selected, this);
      return selected;
    } catch (RuntimeException | Error failure) {
      teardownAllRetainedResources(storage);
      staged.forEach(candidate -> candidate.freeIfStaged());
      throw failure;
    }
  }

  @Override
  public FontChainResolver fontChainResolver() {
    return requireSemanticOwner().resolver();
  }

  @Override
  public FontSemanticObservation semanticObservation() {
    SemanticFontOwner.Observation observation = requireSemanticOwner().observation();
    return new FontSemanticObservation(
        observation.generation(),
        observation.identities().stream()
            .map(
                identity ->
                    new FontSemanticObservation.Identity(
                        identity.key().family(),
                        identity.key().style(),
                        identity.key().weight(),
                        identity.key().stretch(),
                        identity.normalizedLocator(),
                        identity.byteRevision()))
            .toList());
  }

  @Override
  public FontResourceObservation resourceObservation() {
    if (resourceAggregate() != this) {
      return resourceAggregate().resourceObservation();
    }
    requireSemanticOwner();
    FontStorageImpl.ResourceSnapshot storage = atomicStorage().resourceSnapshot();
    return new FontResourceObservation(
        storage.byteEntries(),
        storage.byteCapacity(),
        fontInfoMap.size(),
        storage.issuedExternalAliasViews(),
        FontResourceObservation.AliasLifetime.JVM_MANAGED_CALLER_RETAINABLE);
  }

  @Override
  public Mutation clear() {
    if (resourceAggregate() != this) {
      return resourceAggregate().clear();
    }
    SemanticFontOwner owner = requireSemanticOwner();
    Mutation mutation = owner.clear();
    if (mutation.outcome() == MutationOutcome.CHANGED) {
      teardownAllRetainedResources(atomicStorage());
    }
    return mutation;
  }

  @Override
  public void close() {
    if (resourceAggregate() != this) {
      resourceAggregate().close();
      return;
    }
    if (semanticOwner == null) {
      throw new IllegalStateException("Font service semantic owner is not installed");
    }
    SemanticFontOwner owner = semanticOwner;
    if (!owner.prepareResourceClose()) {
      teardownAllRetainedResources(atomicStorage());
      Font.releaseSemanticOwner(owner, this);
      return;
    }
    teardownAllRetainedResources(atomicStorage());
    owner.completeCloseAfterResourceTeardown();
    Font.releaseSemanticOwner(owner, this);
  }

  /** {@inheritDoc} */
  @Override
  public Font loadFont(String path) throws FontLoadingException {
    if (resourceAggregate() != this) {
      return resourceAggregate().loadFont(path);
    }
    SemanticFontOwner owner = requireSemanticOwner();
    FontStorageImpl storage = atomicStorage();
    List<Font> registeredBefore = owner.registeredFonts();
    String normalizedLocator = SemanticFontOwner.normalizeLocator(path);
    String sourcePath =
        owner.registeredFonts().stream()
            .map(Font::path)
            .filter(
                registeredPath ->
                    SemanticFontOwner.normalizeLocator(registeredPath).equals(normalizedLocator))
            .findFirst()
            .orElse(path);
    AtomicReference<ByteBuffer> stagedBytes = new AtomicReference<>();
    AtomicReference<OwnedFontInfo> stagedInfo = new AtomicReference<>();
    AtomicReference<Font> stagedDescriptor = new AtomicReference<>();
    Mutation mutation;
    try {
      mutation =
          owner.load(
              new FontLoadRequest(
                  path,
                  () -> {
                    ByteBuffer bytes = storage.stageFontData(sourcePath);
                    stagedBytes.set(bytes);
                    return bytes;
                  },
                  bytes -> {
                    OwnedFontInfo info = createOwnedFontInfo(sourcePath, bytes);
                    stagedInfo.set(info);
                    try {
                      Font descriptor = parseFontDescriptor(sourcePath, info.value());
                      transactionFailureInjector.before(TransactionStage.AFTER_DESCRIPTOR_PARSE);
                      info.discardStagedBorrowedViews();
                      stagedDescriptor.set(descriptor);
                      return descriptor;
                    } catch (RuntimeException | Error failure) {
                      info.freeStaged();
                      throw failure;
                    }
                  }),
              provisional ->
                  publishLoadedResources(
                      storage,
                      registeredBefore,
                      stagedBytes,
                      stagedInfo,
                      stagedDescriptor,
                      provisional));
    } catch (RuntimeException | Error failure) {
      OwnedFontInfo rejected = stagedInfo.get();
      if (rejected != null) {
        rejected.freeStaged();
      }
      throw failure;
    }
    if (mutation.outcome() == MutationOutcome.REJECTED) {
      OwnedFontInfo rejected = stagedInfo.get();
      if (rejected != null) {
        rejected.freeStaged();
      }
      throw new FontLoadingException("Failed to load font from '%s'".formatted(path));
    }

    Font parsed = Objects.requireNonNull(stagedDescriptor.get());
    FaceKey loadedFace = faceKey(parsed);
    Font canonical =
        owner.registeredFonts().stream()
            .filter(registered -> faceKey(registered).equals(loadedFace))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Published font descriptor is unavailable"));
    return canonical;
  }

  private void publishLoadedResources(
      FontStorageImpl storage,
      List<Font> registeredBefore,
      AtomicReference<ByteBuffer> stagedBytes,
      AtomicReference<OwnedFontInfo> stagedInfo,
      AtomicReference<Font> stagedDescriptor,
      Mutation mutation) {
    OwnedFontInfo preparedInfo = stagedInfo.get();
    if (mutation.outcome() == MutationOutcome.REJECTED || preparedInfo == null) {
      return;
    }
    if (mutation.outcome() == MutationOutcome.UNCHANGED) {
      preparedInfo.freeStaged();
      return;
    }

    Font parsed = Objects.requireNonNull(stagedDescriptor.get());
    Font previous =
        registeredBefore.stream()
            .filter(registered -> faceKey(registered).equals(faceKey(parsed)))
            .findFirst()
            .orElse(null);
    String newLocator = SemanticFontOwner.normalizeLocator(parsed.path());
    String previousLocator =
        previous == null ? null : SemanticFontOwner.normalizeLocator(previous.path());
    ByteBuffer previousBytesAtNewLocator = storage.ownedFontData(newLocator);
    OwnedFontInfo previousInfoAtNewLocator = fontInfoMap.get(newLocator);
    ByteBuffer previousFaceBytes =
        previousLocator == null ? null : storage.ownedFontData(previousLocator);
    OwnedFontInfo previousFaceInfo =
        previousLocator == null ? null : fontInfoMap.get(previousLocator);

    try {
      transactionFailureInjector.before(TransactionStage.BEFORE_BYTE_CACHE_COMMIT);
      storage.commitFontData(newLocator, Objects.requireNonNull(stagedBytes.get()));
      transactionFailureInjector.before(TransactionStage.BEFORE_INFO_MAP_PUT);
      fontInfoMap.put(newLocator, preparedInfo);
      transactionFailureInjector.before(TransactionStage.BEFORE_RESOURCE_TRANSFER);
      preparedInfo.transferAfterPublication();
      if (previous != null) {
        retirePreviousAfterReplacement(
            storage,
            previousLocator,
            newLocator,
            previousFaceInfo);
      }
    } catch (RuntimeException | Error failure) {
      restoreInfo(fontInfoMap, newLocator, previousInfoAtNewLocator);
      storage.restoreFontData(newLocator, previousBytesAtNewLocator);
      if (previousLocator != null && !previousLocator.equals(newLocator)) {
        restoreInfo(fontInfoMap, previousLocator, previousFaceInfo);
        storage.restoreFontData(previousLocator, previousFaceBytes);
      }
      preparedInfo.freeAfterFailedPublication();
      throw failure;
    }
  }

  @SuppressWarnings("squid:S3776")
  private Font parseFontDescriptor(String path, STBTTFontinfo fontInfo) {
    String fontFamily = getFontFamily(fontInfo);
    String subfamily = getSubfamily(fontInfo);

    // split subfamily by capital letter and trim spaces
    String[] fontFeatures = subfamily.split(SUBINDEX_SPLIT_REGEX);

    FontStyle fontStyle = FontStyle.NORMAL;
    FontWeight fontWeight = FontWeight.NORMAL;
    FontStretch fontStretch = FontStretch.NORMAL;
    for (String f : fontFeatures) {
      String fontFeature = f.trim();
      if (FontStyle.contains(fontFeature)) {
        fontStyle = FontStyle.find(fontFeature);
      } else if (FontStretch.contains(fontFeature)) {
        fontStretch = FontStretch.find(fontFeature);
      } else if (FontWeight.contains(fontFeature)) {
        fontWeight = FontWeight.find(fontFeature);
      } else {
        String[] subFeatures = fontFeature.split(SUBFEATURE_SPLIT_REGEX);
        for (String sf : subFeatures) {
          String sff = sf.trim();
          if (FontStyle.contains(sff)) {
            fontStyle = FontStyle.find(sff);
          } else if (FontStretch.contains(sff)) {
            fontStretch = FontStretch.find(sff);
          } else if (FontWeight.contains(sff)) {
            fontWeight = FontWeight.find(sff);
          }
        }
      }
    }

    if (LOG.isInfoEnabled()) {
      LOG.info(
          "Font [ {} | {} ] loaded successfully from '{}'",
          fontFamily,
          Arrays.toString(fontFeatures),
          path);
    }
    return new Font(fontFamily, fontStyle, fontStretch, fontWeight, path);
  }

  @Override
  public boolean isFontAvailable(@NonNull Font font) {
    if (resourceAggregate() != this) {
      return resourceAggregate().isFontAvailable(font);
    }
    if (fontStorage instanceof FontStorageImpl) {
      SemanticFontOwner owner = requireSemanticOwner();
      String locator = SemanticFontOwner.normalizeLocator(font.path());
      FaceKey requestedFace = faceKey(font);
      return fontInfoMap.containsKey(locator)
          && owner.registeredFonts().stream()
              .anyMatch(
                  registered ->
                      faceKey(registered).equals(requestedFace)
                          && SemanticFontOwner.normalizeLocator(registered.path()).equals(locator));
    }
    return fontInfoMap.containsKey(font.path());
  }

  private String getSubfamily(STBTTFontinfo fontInfo) {
    String typographicSubfamily = getInfo(fontInfo, TYPOGRAPHIC_FONT_SUBFAMILY_INDEX);
    return typographicSubfamily.isBlank()
        ? getInfo(fontInfo, FONT_SUBFAMILY_INDEX)
        : typographicSubfamily;
  }

  private String getFontFamily(STBTTFontinfo fontInfo) {
    String typographicFontFamily = getInfo(fontInfo, TYPOGRAPHIC_FONT_FAMILY_INDEX);
    return typographicFontFamily.isBlank()
        ? getInfo(fontInfo, FONT_FAMILY_INDEX)
        : typographicFontFamily;
  }

  private FontRequest builtInRequest(
      FontStorageImpl storage, StagedBuiltIn candidate, boolean finalCandidate) {
    return FontRequest.from(
        candidate.font(),
        () -> {
          ByteBuffer bytes = storage.stageFontData(candidate.font().path());
          candidate.bytes().set(bytes);
          return bytes;
        },
        bytes -> {
          OwnedFontInfo info = createOwnedFontInfo(candidate.font().path(), bytes);
          candidate.info().set(info);
          try {
            transactionFailureInjector.before(TransactionStage.AFTER_DESCRIPTOR_PARSE);
            info.discardStagedBorrowedViews();
            if (finalCandidate) {
              verifyStagedPublicationPreconditions(true);
            }
          } catch (RuntimeException | Error failure) {
            info.freeStaged();
            throw failure;
          }
        },
        (request, bytes) -> {});
  }

  private static FaceKey faceKey(Font font) {
    return new FaceKey(
        font.fontFamily(),
        font.style().name(),
        font.weight().name(),
        font.stretch().name());
  }

  private SemanticFontOwner requireSemanticOwner() {
    if (semanticOwner == null) {
      throw new IllegalStateException("Font service semantic owner is not installed");
    }
    SemanticFontOwner installed = Font.semanticOwner();
    if (installed != semanticOwner) {
      throw new IllegalStateException("Font service is not attached to the production semantic owner");
    }
    return installed;
  }

  void verifyPublicStorageRead(FontStorageImpl storage, SemanticFontOwner expectedOwner) {
    FontServiceImpl aggregate = resourceAggregate();
    if (aggregate != this) {
      aggregate.verifyPublicStorageRead(storage, expectedOwner);
      return;
    }
    if (fontStorage != storage || semanticOwner != expectedOwner) {
      throw new IllegalStateException(
          "Font storage is not owned by the installed semantic service aggregate");
    }
    if (requireSemanticOwner() != expectedOwner || Font.semanticService() != this) {
      throw new IllegalStateException(
          "Font storage semantic owner/service aggregate is no longer installed");
    }
  }

  private FontStorageImpl atomicStorage() {
    if (fontStorage instanceof FontStorageImpl storage) {
      return storage;
    }
    throw new IllegalStateException(
        "Semantic font mutation requires FontStorageImpl staged publication");
  }

  private FontServiceImpl resourceAggregate() {
    return aggregateService == null ? this : aggregateService;
  }

  private void verifyStagedPublicationPreconditions(boolean includesOwnerBinding) {
    transactionFailureInjector.before(TransactionStage.BEFORE_CACHE_PUBLICATION);
    if (includesOwnerBinding) {
      transactionFailureInjector.before(TransactionStage.BEFORE_OWNER_BINDING);
    }
    transactionFailureInjector.before(TransactionStage.BEFORE_RESOURCE_TRANSFER);
  }

  private void retirePreviousAfterReplacement(
      FontStorageImpl storage,
      String previousLocator,
      String newLocator,
      OwnedFontInfo previousInfo) {
    boolean sameLocator = previousLocator.equals(newLocator);
    transactionFailureInjector.before(TransactionStage.BEFORE_RETAINED_RETIREMENT);
    recordTeardown(ResourceLifecycleEvent.STOP_USE);
    OwnedFontInfo retained = sameLocator ? previousInfo : fontInfoMap.remove(previousLocator);
    recordTeardown(
        ResourceLifecycleEvent.CLEAR_DEPENDENT_MEASUREMENT_AND_INFO_REFERENCES);
    recordTeardown(
        ResourceLifecycleEvent.DISCARD_RETAINED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS);
    if (retained != null) {
      transactionFailureInjector.before(TransactionStage.BEFORE_RETAINED_INFO_FREE);
      retained.freeRetained();
    }
    recordTeardown(
        ResourceLifecycleEvent.FREE_RETAINED_OWNER_CONTROLLED_STBTT_FONT_INFO);
    if (!sameLocator) {
      storage.retireFontData(previousLocator);
    }
    recordTeardown(ResourceLifecycleEvent.DROP_RETAINED_BYTE_OWNER_REFERENCES);
  }

  private static void restoreInfo(
      Map<String, OwnedFontInfo> infos, String locator, OwnedFontInfo previous) {
    if (previous == null) {
      infos.remove(locator);
    } else {
      infos.put(locator, previous);
    }
  }

  private void teardownAllRetainedResources(FontStorageImpl storage) {
    glyphProbeCache.clear();
    advanceCache.clear();
    kerningCache.clear();
    resolvedPrimitiveCache.clear();
    wrappedLayoutCache.clear();
    if (fontInfoMap.isEmpty() && !storage.hasFontData()) {
      return;
    }
    recordTeardown(ResourceLifecycleEvent.STOP_USE);
    List<OwnedFontInfo> retained = List.copyOf(fontInfoMap.values());
    fontInfoMap.clear();
    recordTeardown(
        ResourceLifecycleEvent.CLEAR_DEPENDENT_MEASUREMENT_AND_INFO_REFERENCES);
    recordTeardown(
        ResourceLifecycleEvent.DISCARD_RETAINED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS);
    retained.forEach(OwnedFontInfo::freeRetained);
    recordTeardown(
        ResourceLifecycleEvent.FREE_RETAINED_OWNER_CONTROLLED_STBTT_FONT_INFO);
    storage.clearFontData();
    recordTeardown(ResourceLifecycleEvent.DROP_RETAINED_BYTE_OWNER_REFERENCES);
  }

  private void recordPreparation(ResourceLifecycleEvent event) {
    try {
      resourceLifecycleObserver.preparation(event);
    } catch (RuntimeException | Error failure) {
      LOG.warn("Font resource preparation observer failed at {}", event, failure);
    }
  }

  private void recordTeardown(ResourceLifecycleEvent event) {
    try {
      resourceLifecycleObserver.teardown(event);
    } catch (RuntimeException | Error failure) {
      LOG.warn("Font resource teardown observer failed at {}", event, failure);
    }
  }

  /** {@inheritDoc} */
  @Override
  public TextMetrics measureText(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    diagnostics.increment(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES);
    return measureText(text, 0, List.of(font), fontSize, lineHeight, Float.MAX_VALUE, false);
  }

  /** {@inheritDoc} */
  @Override
  public TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    diagnostics.increment(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES);
    return measureText(text, offsetX, List.of(font), fontSize, lineHeight, maxWidth, wordWrap);
  }

  /**
   * {@inheritDoc}
   *
   * <p><strong>Implementation:</strong> This override considers the full list in order when
   * resolving source glyphs, rather than using the interface default's first-font compatibility
   * behavior.
   */
  @Override
  public TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES);
    return completeMeasurement(
            new PreparedRange(
                text,
                0,
                text.length(),
                offsetX,
                fonts,
                fontSize,
                lineHeight,
                maxWidth,
                wordWrap))
        .metrics();
  }

  @Override
  public ResolvedMeasurement measureRange(
      String source,
      int start,
      int end,
      float offsetX,
      List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    return completeMeasurement(
        new PreparedRange(
            source, start, end, offsetX, fonts, fontSize, lineHeight, maxWidth, wordWrap));
  }

  /** {@inheritDoc} */
  @Override
  public TextMetrics getTextMetrics(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    diagnostics.increment(TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_METRICS_FONT_ENTRIES);
    return measureText(text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
  }

  @Override
  public FontMetrics getFontMetrics(@NonNull Font font, float fontSize, float lineHeight) {
    return measureText("", font, fontSize, lineHeight).fontMetrics();
  }

  @Override
  public TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_ENTRIES);
    return measureText(text, font, fontSize, lineHeight).lines().get(0);
  }

  @Override
  public TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float offsetX) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES);
    return getTextCaretMetrics(text, List.of(font), fontSize, offsetX);
  }

  @Override
  public TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float offsetX) {
    diagnostics.increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES);
    ResolvedMeasurement resolved =
        completeMeasurement(
            new PreparedRange(
                text,
                0,
                text.length(),
                0,
                fonts,
                fontSize,
                1,
                Float.POSITIVE_INFINITY,
                false));
    return resolved.lineCaretStops().getFirst().caretAt(offsetX, diagnostics);
  }

  private FontMetrics measureFontMetrics(STBTTFontinfo fontInfo, float fontSize, float lineHeight) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer ascent = stack.mallocInt(1);
      IntBuffer descent = stack.mallocInt(1);
      IntBuffer lineGap = stack.mallocInt(1);
      stbtt_GetFontVMetrics(fontInfo, ascent, descent, lineGap);

      float scaleFactor = stbtt_ScaleForMappingEmToPixels(fontInfo, fontSize);
      float requestedLineHeight = fontSize * lineHeight;
      float metricsAscent = ascent.get(0) * scaleFactor;
      float metricsDescent = Math.abs(descent.get(0) * scaleFactor);
      float metricsLineGap = Math.max(0, lineGap.get(0) * scaleFactor);
      float measuredLineHeight = Math.max(requestedLineHeight, metricsAscent + metricsDescent + metricsLineGap);
      if (roundToPixel) {
        metricsAscent = Math.round(metricsAscent);
        metricsDescent = Math.round(metricsDescent);
        metricsLineGap = Math.round(metricsLineGap);
        measuredLineHeight = Math.round(measuredLineHeight);
      }
      return new FontMetrics(
          metricsAscent, metricsDescent, metricsLineGap, measuredLineHeight, metricsAscent);
    }
  }

  private GlyphMeasurement resolveGlyph(
      List<ResolvedFont> fonts, int codePoint, long generation) {
    diagnostics.increment(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS);
    for (ResolvedFont candidate : fonts) {
      STBTTFontinfo fontInfo = candidate.fontInfo();
      int glyphIndex;
      if (cacheConfiguration.enabled()) {
        GlyphProbeKey key = glyphProbeKey(candidate.locator(), generation, codePoint);
        Integer cached = glyphProbeCache.get(key);
        glyphIndex = cached == null ? -1 : cached;
        if (cached == null) {
          diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES);
          glyphIndex = stbtt_FindGlyphIndex(fontInfo, codePoint);
          glyphProbeCache.put(key, glyphIndex);
        }
      } else {
        diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES);
        glyphIndex = stbtt_FindGlyphIndex(fontInfo, codePoint);
      }
      if (glyphIndex != 0) {
        return new GlyphMeasurement(
            candidate.font(), candidate.locator(), fontInfo, glyphIndex, codePoint, false);
      }
    }

    // The replacement character is visible in the bundled fallback instead of becoming blank.
    for (ResolvedFont candidate : fonts) {
      STBTTFontinfo fontInfo = candidate.fontInfo();
      int glyphIndex;
      if (cacheConfiguration.enabled()) {
        GlyphProbeKey key =
            glyphProbeKey(candidate.locator(), generation, REPLACEMENT_CODE_POINT);
        Integer cached = glyphProbeCache.get(key);
        glyphIndex = cached == null ? -1 : cached;
        if (cached == null) {
          diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES);
          glyphIndex = stbtt_FindGlyphIndex(fontInfo, REPLACEMENT_CODE_POINT);
          glyphProbeCache.put(key, glyphIndex);
        }
      } else {
        diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES);
        glyphIndex = stbtt_FindGlyphIndex(fontInfo, REPLACEMENT_CODE_POINT);
      }
      if (glyphIndex != 0) {
        return new GlyphMeasurement(
            candidate.font(),
            candidate.locator(),
            fontInfo,
            glyphIndex,
            REPLACEMENT_CODE_POINT,
            true);
      }
    }
    ResolvedFont fallback = fonts.get(0);
    return new GlyphMeasurement(
        fallback.font(),
        fallback.locator(),
        fallback.fontInfo(),
        0,
        REPLACEMENT_CODE_POINT,
        true);
  }

  private PairKerningMeasurement measurePairKerning(
      GlyphMeasurement glyph, GlyphMeasurement previousGlyph, float fontSize, long generation) {
    if (previousGlyph == null) {
      return new PairKerningMeasurement(null, -1, 0, 0);
    }
    if (previousGlyph.fontInfo() != glyph.fontInfo()) {
      return new PairKerningMeasurement(previousGlyph.fontInfo(), previousGlyph.glyphIndex(), 0, 0);
    }

    KerningKey key = null;
    if (cacheConfiguration.enabled()) {
      key =
          new KerningKey(
              glyph.locator(),
              generation,
              previousGlyph.glyphIndex(),
              glyph.glyphIndex(),
              Float.floatToIntBits(fontSize));
      KerningValue cached = kerningCache.get(key);
      if (cached != null) {
        return new PairKerningMeasurement(
            previousGlyph.fontInfo(),
            previousGlyph.glyphIndex(),
            cached.rawAdvance(),
            cached.advance());
      }
    }
    diagnostics.increment(TextDiagnosticCounter.NATIVE_KERNING_CALLS);
    int rawAdvance =
        stbtt_GetGlyphKernAdvance(
            glyph.fontInfo(), previousGlyph.glyphIndex(), glyph.glyphIndex());
    float scaleFactor = stbtt_ScaleForMappingEmToPixels(glyph.fontInfo(), fontSize);
    float advance = (int) (rawAdvance * scaleFactor + 0.5f);
    if (key != null) {
      kerningCache.put(key, new KerningValue(rawAdvance, advance));
    }
    return new PairKerningMeasurement(
        previousGlyph.fontInfo(), previousGlyph.glyphIndex(), rawAdvance, advance);
  }

  private BaseAdvanceMeasurement measureBaseAdvance(
      GlyphMeasurement glyph, IntBuffer pAdvance, float fontSize, long generation) {
    AdvanceKey key = null;
    if (cacheConfiguration.enabled()) {
      key =
          new AdvanceKey(
              glyph.locator(),
              generation,
              glyph.glyphIndex(),
              Float.floatToIntBits(fontSize));
      BaseAdvanceMeasurement cached = advanceCache.get(key);
      if (cached != null) {
        return cached;
      }
    }
    diagnostics.increment(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS);
    stbtt_GetGlyphHMetrics(glyph.fontInfo(), glyph.glyphIndex(), pAdvance, null);
    int rawAdvance = pAdvance.get(0);
    float scaleFactor = stbtt_ScaleForMappingEmToPixels(glyph.fontInfo(), fontSize);
    short scaledTenths = (short) (scaleFactor * rawAdvance * 10.0f);
    float advance = (int) (scaledTenths / 10.0f + 0.5f);
    BaseAdvanceMeasurement result = new BaseAdvanceMeasurement(rawAdvance, advance);
    if (key != null) {
      advanceCache.put(key, result);
    }
    return result;
  }

  private GlyphProbeKey glyphProbeKey(String locator, long generation, int codePoint) {
    return new GlyphProbeKey(locator, generation, codePoint);
  }

  // obtains font info from the map or if map has no entry, creates it and adds it to the map
  private String getInfo(STBTTFontinfo stbttFontinfo, int i) {
    String info = "";
    ByteBuffer name =
        stbtt_GetFontNameString(
            stbttFontinfo,
            STBTT_PLATFORM_ID_MICROSOFT,
            STBTT_MS_EID_UNICODE_BMP,
            STBTT_MS_LANG_ENGLISH,
            i);
    if (name != null) {
      int capacity = name.capacity();
      byte[] bytes = new byte[capacity];
      name.get(bytes);
      info = new String(bytes, StandardCharsets.UTF_16);
    }
    return info;
  }

  private STBTTFontinfo getFontInfo(Font font) throws FontLoadingException {
    if (resourceAggregate() != this) {
      return resourceAggregate().getFontInfo(font);
    }
    if (!(fontStorage instanceof FontStorageImpl)) {
      throw new IllegalStateException(
          "Native font measurement requires the installed FontStorageImpl lifecycle aggregate");
    }
    requireSemanticOwner();
    return getFontInfoUnchecked(font, SemanticFontOwner.normalizeLocator(font.path()));
  }

  private STBTTFontinfo getFontInfoUnchecked(Font font, String locator)
      throws FontLoadingException {
    if (resourceAggregate() != this) {
      return resourceAggregate().getFontInfoUnchecked(font, locator);
    }
    if (!(fontStorage instanceof FontStorageImpl)) {
      throw new IllegalStateException(
          "Native font measurement requires the installed FontStorageImpl lifecycle aggregate");
    }
    String fontPath = font.path();
    OwnedFontInfo cached = fontInfoMap.get(locator);
    if (cached != null) {
      return cached.value();
    }
    OwnedFontInfo created = createOwnedFontInfo(fontPath, fontStorage.getFontData(fontPath));
    created.discardStagedBorrowedViews();
    fontInfoMap.put(locator, created);
    created.transferAfterPublication();
    return created.value();
  }

  private OwnedFontInfo createOwnedFontInfo(String fontPath, ByteBuffer fontData)
      throws FontLoadingException {
    OwnedFontInfo owned = allocateOwnedFontInfo();
    try {
      transactionFailureInjector.before(TransactionStage.AFTER_NATIVE_ALLOCATION);
      if (!hasSupportedSfntHeader(fontData)
          || !STBTruetype.stbtt_InitFont(owned.value(), fontData)) {
        throw new FontLoadingException("Failed to load font from '%s'".formatted(fontPath));
      }

      for (int i = 0; i < 25; i++) {
        ByteBuffer name =
            stbtt_GetFontNameString(
                owned.value(),
                STBTT_PLATFORM_ID_MICROSOFT,
                STBTT_MS_EID_UNICODE_BMP,
                STBTT_MS_LANG_ENGLISH,
                i);
        if (name != null) {
          byte[] bytes = new byte[name.capacity()];
          name.get(bytes);
        }
      }
      return owned;
    } catch (RuntimeException | Error failure) {
      owned.freeStaged();
      throw failure;
    }
  }

  private OwnedFontInfo allocateOwnedFontInfo() {
    STBTTFontinfo allocated = STBTTFontinfo.malloc();
    try {
      return new OwnedFontInfo(allocated);
    } catch (RuntimeException | Error failure) {
      allocated.free();
      throw failure;
    }
  }

  private static boolean hasSupportedSfntHeader(ByteBuffer fontData) {
    if (fontData == null || fontData.remaining() < 12) {
      return false;
    }
    int offset = fontData.position();
    int signature =
        Byte.toUnsignedInt(fontData.get(offset)) << 24
            | Byte.toUnsignedInt(fontData.get(offset + 1)) << 16
            | Byte.toUnsignedInt(fontData.get(offset + 2)) << 8
            | Byte.toUnsignedInt(fontData.get(offset + 3));
    return signature == 0x00010000
        || signature == 0x4f54544f
        || signature == 0x74727565
        || signature == 0x74797031
        || signature == 0x74746366;
  }

  enum ResourceLifecycleEvent {
    ALLOCATE_TRANSACTION_OWNED_STBTT_FONT_INFO,
    DISCARD_STAGED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS,
    TRANSFER_STAGED_STBTT_FONT_INFO_AFTER_SUCCESSFUL_PUBLICATION,
    FREE_TRANSACTION_OWNED_STAGED_STBTT_FONT_INFO,
    DROP_STAGED_BYTE_OWNER_REFERENCE,
    FREE_ROLLED_BACK_TRANSFERRED_STBTT_FONT_INFO,
    DROP_ROLLED_BACK_TRANSFERRED_BYTE_OWNER_REFERENCE,
    STOP_USE,
    CLEAR_DEPENDENT_MEASUREMENT_AND_INFO_REFERENCES,
    DISCARD_RETAINED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS,
    FREE_RETAINED_OWNER_CONTROLLED_STBTT_FONT_INFO,
    DROP_RETAINED_BYTE_OWNER_REFERENCES
  }

  static class ResourceLifecycleObserver {
    private static final ResourceLifecycleObserver NO_OP = new ResourceLifecycleObserver();

    void preparation(ResourceLifecycleEvent event) {}

    void teardown(ResourceLifecycleEvent event) {}
  }

  enum TransactionStage {
    AFTER_NATIVE_ALLOCATION,
    AFTER_DESCRIPTOR_PARSE,
    BEFORE_CACHE_PUBLICATION,
    BEFORE_OWNER_BINDING,
    BEFORE_RETAINED_RETIREMENT,
    BEFORE_RETAINED_INFO_FREE,
    BEFORE_BYTE_CACHE_COMMIT,
    BEFORE_INFO_MAP_PUT,
    BEFORE_RESOURCE_TRANSFER
  }

  static class TransactionFailureInjector {
    private static final TransactionFailureInjector NO_OP = new TransactionFailureInjector();

    void before(TransactionStage stage) {}
  }

  private final class OwnedFontInfo {
    private final STBTTFontinfo value;
    private OwnershipState state = OwnershipState.STAGED;
    private boolean stagedBorrowedViewsDiscarded;

    private OwnedFontInfo(STBTTFontinfo value) {
      this.value = value;
      recordPreparation(ResourceLifecycleEvent.ALLOCATE_TRANSACTION_OWNED_STBTT_FONT_INFO);
    }

    private STBTTFontinfo value() {
      if (state == OwnershipState.FREED) {
        throw new IllegalStateException("Owner-controlled STB font info is already freed");
      }
      return value;
    }

    private void discardStagedBorrowedViews() {
      if (state == OwnershipState.STAGED && !stagedBorrowedViewsDiscarded) {
        stagedBorrowedViewsDiscarded = true;
        recordPreparation(
            ResourceLifecycleEvent
                .DISCARD_STAGED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS);
      }
    }

    private void transferAfterPublication() {
      if (state != OwnershipState.STAGED) {
        return;
      }
      discardStagedBorrowedViews();
      state = OwnershipState.RETAINED;
      recordPreparation(
          ResourceLifecycleEvent.TRANSFER_STAGED_STBTT_FONT_INFO_AFTER_SUCCESSFUL_PUBLICATION);
    }

    private void freeStaged() {
      if (state != OwnershipState.STAGED) {
        return;
      }
      discardStagedBorrowedViews();
      value.free();
      state = OwnershipState.FREED;
      recordPreparation(
          ResourceLifecycleEvent.FREE_TRANSACTION_OWNED_STAGED_STBTT_FONT_INFO);
      recordPreparation(ResourceLifecycleEvent.DROP_STAGED_BYTE_OWNER_REFERENCE);
    }

    private void freeRetained() {
      if (state != OwnershipState.RETAINED) {
        return;
      }
      value.free();
      state = OwnershipState.FREED;
    }

    private void freeAfterFailedPublication() {
      if (state == OwnershipState.STAGED) {
        freeStaged();
      } else if (state == OwnershipState.RETAINED) {
        freeRetained();
        recordPreparation(
            ResourceLifecycleEvent.FREE_ROLLED_BACK_TRANSFERRED_STBTT_FONT_INFO);
        recordPreparation(
            ResourceLifecycleEvent.DROP_ROLLED_BACK_TRANSFERRED_BYTE_OWNER_REFERENCE);
      }
    }
  }

  private enum OwnershipState {
    STAGED,
    RETAINED,
    FREED
  }

  private record StagedBuiltIn(
      Font font, AtomicReference<ByteBuffer> bytes, AtomicReference<OwnedFontInfo> info) {
    private StagedBuiltIn(Font font) {
      this(font, new AtomicReference<>(), new AtomicReference<>());
    }

    private void freeIfStaged() {
      OwnedFontInfo staged = info.get();
      if (staged != null) {
        staged.freeStaged();
      }
    }
  }

  /**
   * Resolves width-independent source primitives for later private line and run construction.
   *
   * <p>This seam deliberately does not accept wrap or placement inputs and does not publish public
   * measurement results. CRLF is retained as two scanned code points with paired separator markers
   * so a later materializer can treat the boundary as atomic without losing source evidence.
   */
  ResolvedPrimitiveSequence resolvePrimitives(
      String source, int start, int end, List<Font> fonts, float fontSize) {
    PreparedRange.validateSourceRange(source, start, end);
    Objects.requireNonNull(fonts, "fonts");

    List<Font> normalizedFonts = fonts.isEmpty() ? List.of(Font.DEFAULT) : fonts;
    long generation = requireSemanticOwner().generation();
    List<ResolvedFont> resolvedFonts = new ArrayList<>(normalizedFonts.size());
    for (Font font : normalizedFonts) {
      String locator = SemanticFontOwner.normalizeLocator(font.path());
      resolvedFonts.add(new ResolvedFont(font, locator, getFontInfoUnchecked(font, locator)));
    }
    ResolvedPrimitiveBuilder builder = new ResolvedPrimitiveBuilder(diagnostics, start, end);
    GlyphMeasurement previousGlyph = null;
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pAdvance = stack.mallocInt(1);
      for (int sourceStart = start; sourceStart < end; ) {
        diagnostics.increment(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED);
        char first = source.charAt(sourceStart);
        int sourceCodePoint = first;
        int sourceEnd = sourceStart + 1;
        if (Character.isHighSurrogate(first) && sourceEnd < end) {
          char second = source.charAt(sourceEnd);
          if (Character.isLowSurrogate(second)) {
            sourceCodePoint = Character.toCodePoint(first, second);
            sourceEnd++;
          }
        }
        SeparatorKind separatorKind =
            separatorKind(source, start, end, sourceStart, sourceCodePoint);
        if (separatorKind != SeparatorKind.NONE) {
          builder.append(
              ResolvedPrimitive.separator(
                  sourceStart, sourceEnd, sourceCodePoint, separatorKind));
          previousGlyph = null;
          sourceStart = sourceEnd;
          continue;
        }

        GlyphMeasurement glyph = resolveGlyph(resolvedFonts, sourceCodePoint, generation);
        PairKerningMeasurement kerning =
            measurePairKerning(glyph, previousGlyph, fontSize, generation);
        BaseAdvanceMeasurement base = measureBaseAdvance(glyph, pAdvance, fontSize, generation);
        builder.append(
            ResolvedPrimitive.glyph(
                sourceStart,
                sourceEnd,
                sourceCodePoint,
                glyph.renderedCodePoint(),
                glyph.font(),
                glyph.fontInfo(),
                glyph.glyphIndex(),
                glyph.replacement(),
                base.rawAdvance(),
                base.advance(),
                kerning.previousFontInfo(),
                kerning.previousGlyphIndex(),
                kerning.rawAdvance(),
                kerning.advance()));
        previousGlyph = glyph;
        sourceStart = sourceEnd;
      }
    }
    return builder.freeze();
  }

  PrivatePreparedMeasurement preparePrivateMeasurement(
      String source,
      int start,
      int end,
      float offsetX,
      List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    return prepareRange(
        new PreparedRange(
            source,
            start,
            end,
            offsetX,
            fonts,
            fontSize,
            lineHeight,
            maxWidth,
            wordWrap));
  }

  PrivatePreparedMeasurement prepareRange(PreparedRange request) {
    Objects.requireNonNull(request, "request");
    diagnostics.increment(TextDiagnosticCounter.RANGE_PREPARATIONS);
    List<Font> normalizedFonts =
        request.fonts().isEmpty() ? List.of(Font.DEFAULT) : request.fonts();
    Font primaryFont = normalizedFonts.get(0);
    FontMetrics fontMetrics =
        measureFontMetrics(getFontInfo(primaryFont), request.fontSize(), request.lineHeight());
    ResolvedPrimitiveSequence sequence;
    if (cacheConfiguration.enabled()) {
      ResolvedPrimitiveKey primitiveKey = primitiveCacheKey(request);
      sequence = resolvedPrimitiveCache.get(primitiveKey);
      if (sequence == null) {
        sequence =
            resolvePrimitives(
                request.source(),
                request.start(),
                request.end(),
                normalizedFonts,
                request.fontSize());
        resolvedPrimitiveCache.put(primitiveKey, sequence);
      }
    } else {
      sequence =
          resolvePrimitives(
              request.source(),
              request.start(),
              request.end(),
              normalizedFonts,
              request.fontSize());
    }
    return new PrivateResultBuilder(
            diagnostics,
            sequence,
            request,
            primaryFont,
            fontMetrics)
        .freeze();
  }

  private ResolvedMeasurement completeMeasurement(PreparedRange request) {
    diagnostics.increment(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS);
    if (!cacheConfiguration.enabled()) {
      return new FinalMeasurementMaterializer(diagnostics, prepareRange(request)).materialize();
    }
    ResolvedPrimitiveKey primitiveKey = primitiveCacheKey(request);
    WrappedLayoutKey layoutKey =
        new WrappedLayoutKey(
            primitiveKey,
            request.maxWidth(),
            request.offsetX(),
            Float.floatToIntBits(request.lineHeight()) + ":" + Float.floatToIntBits(request.fontSize()),
            request.wordWrap() ? "word-wrap" : "no-wrap",
            "m2-default");
    // Keep both calculation-family lookups observable even when an exact final layout is warm.
    resolvedPrimitiveCache.get(primitiveKey);
    ResolvedMeasurement cached = wrappedLayoutCache.get(layoutKey);
    if (cached != null) return cached;
    ResolvedMeasurement measured =
        new FinalMeasurementMaterializer(diagnostics, prepareRange(request)).materialize();
    wrappedLayoutCache.put(layoutKey, measured);
    return measured;
  }

  private ResolvedPrimitiveKey primitiveCacheKey(PreparedRange request) {
    // Preserve the existing custom-storage boundary before cache-key generation can observe the
    // semantic owner; custom storage is intentionally rejected before any native read/allocation.
    if (!(fontStorage instanceof FontStorageImpl)) {
      atomicStorage();
    }
    long generation = requireSemanticOwner().generation();
    List<String> fonts =
        request.fonts().stream()
            .map(
                font ->
                    SemanticFontOwner.normalizeLocator(font.path()) + "@" + generation)
            .toList();
    return new ResolvedPrimitiveKey(
        request.start() + ":" + request.end() + ":" + request.source(),
        fonts,
        request.fontSize(),
        roundToPixel ? "round-to-pixel" : "fractional",
        "m2-resolution");
  }

  private static long primitiveWeight(ResolvedPrimitiveSequence sequence) {
    return Math.max(1L, sequence.primitives().size() * 96L);
  }

  private static long measurementWeight(ResolvedMeasurement measurement) {
    return Math.max(1L, measurement.metrics().lines().size() * 64L
        + measurement.lineCaretStops().stream().mapToLong(stops -> stops.size() * 8L).sum());
  }

  private SeparatorKind separatorKind(
      String source, int start, int end, int sourceStart, int sourceCodePoint) {
    if (sourceCodePoint == '\r') {
      return sourceStart + 1 < end && source.charAt(sourceStart + 1) == '\n'
          ? SeparatorKind.CRLF_START
          : SeparatorKind.CR;
    }
    if (sourceCodePoint == '\n') {
      return sourceStart > start && source.charAt(sourceStart - 1) == '\r'
          ? SeparatorKind.CRLF_END
          : SeparatorKind.LF;
    }
    return SeparatorKind.NONE;
  }

  enum SeparatorKind {
    NONE,
    LF,
    CR,
    CRLF_START,
    CRLF_END
  }

  record ResolvedPrimitive(
      int sourceStart,
      int sourceEnd,
      int sourceCodePoint,
      int renderedCodePoint,
      Font font,
      STBTTFontinfo fontInfo,
      int glyphIndex,
      boolean replacement,
      int rawBaseAdvance,
      float baseAdvance,
      STBTTFontinfo previousFontInfo,
      int previousGlyphIndex,
      int rawPairKerningAdvance,
      float pairKerningAdvance,
      SeparatorKind separatorKind) {
    private static ResolvedPrimitive glyph(
        int sourceStart,
        int sourceEnd,
        int sourceCodePoint,
        int renderedCodePoint,
        Font font,
        STBTTFontinfo fontInfo,
        int glyphIndex,
        boolean replacement,
        int rawBaseAdvance,
        float baseAdvance,
        STBTTFontinfo previousFontInfo,
        int previousGlyphIndex,
        int rawPairKerningAdvance,
        float pairKerningAdvance) {
      return new ResolvedPrimitive(
          sourceStart,
          sourceEnd,
          sourceCodePoint,
          renderedCodePoint,
          font,
          fontInfo,
          glyphIndex,
          replacement,
          rawBaseAdvance,
          baseAdvance,
          previousFontInfo,
          previousGlyphIndex,
          rawPairKerningAdvance,
          pairKerningAdvance,
          SeparatorKind.NONE);
    }

    private static ResolvedPrimitive separator(
        int sourceStart, int sourceEnd, int sourceCodePoint, SeparatorKind separatorKind) {
      return new ResolvedPrimitive(
          sourceStart,
          sourceEnd,
          sourceCodePoint,
          sourceCodePoint,
          null,
          null,
          0,
          false,
          0,
          0,
          null,
          -1,
          0,
          0,
          separatorKind);
    }

    boolean separator() {
      return separatorKind != SeparatorKind.NONE;
    }
  }

  record PrivateRunRange(
      int primitiveStart,
      int primitiveEnd,
      int sourceStart,
      int sourceEnd,
      Font font,
      STBTTFontinfo fontInfo,
      int glyphCount) {}

  static final class ResolvedPrimitiveSequence {
    private final int sourceStart;
    private final int sourceEnd;
    private final List<ResolvedPrimitive> primitives;
    private final List<PrivateRunRange> runRanges;

    private ResolvedPrimitiveSequence(
        int sourceStart,
        int sourceEnd,
        List<ResolvedPrimitive> primitives,
        List<PrivateRunRange> runRanges) {
      this.sourceStart = sourceStart;
      this.sourceEnd = sourceEnd;
      this.primitives = primitives;
      this.runRanges = runRanges;
    }

    int sourceStart() {
      return sourceStart;
    }

    int sourceEnd() {
      return sourceEnd;
    }

    List<ResolvedPrimitive> primitives() {
      return primitives;
    }

    List<PrivateRunRange> runRanges() {
      return runRanges;
    }
  }

  record PrivatePreWrapLine(
      int primitiveStart,
      int primitiveEnd,
      int runRangeStart,
      int runRangeEnd,
      int sourceStart,
      int sourceEnd,
      int charCount,
      List<Integer> caretBoundaries,
      List<Float> rawAdvanceSlots,
      List<Float> rebasedAdvanceSlots,
      float textAdvance,
      float width,
      float height,
      float baseline,
      FontMetrics fontMetrics) {}

  private record PrivateWrapRange(
      int primitiveStart,
      int primitiveEnd,
      int sourceStart,
      int sourceEnd,
      float initialOffset,
      float plannedWidth) {}

  static final class PrivatePreparedMeasurement {
    private final ResolvedPrimitiveSequence sequence;
    private final List<PrivatePreWrapLine> lines;
    private final PreparedRange request;
    private final Font primaryFont;
    private final FontMetrics fontMetrics;
    private final float width;
    private final float height;
    private final boolean alreadyFinal;

    private PrivatePreparedMeasurement(
        ResolvedPrimitiveSequence sequence,
        List<PrivatePreWrapLine> lines,
        PreparedRange request,
        Font primaryFont,
        FontMetrics fontMetrics,
        float width,
        float height,
        boolean alreadyFinal) {
      this.sequence = sequence;
      this.lines = lines;
      this.request = request;
      this.primaryFont = primaryFont;
      this.fontMetrics = fontMetrics;
      this.width = width;
      this.height = height;
      this.alreadyFinal = alreadyFinal;
    }

    ResolvedPrimitiveSequence sequence() {
      return sequence;
    }

    List<PrivatePreWrapLine> lines() {
      return lines;
    }

    PreparedRange request() {
      return request;
    }

    Font primaryFont() {
      return primaryFont;
    }

    FontMetrics fontMetrics() {
      return fontMetrics;
    }

    float offsetX() {
      return request.offsetX();
    }

    float maxWidth() {
      return request.maxWidth();
    }

    boolean wordWrap() {
      return request.wordWrap();
    }

    float width() {
      return width;
    }

    float height() {
      return height;
    }

    boolean alreadyFinal() {
      return alreadyFinal;
    }
  }

  private static final class PrivateResultBuilder {
    private final DiagnosticSession diagnostics;
    private final ResolvedPrimitiveSequence sequence;
    private final PreparedRange request;
    private final Font primaryFont;
    private final FontMetrics fontMetrics;
    private final List<PrivatePreWrapLine> lines = new ArrayList<>();
    private int runRangeStartCursor;
    private int runRangeEndCursor;
    private float width;
    private boolean frozen;

    private PrivateResultBuilder(
        DiagnosticSession diagnostics,
        ResolvedPrimitiveSequence sequence,
        PreparedRange request,
        Font primaryFont,
        FontMetrics fontMetrics) {
      this.diagnostics = diagnostics;
      this.sequence = sequence;
      this.request = request;
      this.primaryFont = primaryFont;
      this.fontMetrics = fontMetrics;
    }

    private PrivatePreparedMeasurement freeze() {
      requireMutable();
      List<PrivateWrapRange> wrapRanges =
          new PrivateWrapPlanner(diagnostics, sequence, request).plan();
      for (PrivateWrapRange range : wrapRanges) {
        appendLine(range);
      }

      frozen = true;
      diagnostics.increment(TextDiagnosticCounter.RESULT_BUILDER_FREEZES);
      float height = lines.size() * fontMetrics.lineHeight();
      boolean alreadyFinal =
          lines.stream().allMatch(candidate -> candidate.width() <= request.maxWidth());
      return new PrivatePreparedMeasurement(
          sequence,
          List.copyOf(lines),
          request,
          primaryFont,
          fontMetrics,
          width,
          height,
          alreadyFinal);
    }

    private void appendLine(PrivateWrapRange range) {
      List<PrivateRunRange> runRanges = sequence.runRanges();
      while (runRangeStartCursor < runRanges.size()
          && runRanges.get(runRangeStartCursor).primitiveEnd() <= range.primitiveStart()) {
        runRangeStartCursor++;
      }
      runRangeEndCursor = Math.max(runRangeEndCursor, runRangeStartCursor);
      while (runRangeEndCursor < runRanges.size()
          && runRanges.get(runRangeEndCursor).primitiveStart() < range.primitiveEnd()) {
        runRangeEndCursor++;
      }

      PrivateLineBuilder line =
          new PrivateLineBuilder(
              diagnostics,
              range.primitiveStart(),
              runRangeStartCursor,
              range.sourceStart(),
              range.initialOffset(),
              fontMetrics);
      List<ResolvedPrimitive> primitives = sequence.primitives();
      for (int primitiveIndex = range.primitiveStart();
          primitiveIndex < range.primitiveEnd();
          primitiveIndex++) {
        ResolvedPrimitive primitive = primitives.get(primitiveIndex);
        if (primitive.separator()) {
          throw new IllegalStateException("Wrapped line range contains a separator primitive");
        }
        line.append(primitive);
      }
      PrivatePreWrapLine frozenLine =
          line.freeze(range.primitiveEnd(), runRangeEndCursor, range.sourceEnd());
      if (Float.compare(frozenLine.width(), range.plannedWidth()) != 0) {
        throw new IllegalStateException(
            "Wrap planning and private line materialization produced different widths");
      }
      lines.add(frozenLine);
      diagnostics.increment(TextDiagnosticCounter.LINE_BUILDER_APPENDS);
      width = Math.max(width, frozenLine.width());
    }

    private void requireMutable() {
      if (frozen) {
        throw new IllegalStateException("Private result builder is already frozen");
      }
    }
  }

  /**
   * Selects private line ranges in one forward pass over already-resolved primitives.
   *
   * <p>A word candidate retains only its boundary and the accumulated deferred suffix state. No
   * primitive is removed from or reinserted into the resolved sequence, and the source cursor never
   * moves backward when a candidate is accepted.
   */
  private static final class PrivateWrapPlanner {
    private final DiagnosticSession diagnostics;
    private final ResolvedPrimitiveSequence sequence;
    private final PreparedRange request;
    private final List<PrivateWrapRange> ranges = new ArrayList<>();

    private PrivateWrapPlanner(
        DiagnosticSession diagnostics,
        ResolvedPrimitiveSequence sequence,
        PreparedRange request) {
      this.diagnostics = diagnostics;
      this.sequence = sequence;
      this.request = request;
    }

    private List<PrivateWrapRange> plan() {
      List<ResolvedPrimitive> primitives = sequence.primitives();
      int primitiveIndex = 0;
      int linePrimitiveStart = 0;
      int lineSourceStart = sequence.sourceStart();
      int lineGlyphCount = 0;
      float lineOffset = request.offsetX();
      float lineTextAdvance = 0;
      WordBoundaryCandidate wordCandidate = null;
      boolean trailingHardSeparator = false;

      while (primitiveIndex < primitives.size()) {
        ResolvedPrimitive primitive = primitives.get(primitiveIndex);
        diagnostics.increment(TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS);
        if (primitive.separator()) {
          if (primitive.separatorKind() == SeparatorKind.CRLF_END) {
            throw new IllegalStateException("CRLF end primitive has no matching CRLF start");
          }
          addRange(
              linePrimitiveStart,
              primitiveIndex,
              lineSourceStart,
              primitive.sourceStart(),
              lineOffset,
              lineTextAdvance);
          if (primitive.separatorKind() == SeparatorKind.CRLF_START) {
            int endIndex = primitiveIndex + 1;
            if (endIndex >= primitives.size()
                || primitives.get(endIndex).separatorKind() != SeparatorKind.CRLF_END) {
              throw new IllegalStateException("CRLF start primitive has no matching CRLF end");
            }
            diagnostics.increment(TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS);
            primitiveIndex = endIndex + 1;
            lineSourceStart = primitives.get(endIndex).sourceEnd();
          } else {
            primitiveIndex++;
            lineSourceStart = primitive.sourceEnd();
          }
          linePrimitiveStart = primitiveIndex;
          lineGlyphCount = 0;
          lineOffset = 0;
          lineTextAdvance = 0;
          wordCandidate = null;
          trailingHardSeparator = true;
          continue;
        }

        boolean wordBoundary =
            request.wordWrap() && Character.isSpaceChar(primitive.sourceCodePoint());
        WordBoundaryCandidate effectiveCandidate = wordCandidate;
        if (wordBoundary) {
          effectiveCandidate =
              new WordBoundaryCandidate(
                  primitiveIndex,
                  primitiveIndex + 1,
                  primitive.sourceStart(),
                  primitive.sourceEnd(),
                  lineTextAdvance,
                  0,
                  0);
        } else if (effectiveCandidate != null) {
          effectiveCandidate = effectiveCandidate.appendDeferred(primitive);
        }

        float primitiveAdvance =
            lineGlyphCount == 0
                ? primitive.baseAdvance()
                : primitive.baseAdvance() + primitive.pairKerningAdvance();
        float nextTextAdvance = lineTextAdvance + primitiveAdvance;
        boolean overWidth = lineOffset + nextTextAdvance > request.maxWidth();
        if (overWidth
            && effectiveCandidate != null
            && effectiveCandidate.linePrimitiveEnd() > linePrimitiveStart) {
          addRange(
              linePrimitiveStart,
              effectiveCandidate.linePrimitiveEnd(),
              lineSourceStart,
              effectiveCandidate.lineSourceEnd(),
              lineOffset,
              effectiveCandidate.lineTextAdvance());
          linePrimitiveStart = effectiveCandidate.deferredPrimitiveStart();
          lineSourceStart = effectiveCandidate.deferredSourceStart();
          lineGlyphCount = effectiveCandidate.deferredGlyphCount();
          lineOffset = 0;
          lineTextAdvance = effectiveCandidate.deferredTextAdvance();
          wordCandidate = null;
          primitiveIndex++;
          trailingHardSeparator = false;
          continue;
        }

        if (overWidth && lineGlyphCount > 0) {
          addRange(
              linePrimitiveStart,
              primitiveIndex,
              lineSourceStart,
              primitive.sourceStart(),
              lineOffset,
              lineTextAdvance);
          linePrimitiveStart = primitiveIndex;
          lineSourceStart = primitive.sourceStart();
          lineGlyphCount = 1;
          lineOffset = 0;
          lineTextAdvance = primitive.baseAdvance();
          wordCandidate =
              wordBoundary
                  ? new WordBoundaryCandidate(
                      primitiveIndex,
                      primitiveIndex + 1,
                      primitive.sourceStart(),
                      primitive.sourceEnd(),
                      0,
                      0,
                      0)
                  : null;
          primitiveIndex++;
          trailingHardSeparator = false;
          continue;
        }

        lineGlyphCount++;
        lineTextAdvance = nextTextAdvance;
        wordCandidate = effectiveCandidate;
        primitiveIndex++;
        trailingHardSeparator = false;
      }

      if (linePrimitiveStart < primitives.size() || ranges.isEmpty() || trailingHardSeparator) {
        addRange(
            linePrimitiveStart,
            primitives.size(),
            lineSourceStart,
            sequence.sourceEnd(),
            lineOffset,
            lineTextAdvance);
      }
      return List.copyOf(ranges);
    }

    private void addRange(
        int primitiveStart,
        int primitiveEnd,
        int sourceStart,
        int sourceEnd,
        float initialOffset,
        float textAdvance) {
      ranges.add(
          new PrivateWrapRange(
              primitiveStart,
              primitiveEnd,
              sourceStart,
              sourceEnd,
              initialOffset,
              initialOffset + textAdvance));
    }
  }

  private record WordBoundaryCandidate(
      int linePrimitiveEnd,
      int deferredPrimitiveStart,
      int lineSourceEnd,
      int deferredSourceStart,
      float lineTextAdvance,
      float deferredTextAdvance,
      int deferredGlyphCount) {
    private WordBoundaryCandidate appendDeferred(ResolvedPrimitive primitive) {
      float advance =
          deferredGlyphCount == 0
              ? primitive.baseAdvance()
              : primitive.baseAdvance() + primitive.pairKerningAdvance();
      return new WordBoundaryCandidate(
          linePrimitiveEnd,
          deferredPrimitiveStart,
          lineSourceEnd,
          deferredSourceStart,
          lineTextAdvance,
          deferredTextAdvance + advance,
          deferredGlyphCount + 1);
    }
  }

  /** Freezes final public lines, runs, glyphs, and aligned line-local caret stops exactly once. */
  private static final class FinalMeasurementMaterializer {
    private final DiagnosticSession diagnostics;
    private final PrivatePreparedMeasurement prepared;
    private final List<TextLineMetrics> lines = new ArrayList<>();
    private final List<FinalLineCaretStops> lineCaretStops = new ArrayList<>();
    private float width;

    private FinalMeasurementMaterializer(
        DiagnosticSession diagnostics, PrivatePreparedMeasurement prepared) {
      this.diagnostics = diagnostics;
      this.prepared = prepared;
    }

    private ResolvedMeasurement materialize() {
      for (PrivatePreWrapLine privateLine : prepared.lines()) {
        materializeLine(privateLine);
      }
      TextMetrics metrics =
          new TextMetrics(
              lines,
              width,
              lines.size() * prepared.fontMetrics().lineHeight(),
              prepared.fontMetrics().lineHeight(),
              prepared.fontMetrics());
      return new ResolvedMeasurement(metrics, lineCaretStops);
    }

    private void materializeLine(PrivatePreWrapLine privateLine) {
      int glyphCount = privateLine.primitiveEnd() - privateLine.primitiveStart();
      int[] boundaries = new int[glyphCount + 1];
      float[] cumulativeAdvances = new float[glyphCount + 1];
      boundaries[0] = privateLine.sourceStart();

      List<ResolvedTextRun> runs = new ArrayList<>();
      FinalRunBuilder run = null;
      float textAdvance = 0;
      int caretIndex = 1;
      List<ResolvedPrimitive> primitives = prepared.sequence().primitives();
      for (int primitiveIndex = privateLine.primitiveStart();
          primitiveIndex < privateLine.primitiveEnd();
          primitiveIndex++) {
        ResolvedPrimitive primitive = primitives.get(primitiveIndex);
        if (primitive.separator()) {
          throw new IllegalStateException("Final line materialization encountered a separator");
        }
        float advance =
            primitiveIndex == privateLine.primitiveStart()
                ? primitive.baseAdvance()
                : primitive.baseAdvance() + primitive.pairKerningAdvance();
        if (run == null || !run.accepts(primitive)) {
          if (run != null) {
            runs.add(run.freeze());
          }
          run = new FinalRunBuilder(diagnostics, primitive);
        }
        run.append(primitive, advance);
        textAdvance += advance;
        boundaries[caretIndex] = primitive.sourceEnd();
        cumulativeAdvances[caretIndex] = textAdvance;
        caretIndex++;
      }
      if (run != null) {
        runs.add(run.freeze());
      }

      if (Float.compare(textAdvance, privateLine.textAdvance()) != 0) {
        throw new IllegalStateException(
            "Final line materialization changed the approved advance accumulation order");
      }
      String characters =
          prepared
              .request()
              .source()
              .substring(privateLine.sourceStart(), privateLine.sourceEnd());
      TextLineMetrics line =
          new TextLineMetrics(
              characters,
              privateLine.sourceStart(),
              privateLine.sourceEnd(),
              privateLine.charCount(),
              privateLine.width(),
              privateLine.height(),
              privateLine.baseline(),
              privateLine.fontMetrics(),
              runs);
      lines.add(line);
      lineCaretStops.add(new FinalLineCaretStops(boundaries, cumulativeAdvances));
      width = Math.max(width, line.width());
    }
  }

  private static final class FinalRunBuilder {
    private final DiagnosticSession diagnostics;
    private final Font font;
    private final STBTTFontinfo fontInfo;
    private final int sourceStart;
    private final List<ResolvedGlyph> glyphs = new ArrayList<>();
    private int sourceEnd;
    private float advance;
    private boolean frozen;

    private FinalRunBuilder(DiagnosticSession diagnostics, ResolvedPrimitive first) {
      this.diagnostics = diagnostics;
      this.font = first.font();
      this.fontInfo = first.fontInfo();
      this.sourceStart = first.sourceStart();
    }

    private boolean accepts(ResolvedPrimitive primitive) {
      return font.equals(primitive.font()) && fontInfo == primitive.fontInfo();
    }

    private void append(ResolvedPrimitive primitive, float glyphAdvance) {
      requireMutable();
      if (!accepts(primitive)) {
        throw new IllegalArgumentException("Final run builder cannot change selected font face");
      }
      glyphs.add(
          new ResolvedGlyph(
              primitive.sourceStart(),
              primitive.sourceEnd(),
              primitive.sourceCodePoint(),
              primitive.renderedCodePoint(),
              primitive.font(),
              primitive.replacement()));
      diagnostics.increment(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS);
      sourceEnd = primitive.sourceEnd();
      advance += glyphAdvance;
    }

    private ResolvedTextRun freeze() {
      requireMutable();
      frozen = true;
      diagnostics.add(TextDiagnosticCounter.GLYPH_SLOTS_COPIED, glyphs.size());
      diagnostics.add(
          TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED, glyphs.size());
      diagnostics.increment(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES);
      diagnostics.increment(TextDiagnosticCounter.RUN_BUILDER_APPENDS);
      diagnostics.increment(TextDiagnosticCounter.RUN_BUILDER_FREEZES);
      return new ResolvedTextRun(sourceStart, sourceEnd, font, glyphs, advance);
    }

    private void requireMutable() {
      if (frozen) {
        throw new IllegalStateException("Final run builder is already frozen");
      }
    }
  }

  private static final class PrivateLineBuilder {
    private final DiagnosticSession diagnostics;
    private final int primitiveStart;
    private final int runRangeStart;
    private final int sourceStart;
    private final float initialOffset;
    private final FontMetrics fontMetrics;
    private final List<Integer> caretBoundaries = new ArrayList<>();
    private final List<Float> rawAdvanceSlots = new ArrayList<>();
    private final List<Float> rebasedAdvanceSlots = new ArrayList<>();
    private float textAdvance;
    private boolean frozen;

    private PrivateLineBuilder(
        DiagnosticSession diagnostics,
        int primitiveStart,
        int runRangeStart,
        int sourceStart,
        float initialOffset,
        FontMetrics fontMetrics) {
      this.diagnostics = diagnostics;
      this.primitiveStart = primitiveStart;
      this.runRangeStart = runRangeStart;
      this.sourceStart = sourceStart;
      this.initialOffset = initialOffset;
      this.fontMetrics = fontMetrics;
      caretBoundaries.add(sourceStart);
      diagnostics.increment(TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_APPENDS);
    }

    private void append(ResolvedPrimitive primitive) {
      requireMutable();
      if (primitive.separator()) {
        throw new IllegalArgumentException("Line builders cannot contain separator primitives");
      }
      float rawAdvance = primitive.baseAdvance() + primitive.pairKerningAdvance();
      float rebasedAdvance =
          rawAdvanceSlots.isEmpty() ? primitive.baseAdvance() : rawAdvance;
      rawAdvanceSlots.add(rawAdvance);
      rebasedAdvanceSlots.add(rebasedAdvance);
      diagnostics.add(TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_APPENDS, 2);
      caretBoundaries.add(primitive.sourceEnd());
      diagnostics.increment(TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_APPENDS);
      textAdvance += rebasedAdvance;
    }

    private PrivatePreWrapLine freeze(int primitiveEnd, int runRangeEnd, int sourceEnd) {
      requireMutable();
      frozen = true;
      diagnostics.increment(TextDiagnosticCounter.LINE_BUILDER_FREEZES);
      diagnostics.increment(TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_FREEZES);
      diagnostics.add(TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_FREEZES, 2);
      return new PrivatePreWrapLine(
          primitiveStart,
          primitiveEnd,
          runRangeStart,
          runRangeEnd,
          sourceStart,
          sourceEnd,
          sourceEnd - sourceStart,
          List.copyOf(caretBoundaries),
          List.copyOf(rawAdvanceSlots),
          List.copyOf(rebasedAdvanceSlots),
          textAdvance,
          initialOffset + textAdvance,
          fontMetrics.lineHeight(),
          fontMetrics.baseline(),
          fontMetrics);
    }

    private void requireMutable() {
      if (frozen) {
        throw new IllegalStateException("Private line builder is already frozen");
      }
    }
  }

  private static final class ResolvedPrimitiveBuilder {
    private final DiagnosticSession diagnostics;
    private final int sourceStart;
    private final int sourceEnd;
    private final List<ResolvedPrimitive> primitives = new ArrayList<>();
    private final List<PrivateRunRange> runRanges = new ArrayList<>();
    private Font runFont;
    private STBTTFontinfo runFontInfo;
    private int runPrimitiveStart;
    private int runSourceStart;
    private int runSourceEnd;
    private int runGlyphCount;
    private int glyphCount;
    private boolean frozen;

    private ResolvedPrimitiveBuilder(DiagnosticSession diagnostics, int sourceStart, int sourceEnd) {
      this.diagnostics = diagnostics;
      this.sourceStart = sourceStart;
      this.sourceEnd = sourceEnd;
    }

    private void append(ResolvedPrimitive primitive) {
      requireMutable();
      int primitiveIndex = primitives.size();
      if (primitive.separator()) {
        finishRun(primitiveIndex);
        primitives.add(primitive);
        diagnostics.increment(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS);
        return;
      }

      if (runFont != null
          && (!runFont.equals(primitive.font()) || runFontInfo != primitive.fontInfo())) {
        finishRun(primitiveIndex);
      }
      if (runFont == null) {
        runFont = primitive.font();
        runFontInfo = primitive.fontInfo();
        runPrimitiveStart = primitiveIndex;
        runSourceStart = primitive.sourceStart();
        runGlyphCount = 0;
      }
      primitives.add(primitive);
      diagnostics.increment(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS);
      diagnostics.increment(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS);
      glyphCount++;
      runGlyphCount++;
      runSourceEnd = primitive.sourceEnd();
    }

    private ResolvedPrimitiveSequence freeze() {
      requireMutable();
      finishRun(primitives.size());
      frozen = true;
      diagnostics.increment(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES);
      diagnostics.increment(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES);
      diagnostics.increment(TextDiagnosticCounter.RUN_BUILDER_FREEZES);
      diagnostics.add(TextDiagnosticCounter.GLYPH_SLOTS_COPIED, glyphCount);
      diagnostics.add(TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED, glyphCount);
      return new ResolvedPrimitiveSequence(
          sourceStart, sourceEnd, List.copyOf(primitives), List.copyOf(runRanges));
    }

    private void finishRun(int primitiveEnd) {
      if (runFont == null) {
        return;
      }
      runRanges.add(
          new PrivateRunRange(
              runPrimitiveStart,
              primitiveEnd,
              runSourceStart,
              runSourceEnd,
              runFont,
              runFontInfo,
              runGlyphCount));
      diagnostics.increment(TextDiagnosticCounter.RUN_BUILDER_APPENDS);
      runFont = null;
      runFontInfo = null;
      runGlyphCount = 0;
    }

    private void requireMutable() {
      if (frozen) {
        throw new IllegalStateException("Resolved primitive builder is already frozen");
      }
    }
  }

  private record BaseAdvanceMeasurement(int rawAdvance, float advance) {}

  private record PairKerningMeasurement(
      STBTTFontinfo previousFontInfo, int previousGlyphIndex, int rawAdvance, float advance) {}

  private record GlyphProbeKey(String locator, long generation, int codePoint) {}

  private record AdvanceKey(String locator, long generation, int glyphIndex, int fontSizeBits) {}

  private record KerningKey(
      String locator, long generation, int previousGlyphIndex, int glyphIndex, int fontSizeBits) {}

  private record KerningValue(int rawAdvance, float advance) {}

  private record ResolvedFont(Font font, String locator, STBTTFontinfo fontInfo) {}

  private record GlyphMeasurement(
      Font font,
      String locator,
      STBTTFontinfo fontInfo,
      int glyphIndex,
      int renderedCodePoint,
      boolean replacement) {}
}
