package com.spinyowl.spinygui.benchmark.diagnostic;

import com.spinyowl.spinygui.benchmark.TextStyleSpecification;
import com.spinyowl.spinygui.benchmark.TextWorkloads;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests.FontInput;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests.InputSet;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Category;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Dimension;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Source-bound declared-input specifications for the E5 counter-only workload matrix. */
public final class DiagnosticWorkloadSpecifications {
  public static final String RESULT_SCHEMA_VERSION = "counter-diagnostics-artifact-2";
  public static final String BENCHMARK_VERSION = "text-counter-diagnostics-2";
  public static final int FRAME_WIDTH_PX = 1280;
  public static final int FRAME_HEIGHT_PX = 720;

  private static final List<Font> PREWARM_FONTS =
      List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR);
  private static final String PREWARM_TEXT = "Counter prewarm Latin 雪";
  private static final String PREWARM_WORKLOAD_CONTENT = "counter-prewarm-fallback-v1";
  private static final TextStyleSpecification STYLE =
      new TextStyleSpecification(
          PREWARM_FONTS,
          List.of(
              Font.ROBOTO_REGULAR,
              Font.ROBOTO_LIGHT,
              Font.ROBOTO_BOLD,
              Font.NOTO_SANS_CJK_SC_REGULAR),
          FontStyle.NORMAL,
          FontWeight.NORMAL,
          FontStretch.NORMAL,
          16,
          1.2f,
          Color.WHITE,
          Display.BLOCK,
          Position.STATIC,
          WhiteSpace.NORMAL,
          TextAlign.LEFT,
          OverflowWrap.NORMAL,
          WordBreak.NORMAL,
          4);
  private static final String HARD_WRAP_PARAGRAPHS = "AVa雪\nAVa雪\nAVa雪\nAVa雪";
  private static final String DEFERRED_PARAGRAPHS =
      "AV a雪aaaa aa\nAV a雪aaaa aa";
  private static final List<String> NORMAL_RENDERER_SOURCES =
      List.of(TextWorkloads.LATIN.replace(" ", ""), TextWorkloads.MIXED_CJK.replace(" ", ""));
  private static final String INPUT_RENDERER_TEXT = "Select a deterministic span.";
  private static final String TEXTAREA_RENDERER_TEXT = "AVa雪aaaa\nAVa雪aaaa\nAVa雪aaaa\nAVa雪aaaa";

  public static final List<CpuScenario> CPU_SCENARIOS =
      List.of(
          cpu("run-assembly-8", "a".repeat(8), 100_000, false, 1, 0, 0, 0),
          cpu("run-assembly-16", "a".repeat(16), 100_000, false, 1, 0, 0, 0),
          cpu("run-assembly-32", "a".repeat(32), 100_000, false, 1, 0, 0, 0),
          cpu("zero-width-boundary", "Boundary a雪 remains unscanned", 0, true, 26, 2, 0, 25),
          cpu(
              "multi-paragraph-fallback-line-start",
              HARD_WRAP_PARAGRAPHS,
              24,
              false,
              12,
              4,
              0,
              8),
          cpu(
              "multi-paragraph-fallback-deferred-suffix",
              DEFERRED_PARAGRAPHS,
              48,
              true,
               6,
               4,
               0,
               4));

  public static final List<RendererScenario> RENDERER_SCENARIOS = createRendererScenarios();

  private DiagnosticWorkloadSpecifications() {
  }

  public static ComparabilityMetadata comparability(
      Scenario scenario,
      ComparabilityMetadata.Environment environment,
      ComparabilityMetadata.Implementation implementation) {
    WorkloadIdentity identity = scenario.identity();
    InputSet manifests = scenario.inputManifests();
    return new ComparabilityMetadata(
        BENCHMARK_VERSION,
        identity.dimensions().get(Dimension.WORKLOAD_VERSION),
        RESULT_SCHEMA_VERSION,
        "text-behavior-1",
        EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED,
        identity.semanticId(),
        identity.displayLabel(),
        manifests.content().sha256(),
        manifests.shape().sha256(),
        manifests.fonts().sha256(),
        environment,
        scenario.executionSettings(),
        implementation);
  }

  public sealed interface Scenario permits CpuScenario, RendererScenario {
    String name();

    Category category();

    ExpectedShape expectedShape();

    Map<Dimension, Object> identityValues();

    WorkloadIdentity identity();

    InputSet inputManifests();

    Map<String, String> executionSettings();

    String evidenceScope();

    default void validateObserved(ObservedShape observed) {
      Objects.requireNonNull(observed, "observed");
      ExpectedShape expected = expectedShape();
      if (!sourceTextSha256().equals(observed.sourceTextSha256())
          || expected.sourceCodePointCount() != observed.sourceCodePointCount()
          || expected.sourceLineCount() != observed.sourceLineCount()
          || expected.visualLineCount() != observed.visualLineCount()
          || expected.paragraphCount() != observed.paragraphCount()
          || expected.fallbackTransitionCount() != observed.fallbackTransitionCount()
          || expected.deferredSuffixCodePointCount()
              != observed.deferredSuffixCodePointCount()
          || expected.lineStartKerningTransitionCount()
              != observed.lineStartKerningTransitionCount()) {
        throw new IllegalStateException(
            "Independently observed prepared/executed shape disagrees with "
                + name()
                + "; declared="
                + expected
                + "; observed="
                + observed);
      }
    }

    default String sourceTextSha256() {
      List<String> sources =
          switch (this) {
            case CpuScenario cpu -> List.of(cpu.text());
            case RendererScenario renderer -> renderer.expandedSourceContents();
          };
      return DiagnosticWorkloadSpecifications.sourceTextSha256(sources);
    }
  }

  public record CpuScenario(
      String name,
      String workloadContent,
      String text,
      List<Font> fonts,
      float fontSizePx,
      float lineHeight,
      float measurementOffsetXPx,
      float wrapWidthPx,
      boolean wordWrap,
      ExpectedShape expectedShape) implements Scenario {

    public CpuScenario {
      requireNameAndContent(name, workloadContent);
      Objects.requireNonNull(text, "text");
      String expectedText =
          switch (name) {
            case "run-assembly-8" -> "a".repeat(8);
            case "run-assembly-16" -> "a".repeat(16);
            case "run-assembly-32" -> "a".repeat(32);
            case "zero-width-boundary" -> "Boundary a雪 remains unscanned";
            case "multi-paragraph-fallback-line-start" -> HARD_WRAP_PARAGRAPHS;
            case "multi-paragraph-fallback-deferred-suffix" -> DEFERRED_PARAGRAPHS;
            default -> null;
          };
      if (!workloadContent.equals("scaled-" + name + "-v1") || !text.equals(expectedText)) {
        throw new IllegalArgumentException(
            "CPU workload-content must bind the exact executed source corpus: " + name);
      }
      fonts = List.copyOf(fonts);
      if (fonts.isEmpty() || wrapWidthPx < 0 || !Float.isFinite(wrapWidthPx)) {
        throw new IllegalArgumentException("Invalid CPU diagnostic scenario: " + name);
      }
      ExpectedShape sourceShape =
          expectedShape.withSource(
              codePointCount(text), sourceLineCount(text), paragraphCount(text));
      if (!sourceShape.equals(expectedShape)) {
        throw new IllegalArgumentException(
            "CPU declared source shape does not match exact content: " + name);
      }
      if (name.equals("zero-width-boundary")
          && (expectedShape.visualLineCount() != 26
              || expectedShape.fallbackTransitionCount() != 2
              || expectedShape.deferredSuffixCodePointCount() != 0
              || expectedShape.lineStartKerningTransitionCount() != 25)) {
        throw new IllegalArgumentException(
            "Zero-width boundary must declare its exact progressing-wrap work");
      }
    }

    @Override
    public Category category() {
      return Category.CPU;
    }

    public TextMetrics execute(FontServiceImpl fontService) {
      return fontService.measureText(
          text, measurementOffsetXPx, fonts, fontSizePx, lineHeight, wrapWidthPx, wordWrap);
    }

    @Override
    public Map<Dimension, Object> identityValues() {
      WorkloadIdentity.requiredDimensions(Category.CPU, "measureParameterizedText");
      Map<Dimension, Object> values = new EnumMap<>(Dimension.class);
      values.put(Dimension.API, "measure-text-wrapped-font-list");
      values.put(Dimension.CATEGORY, "cpu");
      putShape(values, expectedShape);
      values.put(
          Dimension.FIXTURE_PREPARATION_POLICY,
          "counter-scenario-created-before-recorded-operation");
      values.put(Dimension.FONT_CHAIN, fontIdentities(fonts));
      values.put(
          Dimension.FONT_FIXTURE_POLICY,
          "counter-scenario-prewarm-once-before-recorded-operation");
      values.put(Dimension.FONT_RESOLVER, "default");
      values.put(Dimension.FONT_SIZE_PX, fontSizePx);
      values.put(Dimension.FONT_STRETCH, fonts.getFirst().stretch().name());
      values.put(Dimension.FONT_STYLE, fonts.getFirst().style().name());
      values.put(Dimension.FONT_WEIGHT, fonts.getFirst().weight().name());
      values.put(Dimension.HARNESS, "direct");
      values.put(Dimension.LINE_HEIGHT, lineHeight);
      values.put(Dimension.MEASUREMENT_OFFSET_X_PX, measurementOffsetXPx);
      values.put(Dimension.NATIVE_ACCESS, "all-unnamed");
      values.put(Dimension.OPERATION, "measureParameterizedText");
      values.put(Dimension.ROUND_TO_PIXEL, false);
      values.put(Dimension.SETUP_LEVEL, "application-run");
      values.put(Dimension.WORKLOAD_CONTENT, workloadContent);
      values.put(Dimension.WORKLOAD_VERSION, 1);
      values.put(Dimension.WRAP_WIDTH_POLICY, "fixed");
      values.put(Dimension.WRAP_WIDTH_PX, wrapWidthPx);
      values.put(Dimension.WRAPPING_POLICY, wordWrap ? "word-wrap" : "character-wrap");
      return Map.copyOf(values);
    }

    @Override
    public WorkloadIdentity identity() {
      return DiagnosticWorkloadSpecifications.identity(
          category(), "measureParameterizedText", "cpu-text", identityValues(), name);
    }

    @Override
    public InputSet inputManifests() {
      Map<String, String> shape = shapeManifest(expectedShape);
      shape.put("measurement-offset-x-px", decimal(measurementOffsetXPx));
      shape.put("wrap-width-px", decimal(wrapWidthPx));
      shape.put("wrapping-policy", wordWrap ? "word-wrap" : "character-wrap");
      List<FontInput> fontInputs =
          fonts.stream().map(font -> fontInput("measurement", font)).toList();
      return new InputSet(
          BenchmarkInputManifests.content(Map.of("source-0000", text)),
          BenchmarkInputManifests.shape(shape),
          BenchmarkInputManifests.fonts(
              fontInputs,
              typographyConfiguration(
                  fontSizePx,
                  lineHeight,
                  fonts.getFirst().style(),
                  fonts.getFirst().weight(),
                  fonts.getFirst().stretch())));
    }

    @Override
    public Map<String, String> executionSettings() {
      return Map.of(
          "native-access", "all-unnamed",
          "prewarm-operation-count", "1",
          "recorded-operation-count", "1",
          "reset-policy", "immediately-before-recorded-operation",
          "setup-policy", "same-exact-scenario-operation-prewarmed-once",
          "snapshot-policy", "immediately-after-recorded-operation",
          "thread-count", "1",
          "timing", "none");
    }

    @Override
    public String evidenceScope() {
      return "single-recorded-operation";
    }
  }

  public record RendererScenario(
      String name,
      Category category,
      String workloadContent,
      List<String> sourceContents,
      int itemCount,
      Rect container,
      float controlWidthPx,
      float controlHeightPx,
      int selectionStartUtf16,
      int selectionEndUtf16,
      int caretIndexUtf16,
      float scrollXPx,
      float scrollYPx,
      String submissionState,
      ExpectedShape expectedShape) implements Scenario {

    public RendererScenario {
      requireNameAndContent(name, workloadContent);
      Objects.requireNonNull(category, "category");
      sourceContents = List.copyOf(sourceContents);
      Objects.requireNonNull(container, "container");
      Objects.requireNonNull(submissionState, "submissionState");
      Objects.requireNonNull(expectedShape, "expectedShape");
      if (category == Category.CPU
          || sourceContents.isEmpty()
          || itemCount <= 0
          || controlWidthPx <= 0
          || controlHeightPx <= 0
          || (!"changed".equals(submissionState) && !"unchanged".equals(submissionState))) {
        throw new IllegalArgumentException("Invalid renderer diagnostic scenario: " + name);
      }
      String expectedWorkloadContent =
          switch (category) {
            case NORMAL_TEXT -> "alternating-latin-mixed-cjk-counter-v1";
            case INPUT -> "input-selection-text-v1";
            case TEXTAREA -> "multi-paragraph-wrapped-fallback-v1";
            case CPU -> throw new IllegalStateException();
          };
      List<String> expectedSources =
          switch (category) {
            case NORMAL_TEXT -> NORMAL_RENDERER_SOURCES;
            case INPUT -> List.of(INPUT_RENDERER_TEXT);
            case TEXTAREA -> List.of(TEXTAREA_RENDERER_TEXT);
            case CPU -> throw new IllegalStateException();
          };
      if (!workloadContent.equals(expectedWorkloadContent)
          || !sourceContents.equals(expectedSources)) {
        throw new IllegalArgumentException(
            "Renderer workload-content must bind the exact executed source corpus: " + name);
      }
      ExpectedShape sourceShape = aggregateSourceShape(sourceContents, itemCount, expectedShape);
      if (!sourceShape.equals(expectedShape)) {
        throw new IllegalArgumentException(
            "Renderer declared source shape does not match exact aggregate content: " + name);
      }
      int perItemLength = sourceContents.getFirst().length();
      if (category != Category.NORMAL_TEXT
          && (sourceContents.size() != 1
              || selectionStartUtf16 < 0
              || selectionStartUtf16 > selectionEndUtf16
              || selectionEndUtf16 > perItemLength
              || (caretIndexUtf16 != selectionStartUtf16
                  && caretIndexUtf16 != selectionEndUtf16))) {
        throw new IllegalArgumentException("Control selection must bind to its exact source");
      }
      if (category == Category.TEXTAREA
          && Float.compare(controlWidthPx, container.widthPx()) != 0) {
        throw new IllegalArgumentException(
            "Textarea wrap width must equal the production control content width");
      }
    }

    public TextStyleSpecification style() {
      return STYLE;
    }

    public List<Font> prewarmFonts() {
      return PREWARM_FONTS;
    }

    public List<Font> layoutFonts() {
      return STYLE.resolvedFonts();
    }

    public String prewarmText() {
      return PREWARM_TEXT;
    }

    public String sourceContent(int index) {
      return sourceContents.get(Math.floorMod(index, sourceContents.size()));
    }

    public List<String> expandedSourceContents() {
      List<String> expanded = new ArrayList<>(itemCount);
      for (int index = 0; index < itemCount; index++) {
        expanded.add(sourceContent(index));
      }
      return List.copyOf(expanded);
    }

    public String operation() {
      return switch (category) {
        case NORMAL_TEXT -> "render-normal-text-scenario";
        case INPUT -> "render-input-scenario";
        case TEXTAREA -> "render-textarea-scenario";
        case CPU -> throw new IllegalStateException("CPU is not a renderer category");
      };
    }

    public String visibility() {
      return container.visibility(FRAME_WIDTH_PX, FRAME_HEIGHT_PX);
    }

    public String clipState() {
      return "visible".equals(visibility()) ? "inside" : "outside";
    }

    public String contentAlternation() {
      return sourceContents.size() == 2 && itemCount > 1 ? "latin-mixed-cjk" : "none";
    }

    public float wrapWidthPx() {
      return category == Category.TEXTAREA ? controlWidthPx : 0;
    }

    @Override
    public Map<Dimension, Object> identityValues() {
      WorkloadIdentity.requiredDimensions(category, operation());
      Map<Dimension, Object> values = new EnumMap<>(Dimension.class);
      values.put(Dimension.API, "render-frame");
      values.put(Dimension.CATEGORY, category.canonicalValue());
      values.put(Dimension.CLEAR_POLICY, "color-stencil-before-sample");
      values.put(Dimension.CLIP_STATE, clipState());
      values.put(Dimension.COLOR, "white");
      values.put(Dimension.CONTAINER_HEIGHT_PX, container.heightPx());
      values.put(Dimension.CONTAINER_POSITION_X_PX, container.xPx());
      values.put(Dimension.CONTAINER_POSITION_Y_PX, container.yPx());
      values.put(Dimension.CONTAINER_WIDTH_PX, container.widthPx());
      values.put(Dimension.CONTEXT_VISIBILITY, "hidden");
      values.put(Dimension.CONTENT_ALTERNATION, contentAlternation());
      values.put(Dimension.CONTROL_TYPE, controlType());
      putShape(values, expectedShape);
      values.put(Dimension.DISPLAY, STYLE.display().name());
      values.put(
          Dimension.FIXTURE_PREPARATION_POLICY,
          "parameterized-scene-created-before-warmup");
      values.put(Dimension.FONT_CHAIN, stagedFontIdentities());
      values.put(Dimension.FONT_FIXTURE_POLICY, "scenario-corpus-before-measurement");
      values.put(Dimension.FONT_RESOLVER, "default");
      values.put(Dimension.FONT_SIZE_PX, STYLE.fontSizePx());
      values.put(Dimension.FONT_STRETCH, STYLE.effectiveFontStretch().name());
      values.put(Dimension.FONT_STYLE, STYLE.fontStyle().name());
      values.put(Dimension.FONT_WEIGHT, STYLE.fontWeight().name());
      values.put(Dimension.FRAME_HEIGHT_PX, FRAME_HEIGHT_PX);
      values.put(Dimension.FRAME_WIDTH_PX, FRAME_WIDTH_PX);
      values.put(Dimension.HARNESS, "nanovg");
      values.put(Dimension.INLINE_LAYOUT_START_Y_PX, 0);
      values.put(Dimension.LINE_HEIGHT, STYLE.lineHeight());
      values.put(Dimension.MEASURED_FRAMES, 1);
      values.put(Dimension.MEASUREMENT_ORDER, "isolated");
      values.put(Dimension.MEASUREMENT_ORDER_INDEX, 1);
      values.put(Dimension.NATIVE_ACCESS, "all-unnamed");
      values.put(Dimension.OFFSCREEN_EXTENT_PX, container.offscreenExtent(FRAME_WIDTH_PX, FRAME_HEIGHT_PX));
      values.put(Dimension.OFFSCREEN_RATIO, container.offscreenRatio(FRAME_WIDTH_PX, FRAME_HEIGHT_PX));
      values.put(Dimension.OPERATION, operation());
      values.put(Dimension.OVERFLOW_WRAP, STYLE.overflowWrap().name());
      values.put(Dimension.POSITION, STYLE.position().name());
      values.put(Dimension.PREMEASURE_SEQUENCE, "per-scene");
      values.put(Dimension.PREWARM_WORKLOAD_CONTENT, PREWARM_WORKLOAD_CONTENT);
      values.put(Dimension.RENDERER_PATH, rendererPath());
      values.put(Dimension.ROUND_TO_PIXEL, true);
      values.put(Dimension.SCENE_HEIGHT_PX, FRAME_HEIGHT_PX);
      values.put(Dimension.SCENE_WIDTH_PX, FRAME_WIDTH_PX);
      values.put(Dimension.SETUP_LEVEL, "application-run");
      values.put(
          Dimension.SOURCE_UTF16_LENGTH,
          aggregate(sourceContents, itemCount, String::length));
      values.put(Dimension.SUBMISSION_STATE, submissionState);
      values.put(Dimension.SWAP_INTERVAL, 0);
      values.put(Dimension.SYNCHRONIZATION, "none");
      values.put(Dimension.TAB_SIZE, STYLE.tabSize());
      values.put(Dimension.TEXT_ALIGN, STYLE.textAlign().name());
      values.put(Dimension.TEXT_NODE_COUNT, itemCount);
      values.put(Dimension.VALIDATION_POLICY, "none");
      values.put(Dimension.VISIBILITY, visibility());
      values.put(Dimension.WARMUP_FRAMES, "unchanged".equals(submissionState) ? 1 : 0);
      values.put(Dimension.WINDOW_RESIZABLE, false);
      values.put(Dimension.WHITE_SPACE, STYLE.whiteSpace().name());
      values.put(Dimension.WORD_BREAK, STYLE.wordBreak().name());
      values.put(Dimension.WORKLOAD_CONTENT, workloadContent);
      values.put(Dimension.WORKLOAD_VERSION, 1);
      values.put(
          Dimension.WRAPPING_POLICY,
          category == Category.INPUT
              ? "single-line"
              : category == Category.TEXTAREA ? "soft-wrap" : "normal");
      if (category != Category.NORMAL_TEXT) {
        values.put(Dimension.CARET_INDEX_UTF16, caretIndexUtf16);
        values.put(Dimension.CARET_STATE, "visible");
        values.put(Dimension.CONTROL_HEIGHT_PX, controlHeightPx);
        values.put(Dimension.CONTROL_STATE, "focused");
        values.put(Dimension.CONTROL_WIDTH_PX, controlWidthPx);
        values.put(Dimension.SCROLL_X_PX, scrollXPx);
        values.put(Dimension.SELECTION_END_UTF16, selectionEndUtf16);
        values.put(Dimension.SELECTION_START_UTF16, selectionStartUtf16);
      }
      if (category == Category.TEXTAREA) {
        values.put(Dimension.MEASUREMENT_OFFSET_X_PX, 0);
        values.put(Dimension.SCROLL_Y_PX, scrollYPx);
        values.put(Dimension.WRAP_WIDTH_POLICY, "fixed");
        values.put(Dimension.WRAP_WIDTH_PX, wrapWidthPx());
      }
      return Map.copyOf(values);
    }

    @Override
    public WorkloadIdentity identity() {
      String workload =
          switch (category) {
            case NORMAL_TEXT -> "renderer-text-scenario";
            case INPUT -> "renderer-input-scenario";
            case TEXTAREA -> "renderer-textarea-scenario";
            case CPU -> throw new IllegalStateException();
          };
      return DiagnosticWorkloadSpecifications.identity(
          category, operation(), workload, identityValues(), name);
    }

    @Override
    public InputSet inputManifests() {
      Map<String, String> content = new LinkedHashMap<>();
      content.put("prewarm-text", PREWARM_TEXT);
      for (int index = 0; index < itemCount; index++) {
        content.put("source-" + String.format(java.util.Locale.ROOT, "%04d", index), sourceContent(index));
      }
      Map<String, String> shape = shapeManifest(expectedShape);
      removeInapplicableRendererShape(shape);
      shape.put("category", category.canonicalValue());
      shape.put("container-height-px", decimal(container.heightPx()));
      shape.put("container-position-x-px", decimal(container.xPx()));
      shape.put("container-position-y-px", decimal(container.yPx()));
      shape.put("container-width-px", decimal(container.widthPx()));
      shape.put("item-count", Integer.toString(itemCount));
      shape.put("offscreen-extent-px", decimal(container.offscreenExtent(FRAME_WIDTH_PX, FRAME_HEIGHT_PX)));
      shape.put("offscreen-ratio", decimal(container.offscreenRatio(FRAME_WIDTH_PX, FRAME_HEIGHT_PX)));
      shape.put("submission-state", submissionState);
      shape.put("visibility", visibility());
      if (category != Category.NORMAL_TEXT) {
        shape.put("caret-index-utf16", Integer.toString(caretIndexUtf16));
        shape.put("control-height-px", decimal(controlHeightPx));
        shape.put("control-width-px", decimal(controlWidthPx));
        shape.put("scroll-x-px", decimal(scrollXPx));
        shape.put("selection-end-utf16", Integer.toString(selectionEndUtf16));
        shape.put("selection-start-utf16", Integer.toString(selectionStartUtf16));
      }
      if (category == Category.TEXTAREA) {
        shape.put(
            "deferred-suffix-code-point-count",
            Integer.toString(expectedShape.deferredSuffixCodePointCount()));
        shape.put(
            "line-start-kerning-transition-count",
            Integer.toString(expectedShape.lineStartKerningTransitionCount()));
        shape.put("scroll-y-px", decimal(scrollYPx));
        shape.put("wrap-width-px", decimal(wrapWidthPx()));
      }
      return new InputSet(
          BenchmarkInputManifests.content(content),
          BenchmarkInputManifests.shape(shape),
          BenchmarkInputManifests.fonts(
              rendererFontInputs(),
              typographyConfiguration(
                  STYLE.fontSizePx(),
                  STYLE.lineHeight(),
                  STYLE.fontStyle(),
                  STYLE.fontWeight(),
                  STYLE.effectiveFontStretch())));
    }

    @Override
    public Map<String, String> executionSettings() {
      return Map.ofEntries(
          Map.entry("clear-policy", "color-stencil-before-sample"),
          Map.entry("context-visibility", "hidden"),
          Map.entry("native-access", "all-unnamed"),
          Map.entry("predecessor-frame-count", "unchanged".equals(submissionState) ? "1" : "0"),
          Map.entry("prewarm-operation-count", "1"),
          Map.entry("recorded-frame-count", "1"),
          Map.entry("renderer-context-policy", "fresh-context-renderer-and-font-state-per-scenario"),
          Map.entry("reset-policy", "immediately-before-recorded-frame"),
          Map.entry("setup-policy", "exact-scenario-prepared-before-recorded-frame"),
          Map.entry("snapshot-policy", "immediately-after-recorded-frame"),
          Map.entry("swap-interval", "0"),
          Map.entry("thread-count", "1"),
          Map.entry("timing", "none"),
          Map.entry("window-resizable", "false"));
    }

    @Override
    public String evidenceScope() {
      return "aggregate-rendered-items";
    }

    private String controlType() {
      return switch (category) {
        case NORMAL_TEXT -> "none";
        case INPUT -> "input";
        case TEXTAREA -> "textarea";
        case CPU -> throw new IllegalStateException();
      };
    }

    private String rendererPath() {
      return switch (category) {
        case NORMAL_TEXT -> "normal-text";
        case INPUT -> "input-text";
        case TEXTAREA -> "textarea-text";
        case CPU -> throw new IllegalStateException();
      };
    }

    private List<String> stagedFontIdentities() {
      List<String> identities = new ArrayList<>();
      prewarmFonts().stream()
          .map(TextStyleSpecification::fontObjectIdentity)
          .map(value -> "prewarm=" + value)
          .forEach(identities::add);
      layoutFonts().stream()
          .map(TextStyleSpecification::fontObjectIdentity)
          .map(value -> "layout=" + value)
          .forEach(identities::add);
      return List.copyOf(identities);
    }

    private List<FontInput> rendererFontInputs() {
      List<FontInput> inputs = new ArrayList<>();
      prewarmFonts().stream().map(font -> fontInput("prewarm", font)).forEach(inputs::add);
      layoutFonts().stream().map(font -> fontInput("layout", font)).forEach(inputs::add);
      return List.copyOf(inputs);
    }
  }

  public record ExpectedShape(
      int sourceCodePointCount,
      int sourceLineCount,
      int visualLineCount,
      int paragraphCount,
      int fallbackTransitionCount,
      int deferredSuffixCodePointCount,
      int lineStartKerningTransitionCount) {

    public ExpectedShape {
      if (sourceCodePointCount < 0
          || sourceLineCount <= 0
          || visualLineCount < 0
          || paragraphCount <= 0
          || fallbackTransitionCount < 0
          || deferredSuffixCodePointCount < 0
          || lineStartKerningTransitionCount < 0) {
        throw new IllegalArgumentException("Scenario shape counts are outside their domains");
      }
    }

    ExpectedShape withSource(int codePoints, int sourceLines, int paragraphs) {
      return new ExpectedShape(
          codePoints,
          sourceLines,
          visualLineCount,
          paragraphs,
          fallbackTransitionCount,
          deferredSuffixCodePointCount,
          lineStartKerningTransitionCount);
    }
  }

  public record ObservedShape(
      String sourceTextSha256,
      int sourceCodePointCount,
      int sourceUtf16Length,
      int sourceLineCount,
      int visualLineCount,
      int paragraphCount,
      int fallbackTransitionCount,
      int deferredSuffixCodePointCount,
      int lineStartKerningTransitionCount,
      long processedSourceCodePointCount,
      long resolvedGlyphCount,
      long resolvedRunCount,
      long textFragmentCount) {

    public ObservedShape {
      if (sourceTextSha256 == null || !sourceTextSha256.matches("sha256:[0-9a-f]{64}")) {
        throw new IllegalArgumentException("Observed source-text SHA-256 is invalid");
      }
      if (sourceCodePointCount < 0
          || sourceUtf16Length < 0
          || sourceLineCount <= 0
          || visualLineCount < 0
          || paragraphCount <= 0
          || fallbackTransitionCount < 0
          || deferredSuffixCodePointCount < 0
          || lineStartKerningTransitionCount < 0
          || processedSourceCodePointCount < 0
          || resolvedGlyphCount < 0
          || resolvedRunCount < 0
          || textFragmentCount < 0) {
        throw new IllegalArgumentException("Observed evidence counts cannot be negative");
      }
    }
  }

  public record Rect(float xPx, float yPx, float widthPx, float heightPx) {
    public Rect {
      if (!Float.isFinite(xPx)
          || !Float.isFinite(yPx)
          || !(widthPx > 0)
          || !(heightPx > 0)
          || !Float.isFinite(widthPx)
          || !Float.isFinite(heightPx)) {
        throw new IllegalArgumentException("Invalid renderer rectangle");
      }
    }

    public float offscreenRatio(float frameWidth, float frameHeight) {
      float intersectionWidth = Math.max(0, Math.min(xPx + widthPx, frameWidth) - Math.max(xPx, 0));
      float intersectionHeight = Math.max(0, Math.min(yPx + heightPx, frameHeight) - Math.max(yPx, 0));
      return 1 - (intersectionWidth * intersectionHeight) / (widthPx * heightPx);
    }

    public float offscreenExtent(float frameWidth, float frameHeight) {
      return Math.max(
          Math.max(Math.max(0, -xPx), Math.max(0, xPx + widthPx - frameWidth)),
          Math.max(Math.max(0, -yPx), Math.max(0, yPx + heightPx - frameHeight)));
    }

    public String visibility(float frameWidth, float frameHeight) {
      float ratio = offscreenRatio(frameWidth, frameHeight);
      if (Float.compare(ratio, 0) == 0) return "visible";
      if (Float.compare(ratio, 1) == 0) return "offscreen";
      return "mixed";
    }
  }

  private static CpuScenario cpu(
      String name,
      String text,
      float width,
      boolean wordWrap,
      int visualLines,
      int fallbackTransitions,
      int deferredSuffix,
      int lineStartTransitions) {
    return new CpuScenario(
        name,
        "scaled-" + name + "-v1",
        text,
        PREWARM_FONTS,
        16,
        1.2f,
        name.contains("deferred") ? 0.5f : 0,
        width,
        wordWrap,
        new ExpectedShape(
            codePointCount(text),
            sourceLineCount(text),
            visualLines,
            paragraphCount(text),
            fallbackTransitions,
            deferredSuffix,
            lineStartTransitions));
  }

  private static List<RendererScenario> createRendererScenarios() {
    RendererScenario normalVisible =
        renderer(
            "normal-visible-changed",
            Category.NORMAL_TEXT,
            "alternating-latin-mixed-cjk-counter-v1",
            NORMAL_RENDERER_SOURCES,
            4,
            new Rect(20, 20, 640, 160),
            640,
            160,
            0,
            0,
            0,
            "changed",
            4,
            8,
            0);
    RendererScenario inputVisible =
        renderer(
            "input-visible-changed",
            Category.INPUT,
            "input-selection-text-v1",
            List.of(INPUT_RENDERER_TEXT),
            4,
            new Rect(20, 20, 320, 32),
            320,
            32,
            4,
            12,
            12,
            "changed",
            4,
            0,
            0);
    RendererScenario textareaVisible =
        renderer(
            "textarea-visible-changed",
            Category.TEXTAREA,
            "multi-paragraph-wrapped-fallback-v1",
            List.of(TEXTAREA_RENDERER_TEXT),
            1,
            new Rect(20, 20, 48, 160),
            48,
            160,
            2,
            14,
            14,
             "changed",
             8,
             2,
             4);
    return List.of(
        normalVisible,
        withGeometry(normalVisible, "normal-offscreen-changed", new Rect(1280, 20, 640, 160), "changed"),
        withGeometry(normalVisible, "normal-visible-unchanged", normalVisible.container(), "unchanged"),
        inputVisible,
        withSelectionAndGeometry(
            inputVisible,
            "input-offscreen-full-selection",
            new Rect(1280, 20, 320, 32),
            0,
            INPUT_RENDERER_TEXT.length(),
            INPUT_RENDERER_TEXT.length(),
            "changed"),
        withSelectionAndGeometry(
            inputVisible,
            "input-visible-unchanged",
            inputVisible.container(),
            4,
            12,
            12,
            "unchanged"),
        textareaVisible,
        withSelectionAndGeometry(
            textareaVisible,
            "textarea-offscreen-full-selection",
            new Rect(1280, 20, 48, 160),
            0,
            TEXTAREA_RENDERER_TEXT.length(),
            TEXTAREA_RENDERER_TEXT.length(),
            "changed"),
        withSelectionAndGeometry(
            textareaVisible,
            "textarea-visible-unchanged",
            textareaVisible.container(),
            2,
            14,
            14,
            "unchanged"));
  }

  private static RendererScenario renderer(
      String name,
      Category category,
      String workloadContent,
      List<String> sourceContents,
      int itemCount,
      Rect container,
      float controlWidth,
      float controlHeight,
      int selectionStart,
      int selectionEnd,
      int caret,
      String submission,
      int visualLines,
      int fallbackTransitions,
      int lineStartTransitions) {
    int sourceCodePoints = aggregate(sourceContents, itemCount, DiagnosticWorkloadSpecifications::codePointCount);
    int sourceLines = aggregate(sourceContents, itemCount, DiagnosticWorkloadSpecifications::sourceLineCount);
    int paragraphs = aggregate(sourceContents, itemCount, DiagnosticWorkloadSpecifications::paragraphCount);
    return new RendererScenario(
        name,
        category,
        workloadContent,
        sourceContents,
        itemCount,
        container,
        controlWidth,
        controlHeight,
        selectionStart,
        selectionEnd,
        caret,
        0,
        0,
        submission,
        new ExpectedShape(
            sourceCodePoints,
            sourceLines,
            visualLines,
            paragraphs,
            fallbackTransitions,
            0,
            lineStartTransitions));
  }

  private static RendererScenario withGeometry(
      RendererScenario base, String name, Rect geometry, String submission) {
    return withSelectionAndGeometry(
        base,
        name,
        geometry,
        base.selectionStartUtf16(),
        base.selectionEndUtf16(),
        base.caretIndexUtf16(),
        submission);
  }

  private static RendererScenario withSelectionAndGeometry(
      RendererScenario base,
      String name,
      Rect geometry,
      int selectionStart,
      int selectionEnd,
      int caret,
      String submission) {
    return new RendererScenario(
        name,
        base.category(),
        base.workloadContent(),
        base.sourceContents(),
        base.itemCount(),
        geometry,
        base.category() == Category.TEXTAREA ? geometry.widthPx() : base.controlWidthPx(),
        base.controlHeightPx(),
        selectionStart,
        selectionEnd,
        caret,
        base.scrollXPx(),
        base.scrollYPx(),
        submission,
        base.expectedShape());
  }

  private static WorkloadIdentity identity(
      Category category,
      String operation,
      String workload,
      Map<Dimension, Object> values,
      String label) {
    WorkloadIdentity.Builder builder = WorkloadIdentity.e5(workload);
    for (Dimension dimension : WorkloadIdentity.requiredDimensions(category, operation)) {
      Object value = values.get(dimension);
      if (value == null) {
        throw new IllegalStateException("Missing " + dimension.key() + " for " + label);
      }
      builder.dimension(dimension, value);
    }
    return builder.build(label);
  }

  private static void putShape(Map<Dimension, Object> values, ExpectedShape shape) {
    values.put(Dimension.DECLARED_SOURCE_LINE_COUNT, shape.sourceLineCount());
    values.put(Dimension.DECLARED_VISUAL_LINE_COUNT, shape.visualLineCount());
    values.put(Dimension.DEFERRED_SUFFIX_CODE_POINT_COUNT, shape.deferredSuffixCodePointCount());
    values.put(Dimension.FALLBACK_TRANSITION_COUNT, shape.fallbackTransitionCount());
    values.put(Dimension.LINE_START_KERNING_TRANSITION_COUNT, shape.lineStartKerningTransitionCount());
    values.put(Dimension.PARAGRAPH_COUNT, shape.paragraphCount());
    values.put(Dimension.SOURCE_CODE_POINT_COUNT, shape.sourceCodePointCount());
  }

  private static Map<String, String> shapeManifest(ExpectedShape shape) {
    Map<String, String> values = new LinkedHashMap<>();
    values.put("declared-source-line-count", Integer.toString(shape.sourceLineCount()));
    values.put("declared-visual-line-count", Integer.toString(shape.visualLineCount()));
    values.put("deferred-suffix-code-point-count", Integer.toString(shape.deferredSuffixCodePointCount()));
    values.put("fallback-transition-count", Integer.toString(shape.fallbackTransitionCount()));
    values.put("line-start-kerning-transition-count", Integer.toString(shape.lineStartKerningTransitionCount()));
    values.put("paragraph-count", Integer.toString(shape.paragraphCount()));
    values.put("source-code-point-count", Integer.toString(shape.sourceCodePointCount()));
    return values;
  }

  private static void removeInapplicableRendererShape(Map<String, String> shape) {
    shape.remove("deferred-suffix-code-point-count");
    shape.remove("line-start-kerning-transition-count");
  }

  private static Map<String, String> typographyConfiguration(
      float fontSize,
      float lineHeight,
      FontStyle fontStyle,
      FontWeight fontWeight,
      FontStretch fontStretch) {
    return Map.of(
        "font-size-px", decimal(fontSize),
        "font-style", fontStyle.name(),
        "font-stretch", fontStretch.name(),
        "font-weight", fontWeight.name(),
        "line-height", decimal(lineHeight));
  }

  private static FontInput fontInput(String role, Font font) {
    return new FontInput(role, TextStyleSpecification.fontObjectIdentity(font), font.path());
  }

  private static List<String> fontIdentities(List<Font> fonts) {
    return fonts.stream().map(TextStyleSpecification::fontObjectIdentity).toList();
  }

  private static ExpectedShape aggregateSourceShape(
      List<String> sourceContents, int itemCount, ExpectedShape expected) {
    return expected.withSource(
        aggregate(sourceContents, itemCount, DiagnosticWorkloadSpecifications::codePointCount),
        aggregate(sourceContents, itemCount, DiagnosticWorkloadSpecifications::sourceLineCount),
        aggregate(sourceContents, itemCount, DiagnosticWorkloadSpecifications::paragraphCount));
  }

  private static int aggregate(
      List<String> sourceContents,
      int itemCount,
      java.util.function.ToIntFunction<String> counter) {
    int total = 0;
    for (int index = 0; index < itemCount; index++) {
      total = Math.addExact(total, counter.applyAsInt(sourceContents.get(index % sourceContents.size())));
    }
    return total;
  }

  private static void requireNameAndContent(String name, String workloadContent) {
    if (name == null || name.isBlank() || workloadContent == null || workloadContent.isBlank()) {
      throw new IllegalArgumentException("Scenario identity fields cannot be blank");
    }
  }

  private static int sourceLineCount(String text) {
    return text.split("\n", -1).length;
  }

  private static int paragraphCount(String text) {
    return sourceLineCount(text);
  }

  private static int codePointCount(String text) {
    return text.codePointCount(0, text.length());
  }

  static String sourceTextSha256(List<String> sources) {
    Map<String, String> manifest = new LinkedHashMap<>();
    for (int index = 0; index < sources.size(); index++) {
      manifest.put(String.format(java.util.Locale.ROOT, "source-%04d", index), sources.get(index));
    }
    return BenchmarkInputManifests.content(manifest).sha256();
  }

  private static String decimal(float value) {
    if (Float.compare(value, 0) == 0) return "0";
    return new java.math.BigDecimal(Float.toString(value)).stripTrailingZeros().toPlainString();
  }
}
