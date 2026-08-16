package com.spinyowl.spinygui.benchmark.cpu;

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
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Canonical current CPU operations consumed by both JMH execution and identity construction. */
public final class CpuWorkloadSpecifications {
  public static final float FONT_SIZE_PX = 16;
  public static final float LINE_HEIGHT = 1.2f;
  public static final float WRAP_WIDTH_PX = 240;
  public static final float MEASUREMENT_OFFSET_X_PX = 0;
  public static final boolean ROUND_TO_PIXEL = false;

  public static final List<Font> FALLBACK_FONT_CHAIN =
      List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR);

  public static final MeasurementSpec MEASURE_LATIN =
      MeasurementSpec.direct(
          "measureLatin", "latin-v1", TextWorkloads.LATIN, Font.DEFAULT, 1);
  public static final MeasurementSpec MEASURE_WRAPPED_PARAGRAPH =
      MeasurementSpec.wrappedDirect(
          "measureWrappedParagraph",
          "wrapped-paragraph-v1",
          TextWorkloads.WRAPPED_PARAGRAPH,
          Font.DEFAULT,
          WRAP_WIDTH_PX,
          true,
          1);
  public static final MeasurementSpec MEASURE_MIXED_CJK =
      MeasurementSpec.chain(
          "measureMixedCjk", "mixed-cjk-v1", TextWorkloads.MIXED_CJK, FALLBACK_FONT_CHAIN, 1);
  public static final MeasurementSpec MEASURE_SUPPLEMENTARY_UNICODE =
      MeasurementSpec.chain(
          "measureSupplementaryUnicode",
          "supplementary-unicode-v1",
          TextWorkloads.SUPPLEMENTARY_UNICODE,
          FALLBACK_FONT_CHAIN,
          1);
  public static final MeasurementSpec MEASURE_MISSING_GLYPHS =
      MeasurementSpec.chain(
          "measureMissingGlyphs",
          "missing-glyphs-v1",
          TextWorkloads.MISSING_GLYPHS,
          FALLBACK_FONT_CHAIN,
          1);
  public static final MeasurementSpec MEASURE_LONG_SINGLE_FONT =
      MeasurementSpec.direct(
          "measureLongSingleFont",
          "long-single-font-v1",
          TextWorkloads.LONG_SINGLE_FONT,
          Font.DEFAULT,
          TextWorkloads.LONG_SINGLE_FONT_REPEAT_COUNT);
  public static final CaretSpec FIND_CARET_NEAR_BEGINNING =
      new CaretSpec(
          "findCaretNearBeginning",
          "long-single-font-v1",
          TextWorkloads.LONG_SINGLE_FONT,
          Font.DEFAULT,
          FONT_SIZE_PX,
          TextWorkloads.LONG_SINGLE_FONT_REPEAT_COUNT,
          CaretOffsetPolicy.FIXED,
          1,
          null);
  public static final CaretSpec FIND_CARET_NEAR_END =
      new CaretSpec(
          "findCaretNearEnd",
          "long-single-font-v1",
          TextWorkloads.LONG_SINGLE_FONT,
          Font.DEFAULT,
          FONT_SIZE_PX,
          TextWorkloads.LONG_SINGLE_FONT_REPEAT_COUNT,
          CaretOffsetPolicy.MEASURED_WIDTH_MINUS_INSET,
          1,
          LINE_HEIGHT);
  public static final InlineLayoutSpec LAYOUT_DENSE_INLINE_CONTENT =
      new InlineLayoutSpec(
          "layoutTextDenseInlineContent",
          "wrapped-paragraph-v1",
          TextWorkloads.WRAPPED_PARAGRAPH,
          3,
          WRAP_WIDTH_PX,
          0,
          0,
          new TextStyleSpecification(
              List.of(Font.DEFAULT),
              List.of(Font.ROBOTO_REGULAR, Font.ROBOTO_LIGHT, Font.ROBOTO_BOLD),
              FontStyle.NORMAL,
              FontWeight.NORMAL,
              FontStretch.NORMAL,
              FONT_SIZE_PX,
              LINE_HEIGHT,
              Color.BLACK,
              Display.BLOCK,
              Position.STATIC,
              WhiteSpace.NORMAL,
              TextAlign.LEFT,
              OverflowWrap.NORMAL,
              WordBreak.NORMAL,
              4));
  public static final TrialSetupSpec TRIAL_SETUP =
      new TrialSetupSpec(
          ROUND_TO_PIXEL,
          currentFontWarmups(),
          FIND_CARET_NEAR_END,
          LAYOUT_DENSE_INLINE_CONTENT,
          "service-and-operation-fixtures-created-once-and-reused-through-trial",
          "current-corpus-in-trial-setup",
          "default");

  private static final Map<String, OperationSpec> CURRENT_OPERATIONS = createCurrentOperations();

  private CpuWorkloadSpecifications() {
  }

  public static Map<String, OperationSpec> currentOperations() {
    return CURRENT_OPERATIONS;
  }

  /** Complete current JMH settings emitted into every CPU comparability fingerprint. */
  public static Map<String, String> currentExecutionSettings() {
    return Map.ofEntries(
        Map.entry("benchmark-mode", "average-time"),
        Map.entry("forks", "2"),
        Map.entry("measurement-batch-size", "1"),
        Map.entry("measurement-iterations", "5"),
        Map.entry("measurement-time", "PT0.5S"),
        Map.entry("native-access", "all-unnamed"),
        Map.entry("output-time-unit", "microseconds"),
        Map.entry("profiler", "gc"),
        Map.entry("state-scope", "benchmark"),
        Map.entry("threads", "1"),
        Map.entry("warmup-batch-size", "1"),
        Map.entry("warmup-forks", "0"),
        Map.entry("warmup-iterations", "3"),
        Map.entry("warmup-time", "PT0.5S"));
  }

  /** Builds the exact T1 semantic identity used by the current CPU output producer. */
  public static WorkloadIdentity identity(OperationSpec specification) {
    Objects.requireNonNull(specification, "specification");
    var requiredDimensions =
        WorkloadIdentity.requiredDimensions(Category.CPU, specification.operation());
    EnumMap<Dimension, Object> dimensions = new EnumMap<>(Dimension.class);
    dimensions.putAll(specification.identityDimensions());
    dimensions.put(Dimension.BENCHMARK_CLASS, TextCalculationBenchmark.class.getName());
    dimensions.put(Dimension.BENCHMARK_MODE, "average-time");
    dimensions.put(Dimension.CATEGORY, "cpu");
    dimensions.put(Dimension.FORKS, 2);
    dimensions.put(Dimension.HARNESS, "jmh");
    dimensions.put(Dimension.MEASUREMENT_BATCH_SIZE, 1);
    dimensions.put(Dimension.MEASUREMENT_ITERATIONS, 5);
    dimensions.put(Dimension.MEASUREMENT_TIME, "500ms");
    dimensions.put(Dimension.NATIVE_ACCESS, "all-unnamed");
    dimensions.put(Dimension.OUTPUT_TIME_UNIT, "microseconds");
    dimensions.put(Dimension.PROFILER, "gc");
    dimensions.put(Dimension.STATE_SCOPE, "benchmark");
    dimensions.put(Dimension.THREADS, 1);
    dimensions.put(Dimension.WARMUP_BATCH_SIZE, 1);
    dimensions.put(Dimension.WARMUP_FORKS, 0);
    dimensions.put(Dimension.WARMUP_ITERATIONS, 3);
    dimensions.put(Dimension.WARMUP_TIME, "500ms");

    WorkloadIdentity.Builder builder = WorkloadIdentity.e5("cpu-text");
    for (Dimension dimension : requiredDimensions) {
      Object value = dimensions.get(dimension);
      if (value == null) {
        throw new IllegalStateException(
            "Missing producer identity dimension " + dimension.key()
                + " for " + specification.operation());
      }
      builder.dimension(dimension, value);
    }
    return builder.build("CPU " + specification.operation());
  }

  public static ComparabilityMetadata comparability(
      OperationSpec specification,
      String benchmarkVersion,
      EvidenceMode evidenceMode,
      ComparabilityMetadata.Environment environment,
      ComparabilityMetadata.Implementation implementation) {
    WorkloadIdentity identity = identity(specification);
    InputSet manifests = inputManifests(specification, identity);
    return new ComparabilityMetadata(
        benchmarkVersion,
        identity.dimensions().get(Dimension.WORKLOAD_VERSION),
        "jmh-json-comparability-2",
        "text-behavior-1",
        evidenceMode,
        identity.semanticId(),
        identity.displayLabel(),
        manifests.content().sha256(),
        manifests.shape().sha256(),
        manifests.fonts().sha256(),
        environment,
        currentExecutionSettings(),
        implementation);
  }

  public static InputSet inputManifests(OperationSpec specification) {
    return inputManifests(specification, identity(specification));
  }

  private static InputSet inputManifests(
      OperationSpec specification, WorkloadIdentity identity) {
    Map<String, String> shape = new LinkedHashMap<>();
    shape.put(
        "shape-kind",
        switch (specification) {
          case MeasurementSpec ignored -> "measurement";
          case CaretSpec ignored -> "caret";
          case InlineLayoutSpec ignored -> "inline-layout";
        });
    EnumSet<Dimension> shapeDimensions =
        switch (specification) {
          case MeasurementSpec ignored ->
              EnumSet.of(
                  Dimension.CONTENT_REPEAT_COUNT,
                  Dimension.LINE_HEIGHT,
                  Dimension.MEASUREMENT_OFFSET_X_PX,
                  Dimension.WRAP_WIDTH_PX,
                  Dimension.WRAP_WIDTH_POLICY,
                  Dimension.WRAPPING_POLICY);
          case CaretSpec ignored ->
              EnumSet.of(
                  Dimension.CARET_OFFSET_INSET_X_PX,
                  Dimension.CARET_OFFSET_POLICY,
                  Dimension.CARET_OFFSET_X_PX,
                  Dimension.CONTENT_REPEAT_COUNT,
                  Dimension.LINE_HEIGHT);
          case InlineLayoutSpec ignored ->
              EnumSet.of(
                  Dimension.COLOR,
                  Dimension.CONTAINER_HEIGHT_PX,
                  Dimension.CONTAINER_WIDTH_PX,
                  Dimension.DISPLAY,
                  Dimension.INLINE_LAYOUT_START_Y_PX,
                  Dimension.LINE_HEIGHT,
                  Dimension.OVERFLOW_WRAP,
                  Dimension.POSITION,
                  Dimension.TAB_SIZE,
                  Dimension.TEXT_ALIGN,
                  Dimension.TEXT_NODE_COUNT,
                  Dimension.WHITE_SPACE,
                  Dimension.WORD_BREAK,
                  Dimension.WRAPPING_POLICY);
        };
    for (Dimension dimension : shapeDimensions) {
      String value = identity.dimensions().get(dimension);
      if (value != null) shape.put(dimension.key(), value);
    }

    List<Font> fonts =
        switch (specification) {
          case MeasurementSpec measurement -> measurement.orderedFonts();
          case CaretSpec caret -> List.of(caret.font());
          case InlineLayoutSpec inline -> inline.style().resolvedFonts();
        };
    List<FontInput> fontInputs =
        fonts.stream()
            .map(
                font ->
                    new FontInput(
                        "cpu-" + shape.get("shape-kind"),
                        TextStyleSpecification.fontObjectIdentity(font),
                        font.path()))
            .toList();
    return new InputSet(
        BenchmarkInputManifests.content(Map.of("text", specification.text())),
        BenchmarkInputManifests.shape(shape),
        BenchmarkInputManifests.fonts(
            fontInputs,
            Map.of("font-size-px", identity.dimensions().get(Dimension.FONT_SIZE_PX))));
  }

  /** Selects the measured operation from the benchmark name supplied by JMH. */
  public static BenchmarkDispatch dispatchForBenchmark(String benchmarkName) {
    Objects.requireNonNull(benchmarkName, "benchmarkName");
    int separator = benchmarkName.lastIndexOf('.');
    String operation = separator < 0 ? benchmarkName : benchmarkName.substring(separator + 1);
    OperationSpec specification = CURRENT_OPERATIONS.get(operation);
    if (specification == null) {
      throw new IllegalArgumentException("Unknown CPU benchmark operation: " + benchmarkName);
    }
    return new BenchmarkDispatch(operation, specification);
  }

  public record BenchmarkDispatch(String operation, OperationSpec specification) {

    public BenchmarkDispatch {
      Objects.requireNonNull(operation, "operation");
      Objects.requireNonNull(specification, "specification");
      if (!operation.equals(specification.operation())) {
        throw new IllegalArgumentException("Dispatch operation does not match its specification");
      }
    }

    public MeasurementSpec measurement() {
      if (specification instanceof MeasurementSpec measurement) {
        return measurement;
      }
      throw new IllegalStateException(operation + " is not a measurement operation");
    }

    public CaretSpec caret() {
      if (specification instanceof CaretSpec caret) {
        return caret;
      }
      throw new IllegalStateException(operation + " is not a caret operation");
    }

    public InlineLayoutSpec inlineLayout() {
      if (specification instanceof InlineLayoutSpec inlineLayout) {
        return inlineLayout;
      }
      throw new IllegalStateException(operation + " is not an inline-layout operation");
    }
  }

  public sealed interface OperationSpec permits MeasurementSpec, CaretSpec, InlineLayoutSpec {
    String operation();

    String workloadContent();

    String text();

    Map<Dimension, Object> identityDimensions();
  }

  public record TrialSetupSpec(
      boolean roundToPixel,
      List<MeasurementSpec> fontWarmups,
      CaretSpec preparedEndCaret,
      InlineLayoutSpec preparedInlineLayout,
      String fixturePreparationPolicy,
      String fontFixturePolicy,
      String fontResolver) {

    public TrialSetupSpec {
      fontWarmups = List.copyOf(fontWarmups);
      Objects.requireNonNull(preparedEndCaret, "preparedEndCaret");
      Objects.requireNonNull(preparedInlineLayout, "preparedInlineLayout");
      Objects.requireNonNull(fixturePreparationPolicy, "fixturePreparationPolicy");
      Objects.requireNonNull(fontFixturePolicy, "fontFixturePolicy");
      Objects.requireNonNull(fontResolver, "fontResolver");
      if (roundToPixel != ROUND_TO_PIXEL) {
        throw new IllegalArgumentException(
            "Current trial setup requires the declared round-to-pixel configuration");
      }
      if (!fontWarmups.equals(currentFontWarmups())) {
        throw new IllegalArgumentException(
            "Current trial setup requires every complete declared font-warmup specification");
      }
      if (!"service-and-operation-fixtures-created-once-and-reused-through-trial"
              .equals(fixturePreparationPolicy)
          || !"current-corpus-in-trial-setup".equals(fontFixturePolicy)
          || !"default".equals(fontResolver)) {
        throw new IllegalArgumentException(
            "Current trial setup requires the declared fixture, font, and resolver policies");
      }
      if (preparedEndCaret != FIND_CARET_NEAR_END
          || preparedInlineLayout != LAYOUT_DENSE_INLINE_CONTENT) {
        throw new IllegalArgumentException(
            "Current trial fixture policy requires the exact identity-producing caret and inline specs");
      }
    }

    public FontServiceImpl createFontService() {
      return createFontService(DiagnosticSession.disabled());
    }

    public FontServiceImpl createFontService(DiagnosticSession diagnostics) {
      if (!"default".equals(fontResolver)) {
        throw new IllegalStateException("Unsupported CPU benchmark font resolver: " + fontResolver);
      }
      FontServiceImpl service =
          new FontServiceImpl(
              new FontStorageImpl(),
              roundToPixel,
              Objects.requireNonNull(diagnostics, "diagnostics"));
      service.installSemanticOwner();
      preparedInlineLayout.style().verifyResolution(service.fontChainResolver());
      return service;
    }

    public void warmFonts(FontServiceImpl fontService) {
      fontWarmups.forEach(specification -> specification.measure(fontService));
    }
  }

  private static List<MeasurementSpec> currentFontWarmups() {
    return List.of(
        MEASURE_LATIN,
        MEASURE_WRAPPED_PARAGRAPH,
        MEASURE_MIXED_CJK,
        MEASURE_SUPPLEMENTARY_UNICODE,
        MEASURE_MISSING_GLYPHS);
  }

  public enum MeasurementApi {
    DIRECT_FONT("measure-text-font"),
    FONT_CHAIN("measure-text-font-list"),
    WRAPPED_DIRECT_FONT("measure-text-wrapped-font");

    private final String identityValue;

    MeasurementApi(String identityValue) {
      this.identityValue = identityValue;
    }
  }

  public record MeasurementSpec(
      String operation,
      String workloadContent,
      String text,
      MeasurementApi api,
      List<Font> orderedFonts,
      float fontSizePx,
      float lineHeight,
      float measurementOffsetXPx,
      Float maximumWidthPx,
      boolean wordWrap,
      int contentRepeatCount) implements OperationSpec {

    public MeasurementSpec {
      Objects.requireNonNull(operation, "operation");
      Objects.requireNonNull(workloadContent, "workloadContent");
      Objects.requireNonNull(text, "text");
      Objects.requireNonNull(api, "api");
      orderedFonts = List.copyOf(orderedFonts);
      if (orderedFonts.isEmpty()) {
        throw new IllegalArgumentException("orderedFonts cannot be empty");
      }
      if (api != MeasurementApi.FONT_CHAIN && orderedFonts.size() != 1) {
        throw new IllegalArgumentException("Direct-font measurement requires exactly one Font");
      }
      requireCurrentContent(workloadContent, text);
    }

    static MeasurementSpec direct(
        String operation, String workloadContent, String text, Font font, int repeatCount) {
      return new MeasurementSpec(
          operation,
          workloadContent,
          text,
          MeasurementApi.DIRECT_FONT,
          List.of(font),
          FONT_SIZE_PX,
          LINE_HEIGHT,
          MEASUREMENT_OFFSET_X_PX,
          null,
          false,
          repeatCount);
    }

    static MeasurementSpec chain(
        String operation, String workloadContent, String text, List<Font> fonts, int repeatCount) {
      return new MeasurementSpec(
          operation,
          workloadContent,
          text,
          MeasurementApi.FONT_CHAIN,
          fonts,
          FONT_SIZE_PX,
          LINE_HEIGHT,
          MEASUREMENT_OFFSET_X_PX,
          null,
          false,
          repeatCount);
    }

    static MeasurementSpec wrappedDirect(
        String operation,
        String workloadContent,
        String text,
        Font font,
        float maximumWidthPx,
        boolean wordWrap,
        int repeatCount) {
      return new MeasurementSpec(
          operation,
          workloadContent,
          text,
          MeasurementApi.WRAPPED_DIRECT_FONT,
          List.of(font),
          FONT_SIZE_PX,
          LINE_HEIGHT,
          MEASUREMENT_OFFSET_X_PX,
          maximumWidthPx,
          wordWrap,
          repeatCount);
    }

    public TextMetrics measure(FontServiceImpl fontService) {
      return switch (api) {
        case DIRECT_FONT ->
            fontService.measureText(text, orderedFonts.getFirst(), fontSizePx, lineHeight);
        case FONT_CHAIN -> fontService.measureText(text, orderedFonts, fontSizePx, lineHeight);
        case WRAPPED_DIRECT_FONT ->
            fontService.measureText(
                text,
                measurementOffsetXPx,
                orderedFonts.getFirst(),
                fontSizePx,
                lineHeight,
                Objects.requireNonNull(maximumWidthPx, "maximumWidthPx"),
                wordWrap);
      };
    }

    @Override
    public Map<Dimension, Object> identityDimensions() {
      Map<Dimension, Object> dimensions = commonDimensions(operation, workloadContent, orderedFonts);
      dimensions.put(Dimension.API, api.identityValue);
      dimensions.put(Dimension.FONT_SIZE_PX, fontSizePx);
      dimensions.put(Dimension.LINE_HEIGHT, lineHeight);
      dimensions.put(Dimension.MEASUREMENT_OFFSET_X_PX, measurementOffsetXPx);
      dimensions.put(Dimension.WRAP_WIDTH_POLICY, maximumWidthPx == null ? "unbounded" : "fixed");
      dimensions.put(
          Dimension.WRAPPING_POLICY,
          maximumWidthPx == null ? "unwrapped" : wordWrap ? "word-wrap" : "character-wrap");
      if (maximumWidthPx != null) {
        dimensions.put(Dimension.WRAP_WIDTH_PX, maximumWidthPx);
      }
      if (contentRepeatCount > 1) {
        dimensions.put(Dimension.CONTENT_REPEAT_COUNT, contentRepeatCount);
      }
      return Collections.unmodifiableMap(dimensions);
    }
  }

  public enum CaretOffsetPolicy {
    FIXED("fixed"),
    MEASURED_WIDTH_MINUS_INSET("measured-width-minus-inset");

    private final String identityValue;

    CaretOffsetPolicy(String identityValue) {
      this.identityValue = identityValue;
    }
  }

  public record CaretSpec(
      String operation,
      String workloadContent,
      String text,
      Font font,
      float fontSizePx,
      int contentRepeatCount,
      CaretOffsetPolicy offsetPolicy,
      float offsetOrInsetXPx,
      Float preparationLineHeight) implements OperationSpec {

    public CaretSpec {
      Objects.requireNonNull(font, "font");
      Objects.requireNonNull(offsetPolicy, "offsetPolicy");
      requireCurrentContent(workloadContent, text);
    }

    public float preparedOffset(FontServiceImpl fontService) {
      if (offsetPolicy == CaretOffsetPolicy.FIXED) {
        return offsetOrInsetXPx;
      }
      return fontService.measureText(
              text,
              font,
              fontSizePx,
              Objects.requireNonNull(preparationLineHeight, "preparationLineHeight"))
          .width() - offsetOrInsetXPx;
    }

    public TextCaretMetrics findCaret(FontServiceImpl fontService, float preparedOffset) {
      return fontService.getTextCaretMetrics(text, font, fontSizePx, preparedOffset);
    }

    @Override
    public Map<Dimension, Object> identityDimensions() {
      Map<Dimension, Object> dimensions =
          commonDimensions(operation, workloadContent, List.of(font));
      dimensions.put(Dimension.API, "get-text-caret-metrics-font");
      dimensions.put(Dimension.FONT_SIZE_PX, fontSizePx);
      dimensions.put(Dimension.CARET_OFFSET_POLICY, offsetPolicy.identityValue);
      dimensions.put(Dimension.CONTENT_REPEAT_COUNT, contentRepeatCount);
      if (offsetPolicy == CaretOffsetPolicy.FIXED) {
        dimensions.put(Dimension.CARET_OFFSET_X_PX, offsetOrInsetXPx);
      } else {
        dimensions.put(Dimension.CARET_OFFSET_INSET_X_PX, offsetOrInsetXPx);
        dimensions.put(Dimension.LINE_HEIGHT, preparationLineHeight);
      }
      return Collections.unmodifiableMap(dimensions);
    }
  }

  public record InlineLayoutSpec(
      String operation,
      String workloadContent,
      String text,
      int textNodeCount,
      float containerWidthPx,
      float containerHeightPx,
      float layoutStartYPx,
      TextStyleSpecification style) implements OperationSpec {

    public InlineLayoutSpec {
      requireCurrentContent(workloadContent, text);
    }

    public float layout(
        InlineFormattingContext context, Element parent, List<Node> nodes) {
      return context.layout(parent, nodes, layoutStartYPx);
    }

    @Override
    public Map<Dimension, Object> identityDimensions() {
      Map<Dimension, Object> dimensions =
          commonDimensions(operation, workloadContent, style.orderedFonts());
      dimensions.put(Dimension.API, "inline-formatting-context-layout");
      dimensions.put(Dimension.FONT_CHAIN, style.resolvedFontObjectIdentities());
      dimensions.put(Dimension.COLOR, colorIdentity(style.color()));
      dimensions.put(Dimension.CONTAINER_HEIGHT_PX, containerHeightPx);
      dimensions.put(Dimension.CONTAINER_WIDTH_PX, containerWidthPx);
      dimensions.put(Dimension.DISPLAY, style.display().name());
      dimensions.put(Dimension.FONT_STRETCH, style.effectiveFontStretch().name());
      dimensions.put(Dimension.FONT_STYLE, style.fontStyle().name());
      dimensions.put(Dimension.FONT_WEIGHT, style.fontWeight().name());
      dimensions.put(Dimension.FONT_SIZE_PX, style.fontSizePx());
      dimensions.put(Dimension.INLINE_LAYOUT_START_Y_PX, layoutStartYPx);
      dimensions.put(Dimension.LINE_HEIGHT, style.lineHeight());
      dimensions.put(Dimension.OVERFLOW_WRAP, style.overflowWrap().name());
      dimensions.put(Dimension.POSITION, style.position().name());
      dimensions.put(Dimension.TAB_SIZE, style.tabSize());
      dimensions.put(Dimension.TEXT_ALIGN, style.textAlign().name());
      dimensions.put(Dimension.TEXT_NODE_COUNT, textNodeCount);
      dimensions.put(Dimension.WHITE_SPACE, style.whiteSpace().name());
      dimensions.put(Dimension.WORD_BREAK, style.wordBreak().name());
      dimensions.put(Dimension.WRAPPING_POLICY, "normal");
      return Collections.unmodifiableMap(dimensions);
    }
  }

  private static Map<Dimension, Object> commonDimensions(
      String operation, String workloadContent, List<Font> fonts) {
    Font primary = fonts.getFirst();
    Map<Dimension, Object> dimensions = new LinkedHashMap<>();
    dimensions.put(Dimension.OPERATION, operation);
    dimensions.put(Dimension.FIXTURE_PREPARATION_POLICY, TRIAL_SETUP.fixturePreparationPolicy());
    dimensions.put(Dimension.FONT_FIXTURE_POLICY, TRIAL_SETUP.fontFixturePolicy());
    dimensions.put(Dimension.FONT_RESOLVER, TRIAL_SETUP.fontResolver());
    dimensions.put(Dimension.SETUP_LEVEL, "trial");
    dimensions.put(Dimension.WORKLOAD_CONTENT, workloadContent);
    dimensions.put(Dimension.WORKLOAD_VERSION, 1);
    dimensions.put(
        Dimension.FONT_CHAIN,
        fonts.stream().map(TextStyleSpecification::fontObjectIdentity).toList());
    dimensions.put(Dimension.FONT_SIZE_PX, FONT_SIZE_PX);
    dimensions.put(Dimension.FONT_STRETCH, primary.stretch().name());
    dimensions.put(Dimension.FONT_STYLE, primary.style().name());
    dimensions.put(Dimension.FONT_WEIGHT, primary.weight().name());
    dimensions.put(Dimension.ROUND_TO_PIXEL, TRIAL_SETUP.roundToPixel());
    return dimensions;
  }

  private static String colorIdentity(Color color) {
    if (Color.BLACK.equals(color)) {
      return "black";
    }
    if (Color.WHITE.equals(color)) {
      return "white";
    }
    throw new IllegalArgumentException("Unsupported identity color: " + color);
  }

  private static void requireCurrentContent(String declaration, String text) {
    String expected =
        switch (declaration) {
          case "latin-v1" -> TextWorkloads.LATIN;
          case "wrapped-paragraph-v1" -> TextWorkloads.WRAPPED_PARAGRAPH;
          case "mixed-cjk-v1" -> TextWorkloads.MIXED_CJK;
          case "supplementary-unicode-v1" -> TextWorkloads.SUPPLEMENTARY_UNICODE;
          case "missing-glyphs-v1" -> TextWorkloads.MISSING_GLYPHS;
          case "long-single-font-v1" -> TextWorkloads.LONG_SINGLE_FONT;
          default -> null;
        };
    if (expected != null && !expected.equals(text)) {
      throw new IllegalArgumentException(
          "Content does not match the canonical declaration " + declaration);
    }
  }

  private static Map<String, OperationSpec> createCurrentOperations() {
    Map<String, OperationSpec> operations = new LinkedHashMap<>();
    for (OperationSpec operation :
        List.of(
            MEASURE_LATIN,
            MEASURE_WRAPPED_PARAGRAPH,
            MEASURE_MIXED_CJK,
            MEASURE_SUPPLEMENTARY_UNICODE,
            MEASURE_MISSING_GLYPHS,
            MEASURE_LONG_SINGLE_FONT,
            FIND_CARET_NEAR_BEGINNING,
            FIND_CARET_NEAR_END,
            LAYOUT_DENSE_INLINE_CONTENT)) {
      if (operations.put(operation.operation(), operation) != null) {
        throw new IllegalStateException("Duplicate CPU operation: " + operation.operation());
      }
    }
    return Collections.unmodifiableMap(operations);
  }
}
