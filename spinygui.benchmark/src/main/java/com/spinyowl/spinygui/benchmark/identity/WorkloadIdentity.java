package com.spinyowl.spinygui.benchmark.identity;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Canonical declared-input identity for benchmark workloads and rendering/control scenarios. */
public final class WorkloadIdentity {
  public static final int IDENTITY_SCHEMA_VERSION = 1;

  private static final String ID_PREFIX = "spinygui-benchmark:v" + IDENTITY_SCHEMA_VERSION;
  private static final String CPU_BENCHMARK_CLASS =
      "com.spinyowl.spinygui.benchmark.cpu.TextCalculationBenchmark";
  private static final Pattern CANONICAL_NAME = Pattern.compile("[a-z][a-z0-9-]*");
  private static final Pattern CLASS_NAME =
      Pattern.compile("[A-Za-z_$][A-Za-z0-9_$]*(?:\\.[A-Za-z_$][A-Za-z0-9_$]*)+");
  private static final Pattern OPERATION = Pattern.compile("[A-Za-z][A-Za-z0-9-]*");
  private static final Pattern DURATION = Pattern.compile("([0-9]+)\\s*(ns|us|ms|s|m)");
  private static final Pattern CAMEL_BOUNDARY = Pattern.compile("([a-z0-9])([A-Z])");
  private static final Comparator<Dimension> DIMENSION_ORDER =
      Comparator.comparing(Dimension::key);
  private static final String HEX = "0123456789ABCDEF";

  private static final Set<Dimension> TIMED_CPU_COMMON =
      dimensions(
          Dimension.API,
          Dimension.BENCHMARK_CLASS,
          Dimension.BENCHMARK_MODE,
          Dimension.CATEGORY,
          Dimension.FONT_CHAIN,
          Dimension.FONT_FIXTURE_POLICY,
          Dimension.FONT_RESOLVER,
          Dimension.FONT_SIZE_PX,
          Dimension.FONT_STRETCH,
          Dimension.FONT_STYLE,
          Dimension.FONT_WEIGHT,
          Dimension.FORKS,
          Dimension.FIXTURE_PREPARATION_POLICY,
          Dimension.HARNESS,
          Dimension.MEASUREMENT_BATCH_SIZE,
          Dimension.MEASUREMENT_ITERATIONS,
          Dimension.MEASUREMENT_TIME,
          Dimension.NATIVE_ACCESS,
          Dimension.OPERATION,
          Dimension.OUTPUT_TIME_UNIT,
          Dimension.PROFILER,
          Dimension.ROUND_TO_PIXEL,
          Dimension.SETUP_LEVEL,
          Dimension.STATE_SCOPE,
          Dimension.THREADS,
          Dimension.WARMUP_BATCH_SIZE,
          Dimension.WARMUP_FORKS,
          Dimension.WARMUP_ITERATIONS,
          Dimension.WARMUP_TIME,
          Dimension.WORKLOAD_CONTENT,
          Dimension.WORKLOAD_VERSION);
  private static final Set<Dimension> COUNTER_CPU_COMMON =
      dimensions(
          Dimension.API,
          Dimension.CATEGORY,
          Dimension.FONT_CHAIN,
          Dimension.FONT_FIXTURE_POLICY,
          Dimension.FONT_RESOLVER,
          Dimension.FONT_SIZE_PX,
          Dimension.FONT_STRETCH,
          Dimension.FONT_STYLE,
          Dimension.FONT_WEIGHT,
          Dimension.FIXTURE_PREPARATION_POLICY,
          Dimension.HARNESS,
          Dimension.NATIVE_ACCESS,
          Dimension.OPERATION,
          Dimension.ROUND_TO_PIXEL,
          Dimension.SETUP_LEVEL,
          Dimension.WORKLOAD_CONTENT,
          Dimension.WORKLOAD_VERSION);

  private static final Set<Dimension> MEASURE_TEXT =
      dimensions(
          Dimension.LINE_HEIGHT,
          Dimension.MEASUREMENT_OFFSET_X_PX,
          Dimension.WRAP_WIDTH_POLICY,
          Dimension.WRAPPING_POLICY);
  private static final Set<Dimension> WRAPPED_MEASURE_TEXT =
      dimensions(
          Dimension.LINE_HEIGHT,
          Dimension.MEASUREMENT_OFFSET_X_PX,
          Dimension.WRAP_WIDTH_PX,
          Dimension.WRAP_WIDTH_POLICY,
          Dimension.WRAPPING_POLICY);
  private static final Set<Dimension> LONG_MEASURE_TEXT =
      dimensions(
          Dimension.CONTENT_REPEAT_COUNT,
          Dimension.LINE_HEIGHT,
          Dimension.MEASUREMENT_OFFSET_X_PX,
          Dimension.WRAP_WIDTH_POLICY,
          Dimension.WRAPPING_POLICY);
  private static final Set<Dimension> CARET_BEGINNING =
      dimensions(
          Dimension.CARET_OFFSET_POLICY,
          Dimension.CARET_OFFSET_X_PX,
          Dimension.CONTENT_REPEAT_COUNT);
  private static final Set<Dimension> CARET_END =
      dimensions(
          Dimension.CARET_OFFSET_INSET_X_PX,
          Dimension.CARET_OFFSET_POLICY,
          Dimension.CONTENT_REPEAT_COUNT,
          Dimension.LINE_HEIGHT);
  private static final Set<Dimension> INLINE_LAYOUT =
      dimensions(
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
  private static final Set<Dimension> PARAMETERIZED_MEASURE_TEXT =
      dimensions(
          Dimension.DECLARED_SOURCE_LINE_COUNT,
          Dimension.DECLARED_VISUAL_LINE_COUNT,
          Dimension.DEFERRED_SUFFIX_CODE_POINT_COUNT,
          Dimension.FALLBACK_TRANSITION_COUNT,
          Dimension.LINE_HEIGHT,
          Dimension.LINE_START_KERNING_TRANSITION_COUNT,
          Dimension.MEASUREMENT_OFFSET_X_PX,
          Dimension.PARAGRAPH_COUNT,
          Dimension.SOURCE_CODE_POINT_COUNT,
          Dimension.WRAP_WIDTH_PX,
          Dimension.WRAP_WIDTH_POLICY,
          Dimension.WRAPPING_POLICY);

  private static final Map<String, IdentitySchema> CPU_SCHEMAS = cpuSchemas();

  private static final Set<Dimension> RENDERER_COMMON =
      dimensions(
          Dimension.API,
          Dimension.CATEGORY,
          Dimension.CLEAR_POLICY,
          Dimension.CLIP_STATE,
          Dimension.COLOR,
          Dimension.CONTAINER_HEIGHT_PX,
          Dimension.CONTAINER_POSITION_X_PX,
          Dimension.CONTAINER_POSITION_Y_PX,
          Dimension.CONTAINER_WIDTH_PX,
          Dimension.CONTEXT_VISIBILITY,
          Dimension.CONTENT_ALTERNATION,
          Dimension.CONTROL_TYPE,
          Dimension.DISPLAY,
          Dimension.FONT_CHAIN,
          Dimension.FONT_FIXTURE_POLICY,
          Dimension.FONT_RESOLVER,
          Dimension.FONT_SIZE_PX,
          Dimension.FONT_STRETCH,
          Dimension.FONT_STYLE,
          Dimension.FONT_WEIGHT,
          Dimension.FRAME_HEIGHT_PX,
          Dimension.FRAME_WIDTH_PX,
          Dimension.HARNESS,
          Dimension.INLINE_LAYOUT_START_Y_PX,
          Dimension.LINE_HEIGHT,
          Dimension.MEASURED_FRAMES,
          Dimension.MEASUREMENT_ORDER,
          Dimension.MEASUREMENT_ORDER_INDEX,
          Dimension.NATIVE_ACCESS,
          Dimension.OPERATION,
          Dimension.OVERFLOW_WRAP,
          Dimension.POSITION,
          Dimension.PREWARM_WORKLOAD_CONTENT,
          Dimension.PREMEASURE_SEQUENCE,
          Dimension.RENDERER_PATH,
          Dimension.ROUND_TO_PIXEL,
          Dimension.SCENE_HEIGHT_PX,
          Dimension.SCENE_WIDTH_PX,
          Dimension.SUBMISSION_STATE,
          Dimension.SWAP_INTERVAL,
          Dimension.SYNCHRONIZATION,
          Dimension.TAB_SIZE,
          Dimension.TEXT_ALIGN,
          Dimension.VALIDATION_POLICY,
          Dimension.VISIBILITY,
          Dimension.WARMUP_FRAMES,
          Dimension.WINDOW_RESIZABLE,
          Dimension.WHITE_SPACE,
          Dimension.WORD_BREAK,
          Dimension.WORKLOAD_CONTENT,
          Dimension.WORKLOAD_VERSION,
          Dimension.WRAPPING_POLICY);
  private static final Set<Dimension> CURRENT_RENDERER =
      dimensions(
          Dimension.COMPANION_SCENE_SHAPE,
          Dimension.COMPANION_TEXT_NODE_COUNT,
          Dimension.CONTENT_TRANSFORM,
          Dimension.FIXTURE_PREPARATION_POLICY,
          Dimension.SCENE_PAIR_COUNT,
          Dimension.SETUP_LEVEL,
          Dimension.TEXT_NODE_COUNT,
          Dimension.WARMUP_ORDER);
  private static final Set<Dimension> PLANNED_RENDERER =
      dimensions(
          Dimension.DECLARED_SOURCE_LINE_COUNT,
          Dimension.DECLARED_VISUAL_LINE_COUNT,
          Dimension.FALLBACK_TRANSITION_COUNT,
          Dimension.FIXTURE_PREPARATION_POLICY,
          Dimension.OFFSCREEN_EXTENT_PX,
          Dimension.OFFSCREEN_RATIO,
          Dimension.PARAGRAPH_COUNT,
          Dimension.SETUP_LEVEL,
          Dimension.SOURCE_CODE_POINT_COUNT,
          Dimension.SOURCE_UTF16_LENGTH,
          Dimension.TEXT_NODE_COUNT);
  private static final Set<Dimension> CONTROL_RENDERER =
      dimensions(
          Dimension.CARET_INDEX_UTF16,
          Dimension.CARET_STATE,
          Dimension.CONTROL_HEIGHT_PX,
           Dimension.CONTROL_STATE,
           Dimension.CONTROL_WIDTH_PX,
           Dimension.SCROLL_X_PX,
           Dimension.SELECTION_END_UTF16,
           Dimension.SELECTION_START_UTF16);
  private static final Set<Dimension> TEXTAREA_RENDERER =
      dimensions(
          Dimension.DEFERRED_SUFFIX_CODE_POINT_COUNT,
           Dimension.LINE_START_KERNING_TRANSITION_COUNT,
           Dimension.MEASUREMENT_OFFSET_X_PX,
           Dimension.SCROLL_Y_PX,
           Dimension.WRAP_WIDTH_PX,
           Dimension.WRAP_WIDTH_POLICY);

  private static final Map<Category, Map<String, IdentitySchema>> RENDERER_SCHEMAS =
      rendererSchemas();

  private final Namespace namespace;
  private final String workload;
  private final SortedMap<Dimension, String> dimensions;
  private final String displayLabel;
  private final String semanticId;

  private WorkloadIdentity(
      Namespace namespace,
      String workload,
      Map<Dimension, String> dimensions,
      String displayLabel) {
    this.namespace = Objects.requireNonNull(namespace, "namespace");
    this.workload = canonicalName(workload, "workload");
    this.dimensions = immutableDimensions(dimensions);
    this.displayLabel = normalizedText(displayLabel, "displayLabel");
    validateDimensions();
    semanticId = serialize();
  }

  /** Starts an E5 identity whose exact schema is selected by its category and operation. */
  public static Builder e5(String workload) {
    return new Builder(workload);
  }

  /**
   * Addresses one historical E4 series without pretending that the archived unparameterized result
   * had E5 dimensions.
   */
  public static WorkloadIdentity legacyE4(String historicalSeriesKey, String displayLabel) {
    EnumMap<Dimension, String> dimensions = new EnumMap<>(Dimension.class);
    dimensions.put(
        Dimension.HISTORICAL_SERIES_KEY,
        normalizedText(historicalSeriesKey, "historicalSeriesKey"));
    return new WorkloadIdentity(
        Namespace.E4_LEGACY, "unparameterized", dimensions, displayLabel);
  }

  /** Returns the complete schema for one supported E5 category and operation. */
  public static Set<Dimension> requiredDimensions(Category category, String operation) {
    return schema(category, normalizedOperation(operation)).dimensions();
  }

  /** Returns every operation currently covered by the selected complete E5 schema. */
  public static Set<String> supportedOperations(Category category) {
    if (category == Category.CPU) {
      return CPU_SCHEMAS.keySet();
    }
    Map<String, IdentitySchema> schemas = RENDERER_SCHEMAS.get(category);
    if (schemas == null) {
      throw new IllegalArgumentException("Unsupported identity category: " + category);
    }
    return schemas.keySet();
  }

  public Namespace namespace() {
    return namespace;
  }

  public String workload() {
    return workload;
  }

  public SortedMap<Dimension, String> dimensions() {
    return dimensions;
  }

  /** A presentation-only label; it is intentionally excluded from equality and canonical IDs. */
  public String displayLabel() {
    return displayLabel;
  }

  public String semanticId() {
    return semanticId;
  }

  /** The semantic series key for identity schema v1. Result metrics remain attached evidence. */
  public String seriesId() {
    return semanticId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof WorkloadIdentity identity)) {
      return false;
    }
    return namespace == identity.namespace
        && workload.equals(identity.workload)
        && dimensions.equals(identity.dimensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespace, workload, dimensions);
  }

  @Override
  public String toString() {
    return semanticId;
  }

  private static Map<String, IdentitySchema> cpuSchemas() {
    Map<String, IdentitySchema> schemas = new LinkedHashMap<>();
    schemas.put(
        "measureLatin",
        currentCpuSchema("measureLatin", "measure-text-font", MEASURE_TEXT));
    schemas.put(
        "measureWrappedParagraph",
        currentCpuSchema(
            "measureWrappedParagraph", "measure-text-wrapped-font", WRAPPED_MEASURE_TEXT));
    schemas.put(
        "measureMixedCjk",
        currentCpuSchema("measureMixedCjk", "measure-text-font-list", MEASURE_TEXT));
    schemas.put(
        "measureSupplementaryUnicode",
        currentCpuSchema(
            "measureSupplementaryUnicode", "measure-text-font-list", MEASURE_TEXT));
    schemas.put(
        "measureMissingGlyphs",
        currentCpuSchema("measureMissingGlyphs", "measure-text-font-list", MEASURE_TEXT));
    schemas.put(
        "measureLongSingleFont",
        currentCpuSchema("measureLongSingleFont", "measure-text-font", LONG_MEASURE_TEXT));
    schemas.put(
        "findCaretNearBeginning",
        currentCpuSchema(
            "findCaretNearBeginning", "get-text-caret-metrics-font", CARET_BEGINNING));
    schemas.put(
        "findCaretNearEnd",
        currentCpuSchema("findCaretNearEnd", "get-text-caret-metrics-font", CARET_END));
    schemas.put(
        "layoutTextDenseInlineContent",
        currentCpuSchema(
            "layoutTextDenseInlineContent", "inline-formatting-context-layout", INLINE_LAYOUT));
    schemas.put(
        "measureParameterizedText",
        counterCpuSchema(
            "measureParameterizedText", "measure-text-wrapped-font-list",
            PARAMETERIZED_MEASURE_TEXT));
    return Collections.unmodifiableMap(schemas);
  }

  private static IdentitySchema currentCpuSchema(
      String operation, String api, Set<Dimension> operationDimensions) {
    return cpuSchema(
        operation,
        api,
        operationDimensions,
        "service-and-operation-fixtures-created-once-and-reused-through-trial",
        "current-corpus-in-trial-setup");
  }

  private static IdentitySchema cpuSchema(
      String operation,
      String api,
      Set<Dimension> operationDimensions,
      String fixturePreparationPolicy,
      String fontFixturePolicy) {
    EnumSet<Dimension> required = EnumSet.copyOf(TIMED_CPU_COMMON);
    required.addAll(operationDimensions);
    return new IdentitySchema(
        "cpu-text",
        Map.of(
            Dimension.API,
            api,
            Dimension.BENCHMARK_CLASS,
            CPU_BENCHMARK_CLASS,
            Dimension.CATEGORY,
            Category.CPU.canonicalValue(),
            Dimension.FIXTURE_PREPARATION_POLICY,
            fixturePreparationPolicy,
            Dimension.FONT_FIXTURE_POLICY,
            fontFixturePolicy,
            Dimension.HARNESS,
            "jmh",
            Dimension.OPERATION,
            operation,
            Dimension.SETUP_LEVEL,
            "trial"),
        immutableDimensions(required));
  }

  private static IdentitySchema counterCpuSchema(
      String operation, String api, Set<Dimension> operationDimensions) {
    EnumSet<Dimension> required = EnumSet.copyOf(COUNTER_CPU_COMMON);
    required.addAll(operationDimensions);
    return new IdentitySchema(
        "cpu-text",
        Map.of(
            Dimension.API,
            api,
            Dimension.CATEGORY,
            Category.CPU.canonicalValue(),
            Dimension.FIXTURE_PREPARATION_POLICY,
            "counter-scenario-created-before-recorded-operation",
            Dimension.FONT_FIXTURE_POLICY,
            "counter-scenario-prewarm-once-before-recorded-operation",
            Dimension.HARNESS,
            "direct",
            Dimension.OPERATION,
            operation,
            Dimension.SETUP_LEVEL,
            "application-run"),
        immutableDimensions(required));
  }

  private static Map<Category, Map<String, IdentitySchema>> rendererSchemas() {
    EnumMap<Category, Map<String, IdentitySchema>> schemas = new EnumMap<>(Category.class);
    Map<String, IdentitySchema> normalText = new LinkedHashMap<>();
    normalText.put(
        "render-text",
        rendererSchema(
            Category.NORMAL_TEXT,
            "renderer-text",
            "render-text",
            "normal-text",
            "none",
            CURRENT_RENDERER,
            "scenes-created-before-renderer-initialization",
            "mixed-cjk-once-before-each-scene-before-renderer-initialization"));
    normalText.put(
        "render-normal-text-scenario",
        rendererSchema(
            Category.NORMAL_TEXT,
            "renderer-text-scenario",
            "render-normal-text-scenario",
            "normal-text",
            "none",
            PLANNED_RENDERER,
            "parameterized-scene-created-before-warmup",
            "scenario-corpus-before-measurement"));
    schemas.put(Category.NORMAL_TEXT, Collections.unmodifiableMap(normalText));

    schemas.put(
        Category.INPUT,
        Map.of(
            "render-input-scenario",
            rendererSchema(
                Category.INPUT,
                "renderer-input-scenario",
                "render-input-scenario",
                "input-text",
                "input",
                union(PLANNED_RENDERER, CONTROL_RENDERER),
                "parameterized-scene-created-before-warmup",
                "scenario-corpus-before-measurement")));
    schemas.put(
        Category.TEXTAREA,
        Map.of(
            "render-textarea-scenario",
            rendererSchema(
                Category.TEXTAREA,
                "renderer-textarea-scenario",
                "render-textarea-scenario",
                "textarea-text",
                "textarea",
                union(PLANNED_RENDERER, CONTROL_RENDERER, TEXTAREA_RENDERER),
                "parameterized-scene-created-before-warmup",
                "scenario-corpus-before-measurement")));
    return Collections.unmodifiableMap(schemas);
  }

  private static IdentitySchema rendererSchema(
      Category category,
      String workload,
      String operation,
      String rendererPath,
      String controlType,
      Set<Dimension> operationDimensions,
      String fixturePreparationPolicy,
      String fontFixturePolicy) {
    EnumSet<Dimension> required = EnumSet.copyOf(RENDERER_COMMON);
    required.addAll(operationDimensions);
    return new IdentitySchema(
        workload,
        Map.of(
            Dimension.API,
            "render-frame",
            Dimension.CATEGORY,
            category.canonicalValue(),
            Dimension.CONTROL_TYPE,
            controlType,
            Dimension.FIXTURE_PREPARATION_POLICY,
            fixturePreparationPolicy,
            Dimension.FONT_FIXTURE_POLICY,
            fontFixturePolicy,
            Dimension.HARNESS,
            "nanovg",
            Dimension.OPERATION,
            operation,
            Dimension.RENDERER_PATH,
            rendererPath,
            Dimension.SETUP_LEVEL,
            "application-run"),
        immutableDimensions(required));
  }

  private static IdentitySchema schema(Category category, String operation) {
    if (category == Category.CPU) {
      IdentitySchema schema = CPU_SCHEMAS.get(operation);
      if (schema == null) {
        throw new IllegalArgumentException("Unsupported CPU identity operation: " + operation);
      }
      return schema;
    }
    Map<String, IdentitySchema> schemas = RENDERER_SCHEMAS.get(category);
    if (schemas == null) {
      throw new IllegalArgumentException("Unsupported identity category: " + category);
    }
    IdentitySchema schema = schemas.get(operation);
    if (schema == null) {
      throw new IllegalArgumentException(
          "Unsupported " + category.canonicalValue() + " identity operation: " + operation);
    }
    return schema;
  }

  private void validateDimensions() {
    if (namespace == Namespace.E4_LEGACY) {
      if (dimensions.size() != 1 || !dimensions.containsKey(Dimension.HISTORICAL_SERIES_KEY)) {
        throw new IllegalArgumentException(
            "E4 legacy identity requires only historical-series-key");
      }
      return;
    }

    String categoryValue = dimensions.get(Dimension.CATEGORY);
    String operation = dimensions.get(Dimension.OPERATION);
    if (categoryValue == null || operation == null) {
      throw new IllegalArgumentException("E5 identity requires category and operation");
    }
    Category category = Category.fromCanonicalValue(categoryValue);
    IdentitySchema schema = schema(category, operation);
    if (!schema.workload().equals(workload)) {
      throw new IllegalArgumentException(
          "Identity category "
              + categoryValue
              + " requires workload "
              + schema.workload()
              + ", not "
              + workload);
    }

    EnumSet<Dimension> missing = EnumSet.copyOf(schema.dimensions());
    missing.removeAll(dimensions.keySet());
    EnumSet<Dimension> unexpected = EnumSet.copyOf(dimensions.keySet());
    unexpected.removeAll(schema.dimensions());
    if (!missing.isEmpty() || !unexpected.isEmpty()) {
      throw new IllegalArgumentException(
          "Incomplete identity schema for "
              + categoryValue
              + "/"
              + operation
              + "; missing="
              + keys(missing)
              + "; unexpected="
              + keys(unexpected));
    }
    schema
        .fixedValues()
        .forEach(
            (dimension, expected) -> {
              String actual = dimensions.get(dimension);
              if (!expected.equals(actual)) {
                throw new IllegalArgumentException(
                    dimension.key() + " must be " + expected + " for " + categoryValue);
              }
            });

    if (schema.dimensions().contains(Dimension.SELECTION_START_UTF16)) {
      BigInteger sourceLength = new BigInteger(dimensions.get(Dimension.SOURCE_UTF16_LENGTH));
      BigInteger caretIndex = new BigInteger(dimensions.get(Dimension.CARET_INDEX_UTF16));
      BigInteger selectionStart =
          new BigInteger(dimensions.get(Dimension.SELECTION_START_UTF16));
      BigInteger selectionEnd = new BigInteger(dimensions.get(Dimension.SELECTION_END_UTF16));
      if (caretIndex.compareTo(sourceLength) > 0
          || selectionStart.compareTo(selectionEnd) > 0
          || selectionEnd.compareTo(sourceLength) > 0) {
        throw new IllegalArgumentException(
            "Control UTF-16 caret/selection indices must be ordered within source-utf16-length");
      }
    }

    if (schema.dimensions().contains(Dimension.OFFSCREEN_RATIO)) {
      BigDecimal ratio = new BigDecimal(dimensions.get(Dimension.OFFSCREEN_RATIO));
      BigDecimal extent = new BigDecimal(dimensions.get(Dimension.OFFSCREEN_EXTENT_PX));
      String visibility = dimensions.get(Dimension.VISIBILITY);
      if ("visible".equals(visibility)
          && (ratio.signum() != 0 || extent.signum() != 0)) {
        throw new IllegalArgumentException(
            "Visible scenarios require zero offscreen-ratio and offscreen-extent-px");
      }
      if ("offscreen".equals(visibility)
          && (ratio.signum() == 0 || extent.signum() == 0)) {
        throw new IllegalArgumentException(
            "Offscreen scenarios require positive offscreen-ratio and offscreen-extent-px");
      }
    }

    if (schema.dimensions().contains(Dimension.WRAP_WIDTH_POLICY)) {
      boolean fixed = "fixed".equals(dimensions.get(Dimension.WRAP_WIDTH_POLICY));
      if (fixed != schema.dimensions().contains(Dimension.WRAP_WIDTH_PX)) {
        throw new IllegalArgumentException(
            "wrap-width-policy must be fixed exactly when wrap-width-px is present");
      }
      String wrappingPolicy = dimensions.get(Dimension.WRAPPING_POLICY);
      if ((fixed && "unwrapped".equals(wrappingPolicy))
          || (!fixed && !"unwrapped".equals(wrappingPolicy))) {
        throw new IllegalArgumentException(
            "unwrapped is valid only for unbounded execution; fixed-width execution must wrap");
      }
    }

    if (schema.dimensions().contains(Dimension.MEASUREMENT_ORDER)) {
      BigInteger measurementOrderIndex =
          new BigInteger(dimensions.get(Dimension.MEASUREMENT_ORDER_INDEX));
      String measurementOrder = dimensions.get(Dimension.MEASUREMENT_ORDER);
      if (schema.dimensions().contains(Dimension.COMPANION_TEXT_NODE_COUNT)) {
        BigInteger pairCount = new BigInteger(dimensions.get(Dimension.SCENE_PAIR_COUNT));
        BigInteger textNodeCount = new BigInteger(dimensions.get(Dimension.TEXT_NODE_COUNT));
        BigInteger companionTextNodeCount =
            new BigInteger(dimensions.get(Dimension.COMPANION_TEXT_NODE_COUNT));
        if (!pairCount.equals(BigInteger.TWO)
            || measurementOrderIndex.compareTo(BigInteger.ONE) < 0
            || measurementOrderIndex.compareTo(pairCount) > 0
            || "isolated".equals(measurementOrder)) {
          throw new IllegalArgumentException(
              "Paired renderer scenarios require two scenes and a valid non-isolated order index");
        }
        int sizeComparison = textNodeCount.compareTo(companionTextNodeCount);
        boolean expectedSmallFirst =
            (measurementOrderIndex.equals(BigInteger.ONE) && sizeComparison < 0)
                || (measurementOrderIndex.equals(BigInteger.TWO) && sizeComparison > 0);
        if ("small-then-large".equals(measurementOrder) != expectedSmallFirst) {
          throw new IllegalArgumentException(
              "Renderer scene/companion counts must agree with measurement order and index");
        }
      } else if (!"isolated".equals(measurementOrder)
          || !measurementOrderIndex.equals(BigInteger.ONE)) {
        throw new IllegalArgumentException(
            "Unpaired renderer scenarios require isolated measurement order at index one");
      }
    }

    List<String> fontItems = List.of(dimensions.get(Dimension.FONT_CHAIN).split(",", -1));
    boolean anyStagedFonts = fontItems.stream().anyMatch(WorkloadIdentity::isStagedFontIdentity);
    boolean allStagedFonts = fontItems.stream().allMatch(WorkloadIdentity::isStagedFontIdentity);
    if (category == Category.CPU && anyStagedFonts) {
      throw new IllegalArgumentException("CPU font-chain requires direct exact Font identities");
    }
    if (category != Category.CPU
        && (!allStagedFonts
            || fontItems.stream().noneMatch(item -> item.startsWith("prewarm="))
            || fontItems.stream().noneMatch(item -> item.startsWith("layout=")))) {
      throw new IllegalArgumentException(
          "Renderer font-chain requires exact staged prewarm and layout Font identities");
    }
  }

  private String serialize() {
    StringBuilder value =
        new StringBuilder(ID_PREFIX)
            .append(':')
            .append(namespace.canonicalValue())
            .append(':')
            .append(percentEncode(workload));
    dimensions.forEach(
        (dimension, dimensionValue) ->
            value
                .append(';')
                .append(dimension.key())
                .append('=')
                .append(percentEncode(dimensionValue)));
    return value.toString();
  }

  private static Set<Dimension> dimensions(Dimension... dimensions) {
    EnumSet<Dimension> values = EnumSet.noneOf(Dimension.class);
    Collections.addAll(values, dimensions);
    return Collections.unmodifiableSet(values);
  }

  @SafeVarargs
  private static Set<Dimension> union(Set<Dimension>... dimensionSets) {
    EnumSet<Dimension> values = EnumSet.noneOf(Dimension.class);
    for (Set<Dimension> dimensionSet : dimensionSets) {
      values.addAll(dimensionSet);
    }
    return Collections.unmodifiableSet(values);
  }

  private static Set<Dimension> immutableDimensions(Set<Dimension> dimensions) {
    return Collections.unmodifiableSet(EnumSet.copyOf(dimensions));
  }

  private static SortedMap<Dimension, String> immutableDimensions(Map<Dimension, String> source) {
    TreeMap<Dimension, String> ordered = new TreeMap<>(DIMENSION_ORDER);
    ordered.putAll(source);
    return Collections.unmodifiableSortedMap(ordered);
  }

  private static List<String> keys(Set<Dimension> dimensions) {
    return dimensions.stream().sorted(DIMENSION_ORDER).map(Dimension::key).toList();
  }

  private static String canonicalName(String value, String field) {
    String normalized = normalizedText(value, field).trim();
    if (!CANONICAL_NAME.matcher(normalized).matches()) {
      throw new IllegalArgumentException(
          field + " must match " + CANONICAL_NAME.pattern() + ": " + normalized);
    }
    return normalized;
  }

  private static String normalizedText(String value, String field) {
    Objects.requireNonNull(value, field);
    validateSurrogates(value, field);
    String normalized = Normalizer.normalize(value, Normalizer.Form.NFC);
    if (normalized.isBlank()) {
      throw new IllegalArgumentException(field + " cannot be blank");
    }
    return normalized;
  }

  private static String normalizedOperation(String value) {
    String operation = normalizedText(value, "operation").trim();
    if (!OPERATION.matcher(operation).matches()) {
      throw new IllegalArgumentException("Invalid operation: " + value);
    }
    return operation;
  }

  private static void validateSurrogates(String value, String field) {
    for (int index = 0; index < value.length(); index++) {
      char current = value.charAt(index);
      if (Character.isHighSurrogate(current)) {
        if (index + 1 >= value.length() || !Character.isLowSurrogate(value.charAt(index + 1))) {
          throw new IllegalArgumentException(field + " contains an unpaired high surrogate");
        }
        index++;
      } else if (Character.isLowSurrogate(current)) {
        throw new IllegalArgumentException(field + " contains an unpaired low surrogate");
      }
    }
  }

  private static String canonicalEnumeration(
      Object value, String field, Set<String> allowedValues) {
    String source;
    if (value instanceof CanonicalValue canonical) {
      source = canonical.canonicalValue();
    } else if (value instanceof Enum<?> enumeration) {
      source = enumeration.name();
    } else if (value instanceof CharSequence characters) {
      source = characters.toString();
    } else {
      throw invalidType(field, value, "an enum or string");
    }
    String withCamelBoundaries = CAMEL_BOUNDARY.matcher(source.trim()).replaceAll("$1-$2");
    String canonical =
        withCamelBoundaries
            .toLowerCase(Locale.ROOT)
            .replace('_', '-')
            .replaceAll("\\s+", "-");
    canonical = normalizedText(canonical, field);
    if (!CANONICAL_NAME.matcher(canonical).matches() || !allowedValues.contains(canonical)) {
      throw new IllegalArgumentException(
          field + " must be one of " + allowedValues + ", not " + source);
    }
    return canonical;
  }

  private static String canonicalClassName(Object value, String field) {
    if (!(value instanceof CharSequence characters)) {
      throw invalidType(field, value, "a class-name string");
    }
    String canonical = normalizedText(characters.toString(), field).trim();
    if (!CLASS_NAME.matcher(canonical).matches()) {
      throw new IllegalArgumentException("Invalid class name for " + field + ": " + canonical);
    }
    return canonical;
  }

  private static String canonicalOperation(Object value, String field) {
    if (!(value instanceof CharSequence characters)) {
      throw invalidType(field, value, "an operation string");
    }
    return normalizedOperation(characters.toString());
  }

  private static String canonicalToken(Object value, String field) {
    if (!(value instanceof CharSequence characters)) {
      throw invalidType(field, value, "a canonical-name string");
    }
    return canonicalName(characters.toString(), field);
  }

  private static String canonicalOrderedList(Object value, String field) {
    List<String> items = new ArrayList<>();
    if (value instanceof CharSequence characters) {
      Collections.addAll(items, characters.toString().split(",", -1));
    } else if (value instanceof Iterable<?> iterable) {
      for (Object item : iterable) {
        items.add(Objects.toString(item, ""));
      }
    } else if (value != null && value.getClass().isArray()) {
      for (int index = 0; index < Array.getLength(value); index++) {
        items.add(Objects.toString(Array.get(value, index), ""));
      }
    } else {
      throw invalidType(field, value, "a comma-separated string, iterable, or array");
    }
    if (items.isEmpty()) {
      throw new IllegalArgumentException(field + " cannot be empty");
    }
    List<String> canonical = new ArrayList<>(items.size());
    for (String item : items) {
      String normalized = normalizedText(item, field + " item").trim();
      if (normalized.indexOf(',') >= 0) {
        throw new IllegalArgumentException(field + " item cannot contain a comma");
      }
      canonical.add(normalized);
    }
    return String.join(",", canonical);
  }

  private static String canonicalFontObjectChain(Object value, String field) {
    String canonical = canonicalOrderedList(value, field);
    for (String item : canonical.split(",", -1)) {
      String fontIdentity = isStagedFontIdentity(item) ? item.substring(item.indexOf('=') + 1) : item;
      String[] components = fontIdentity.split("\\|", -1);
      if (components.length != 5
          || java.util.Arrays.stream(components).anyMatch(String::isBlank)) {
        throw new IllegalArgumentException(
            field
                + " items must identify family|style|stretch|weight|path, optionally staged as prewarm= or layout=: "
                + item);
      }
    }
    return canonical;
  }

  private static boolean isStagedFontIdentity(String item) {
    return item.startsWith("prewarm=") || item.startsWith("layout=");
  }

  private static String canonicalBoolean(Object value, String field) {
    if (value instanceof Boolean bool) {
      return bool.toString();
    }
    if (value instanceof CharSequence characters) {
      String canonical = characters.toString().trim().toLowerCase(Locale.ROOT);
      if (canonical.equals("true") || canonical.equals("false")) {
        return canonical;
      }
    }
    throw invalidType(field, value, "true or false");
  }

  private static String canonicalInteger(
      Object value, String field, BigInteger minimum, boolean minimumInclusive) {
    BigDecimal decimal = decimal(value, field);
    BigInteger integer;
    try {
      integer = decimal.toBigIntegerExact();
    } catch (ArithmeticException exception) {
      throw new IllegalArgumentException(field + " must be an integer: " + value, exception);
    }
    int comparison = integer.compareTo(minimum);
    if (comparison < 0 || (!minimumInclusive && comparison == 0)) {
      String comparisonText = minimumInclusive ? "at least " : "greater than ";
      throw new IllegalArgumentException(field + " must be " + comparisonText + minimum);
    }
    return integer.toString();
  }

  private static String canonicalDecimal(
      Object value, String field, BigDecimal minimum, boolean minimumInclusive) {
    BigDecimal decimal = decimal(value, field);
    int comparison = decimal.compareTo(minimum);
    if (comparison < 0 || (!minimumInclusive && comparison == 0)) {
      String comparisonText = minimumInclusive ? "at least " : "greater than ";
      throw new IllegalArgumentException(field + " must be " + comparisonText + minimum);
    }
    return normalizedDecimal(decimal);
  }

  private static String canonicalFiniteDecimal(Object value, String field) {
    return normalizedDecimal(decimal(value, field));
  }

  private static String canonicalDecimalRange(
      Object value, String field, BigDecimal minimum, BigDecimal maximum) {
    BigDecimal decimal = decimal(value, field);
    if (decimal.compareTo(minimum) < 0 || decimal.compareTo(maximum) > 0) {
      throw new IllegalArgumentException(
          field + " must be between " + minimum + " and " + maximum + " inclusive");
    }
    return normalizedDecimal(decimal);
  }

  private static BigDecimal decimal(Object value, String field) {
    Objects.requireNonNull(value, field);
    try {
      if (value instanceof BigDecimal decimal) {
        return decimal;
      }
      if (value instanceof BigInteger integer) {
        return new BigDecimal(integer);
      }
      if (value instanceof Byte
          || value instanceof Short
          || value instanceof Integer
          || value instanceof Long) {
        return BigDecimal.valueOf(((Number) value).longValue());
      }
      if (value instanceof Float number) {
        if (!Float.isFinite(number)) {
          throw new IllegalArgumentException(field + " must be finite");
        }
        return new BigDecimal(Float.toString(number));
      }
      if (value instanceof Double number) {
        if (!Double.isFinite(number)) {
          throw new IllegalArgumentException(field + " must be finite");
        }
        return BigDecimal.valueOf(number);
      }
      if (value instanceof CharSequence characters) {
        return new BigDecimal(characters.toString().trim());
      }
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException(field + " must be numeric: " + value, exception);
    }
    throw invalidType(field, value, "a number or numeric string");
  }

  private static String normalizedDecimal(BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      return "0";
    }
    return value.stripTrailingZeros().toPlainString();
  }

  private static String canonicalDuration(Object value, String field) {
    Duration duration;
    if (value instanceof Duration supplied) {
      duration = supplied;
    } else if (value instanceof CharSequence characters) {
      String source = characters.toString().trim();
      Matcher matcher = DURATION.matcher(source.toLowerCase(Locale.ROOT));
      try {
        if (matcher.matches()) {
          long amount = Long.parseLong(matcher.group(1));
          duration =
              switch (matcher.group(2)) {
                case "ns" -> Duration.ofNanos(amount);
                case "us" -> Duration.ofNanos(Math.multiplyExact(amount, 1_000));
                case "ms" -> Duration.ofMillis(amount);
                case "s" -> Duration.ofSeconds(amount);
                case "m" -> Duration.ofMinutes(amount);
                default -> throw new IllegalStateException("Unexpected duration unit");
              };
        } else {
          duration = Duration.parse(source.toUpperCase(Locale.ROOT));
        }
      } catch (ArithmeticException | DateTimeParseException | NumberFormatException exception) {
        throw new IllegalArgumentException(
            field + " must be a positive duration: " + source, exception);
      }
    } else {
      throw invalidType(field, value, "a duration or duration string");
    }
    if (duration.isZero() || duration.isNegative()) {
      throw new IllegalArgumentException(field + " must be a positive duration");
    }
    return duration.toString();
  }

  private static IllegalArgumentException invalidType(
      String field, Object value, String expected) {
    String actual = value == null ? "null" : value.getClass().getName();
    return new IllegalArgumentException(field + " requires " + expected + "; received " + actual);
  }

  private static ValueRule enumeration(String... allowedValues) {
    Set<String> allowed = Set.of(allowedValues);
    return (value, field) -> canonicalEnumeration(value, field, allowed);
  }

  private static String percentEncode(String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    StringBuilder encoded = new StringBuilder(bytes.length);
    for (byte current : bytes) {
      int unsigned = Byte.toUnsignedInt(current);
      if ((unsigned >= 'a' && unsigned <= 'z')
          || (unsigned >= 'A' && unsigned <= 'Z')
          || (unsigned >= '0' && unsigned <= '9')
          || unsigned == '-'
          || unsigned == '.'
          || unsigned == '_'
          || unsigned == '~') {
        encoded.append((char) unsigned);
      } else {
        encoded
            .append('%')
            .append(HEX.charAt(unsigned >>> 4))
            .append(HEX.charAt(unsigned & 0x0F));
      }
    }
    return encoded.toString();
  }

  public enum Namespace implements CanonicalValue {
    E4_LEGACY("e4-legacy"),
    E5("e5");

    private final String canonicalValue;

    Namespace(String canonicalValue) {
      this.canonicalValue = canonicalValue;
    }

    @Override
    public String canonicalValue() {
      return canonicalValue;
    }
  }

  public enum Category implements CanonicalValue {
    CPU("cpu"),
    NORMAL_TEXT("normal-text"),
    INPUT("input"),
    TEXTAREA("textarea");

    private final String canonicalValue;

    Category(String canonicalValue) {
      this.canonicalValue = canonicalValue;
    }

    @Override
    public String canonicalValue() {
      return canonicalValue;
    }

    static Category fromCanonicalValue(String value) {
      for (Category category : values()) {
        if (category.canonicalValue.equals(value)) {
          return category;
        }
      }
      throw new IllegalArgumentException("Unknown identity category: " + value);
    }
  }

  public enum Visibility implements CanonicalValue {
    VISIBLE("visible"),
    OFFSCREEN("offscreen"),
    MIXED("mixed");

    private final String canonicalValue;

    Visibility(String canonicalValue) {
      this.canonicalValue = canonicalValue;
    }

    @Override
    public String canonicalValue() {
      return canonicalValue;
    }
  }

  public enum SubmissionState implements CanonicalValue {
    CHANGED("changed"),
    UNCHANGED("unchanged");

    private final String canonicalValue;

    SubmissionState(String canonicalValue) {
      this.canonicalValue = canonicalValue;
    }

    @Override
    public String canonicalValue() {
      return canonicalValue;
    }
  }

  /** Closed schema-v1 vocabulary. Observed result/count fields are deliberately absent. */
  public enum Dimension {
    API(
        "api",
        enumeration(
            "get-text-caret-metrics-font",
            "inline-formatting-context-layout",
            "measure-text-font",
            "measure-text-font-list",
            "measure-text-wrapped-font",
            "measure-text-wrapped-font-list",
            "render-frame")),
    BENCHMARK_CLASS("benchmark-class", WorkloadIdentity::canonicalClassName),
    BENCHMARK_MODE(
        "benchmark-mode",
        enumeration("average-time", "sample-time", "single-shot-time", "throughput", "all")),
    CARET_OFFSET_POLICY(
        "caret-offset-policy", enumeration("fixed", "measured-width-minus-inset")),
    CARET_OFFSET_INSET_X_PX(
        "caret-offset-inset-x-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, true)),
    CARET_OFFSET_X_PX("caret-offset-x-px", WorkloadIdentity::canonicalFiniteDecimal),
    CARET_INDEX_UTF16(
        "caret-index-utf16",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    CARET_STATE("caret-state", enumeration("visible", "hidden", "none")),
    CATEGORY("category", enumeration("cpu", "normal-text", "input", "textarea")),
    CLEAR_POLICY("clear-policy", enumeration("color-stencil-before-sample", "none")),
    CLIP_STATE("clip-state", enumeration("inside", "outside", "mixed", "none")),
    COLOR("color", enumeration("black", "white")),
    COMPANION_SCENE_SHAPE(
        "companion-scene-shape",
        enumeration("same-workload-content-style-layout-and-geometry")),
    COMPANION_TEXT_NODE_COUNT(
        "companion-text-node-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    CONTAINER_HEIGHT_PX(
        "container-height-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, true)),
    CONTAINER_POSITION_X_PX(
        "container-position-x-px", WorkloadIdentity::canonicalFiniteDecimal),
    CONTAINER_POSITION_Y_PX(
        "container-position-y-px", WorkloadIdentity::canonicalFiniteDecimal),
    CONTAINER_WIDTH_PX(
        "container-width-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, true)),
    CONTEXT_VISIBILITY("context-visibility", enumeration("hidden", "visible")),
    CONTENT_ALTERNATION("content-alternation", enumeration("latin-mixed-cjk", "none")),
    CONTENT_REPEAT_COUNT(
        "content-repeat-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    CONTENT_TRANSFORM("content-transform", enumeration("none", "remove-ascii-spaces")),
    CONTROL_HEIGHT_PX(
        "control-height-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    CONTROL_STATE("control-state", enumeration("focused", "unfocused", "none")),
    CONTROL_TYPE("control-type", enumeration("none", "input", "textarea")),
    CONTROL_WIDTH_PX(
        "control-width-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    DECLARED_SOURCE_LINE_COUNT(
        "declared-source-line-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    DECLARED_VISUAL_LINE_COUNT(
        "declared-visual-line-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    DEFERRED_SUFFIX_CODE_POINT_COUNT(
        "deferred-suffix-code-point-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    DISPLAY("display", enumeration("block")),
    FALLBACK_TRANSITION_COUNT(
        "fallback-transition-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    FONT_CHAIN("font-chain", WorkloadIdentity::canonicalFontObjectChain),
    FONT_FIXTURE_POLICY(
        "font-fixture-policy",
        enumeration(
            "counter-scenario-prewarm-once-before-recorded-operation",
            "current-corpus-in-trial-setup",
            "mixed-cjk-once-before-each-scene-before-renderer-initialization",
            "parameterized-corpus-in-trial-setup",
            "scenario-corpus-before-measurement")),
    FONT_RESOLVER("font-resolver", enumeration("default")),
    FONT_SIZE_PX(
        "font-size-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    FONT_STRETCH("font-stretch", enumeration("normal")),
    FONT_STYLE("font-style", enumeration("normal", "italic")),
    FONT_WEIGHT("font-weight", enumeration("normal", "regular", "light", "bold")),
    FORKS(
        "forks", (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    FRAME_HEIGHT_PX(
        "frame-height-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    FRAME_WIDTH_PX(
        "frame-width-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    HARNESS("harness", enumeration("direct", "jmh", "nanovg")),
    HISTORICAL_SERIES_KEY("historical-series-key", WorkloadIdentity::canonicalToken),
    FIXTURE_PREPARATION_POLICY(
        "fixture-preparation-policy",
        enumeration(
            "counter-scenario-created-before-recorded-operation",
            "parameterized-fixtures-created-in-trial-setup",
            "parameterized-scene-created-before-warmup",
            "scenes-created-before-renderer-initialization",
            "service-and-operation-fixtures-created-once-and-reused-through-trial")),
    INLINE_LAYOUT_START_Y_PX(
        "inline-layout-start-y-px", WorkloadIdentity::canonicalFiniteDecimal),
    LINE_HEIGHT(
        "line-height",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    LINE_START_KERNING_TRANSITION_COUNT(
        "line-start-kerning-transition-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    MEASURED_FRAMES(
        "measured-frames",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    MEASUREMENT_BATCH_SIZE(
        "measurement-batch-size",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    MEASUREMENT_ITERATIONS(
        "measurement-iterations",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    MEASUREMENT_TIME("measurement-time", WorkloadIdentity::canonicalDuration),
    MEASUREMENT_OFFSET_X_PX(
        "measurement-offset-x-px", WorkloadIdentity::canonicalFiniteDecimal),
    MEASUREMENT_ORDER(
        "measurement-order", enumeration("isolated", "small-then-large", "large-then-small")),
    MEASUREMENT_ORDER_INDEX(
        "measurement-order-index",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    NATIVE_ACCESS("native-access", enumeration("all-unnamed")),
    OPERATION("operation", WorkloadIdentity::canonicalOperation),
    OUTPUT_TIME_UNIT(
        "output-time-unit",
        enumeration(
            "nanoseconds", "microseconds", "milliseconds", "seconds", "minutes", "hours")),
    OVERFLOW_WRAP("overflow-wrap", enumeration("normal", "break-word")),
    OFFSCREEN_EXTENT_PX(
        "offscreen-extent-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, true)),
    OFFSCREEN_RATIO(
        "offscreen-ratio",
        (value, field) ->
            canonicalDecimalRange(value, field, BigDecimal.ZERO, BigDecimal.ONE)),
    PARAGRAPH_COUNT(
        "paragraph-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    POSITION("position", enumeration("static")),
    PREWARM_WORKLOAD_CONTENT("prewarm-workload-content", WorkloadIdentity::canonicalToken),
    PREMEASURE_SEQUENCE(
        "premeasure-sequence",
        enumeration(
            "alternating-small-large-plus-small-structural-validation", "per-scene", "none")),
    PROFILER("profiler", enumeration("gc", "none")),
    RENDERER_PATH("renderer-path", enumeration("normal-text", "input-text", "textarea-text")),
    ROUND_TO_PIXEL("round-to-pixel", WorkloadIdentity::canonicalBoolean),
    SCENE_HEIGHT_PX(
        "scene-height-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    SCENE_PAIR_COUNT(
        "scene-pair-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    SCENE_WIDTH_PX(
        "scene-width-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, false)),
    SCROLL_X_PX(
        "scroll-x-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, true)),
    SCROLL_Y_PX(
        "scroll-y-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, true)),
    SELECTION_END_UTF16(
        "selection-end-utf16",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    SELECTION_START_UTF16(
        "selection-start-utf16",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    SETUP_LEVEL("setup-level", enumeration("application-run", "trial")),
    SOURCE_CODE_POINT_COUNT(
        "source-code-point-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    SOURCE_UTF16_LENGTH(
        "source-utf16-length",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    STATE_SCOPE("state-scope", enumeration("benchmark", "thread", "group")),
    SUBMISSION_STATE("submission-state", enumeration("changed", "unchanged")),
    SWAP_INTERVAL(
        "swap-interval",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    SYNCHRONIZATION("synchronization", enumeration("gl-finish", "none")),
    TAB_SIZE(
        "tab-size", (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    TEXT_ALIGN("text-align", enumeration("left", "right", "center", "justify")),
    TEXT_NODE_COUNT(
        "text-node-count",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    THREADS(
        "threads", (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    VALIDATION_POLICY(
        "validation-policy",
        enumeration("small-scene-production-command-recording-before-measurement", "none")),
    VISIBILITY("visibility", enumeration("visible", "offscreen", "mixed")),
    WARMUP_BATCH_SIZE(
        "warmup-batch-size",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    WARMUP_FRAMES(
        "warmup-frames",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    WARMUP_FORKS(
        "warmup-forks",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    WARMUP_ITERATIONS(
        "warmup-iterations",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, true)),
    WARMUP_ORDER(
        "warmup-order", enumeration("alternating-small-large-starting-small", "per-scene")),
    WARMUP_TIME("warmup-time", WorkloadIdentity::canonicalDuration),
    WINDOW_RESIZABLE("window-resizable", WorkloadIdentity::canonicalBoolean),
    WHITE_SPACE("white-space", enumeration("normal", "nowrap", "pre", "pre-wrap")),
    WORD_BREAK("word-break", enumeration("normal", "break-all", "keep-all")),
    WORKLOAD_CONTENT("workload-content", WorkloadIdentity::canonicalToken),
    WORKLOAD_VERSION(
        "workload-version",
        (value, field) -> canonicalInteger(value, field, BigInteger.ZERO, false)),
    WRAP_WIDTH_PX(
        "wrap-width-px",
        (value, field) -> canonicalDecimal(value, field, BigDecimal.ZERO, true)),
    WRAP_WIDTH_POLICY("wrap-width-policy", enumeration("fixed", "unbounded")),
    WRAPPING_POLICY(
        "wrapping-policy",
        enumeration(
            "character-wrap", "unwrapped", "word-wrap", "normal", "single-line", "soft-wrap"));

    private final String key;
    private final ValueRule valueRule;

    Dimension(String key, ValueRule valueRule) {
      this.key = key;
      this.valueRule = valueRule;
    }

    public String key() {
      return key;
    }

    String canonicalValue(Object value) {
      return valueRule.canonicalize(value, key);
    }

    public static Dimension fromKey(String key) {
      for (Dimension dimension : values()) {
        if (dimension.key.equals(key)) {
          return dimension;
        }
      }
      throw new IllegalArgumentException("Unknown identity dimension: " + key);
    }
  }

  private record IdentitySchema(
      String workload, Map<Dimension, String> fixedValues, Set<Dimension> dimensions) {
    IdentitySchema {
      fixedValues = Map.copyOf(fixedValues);
      dimensions = immutableDimensions(dimensions);
    }
  }

  private interface CanonicalValue {
    String canonicalValue();
  }

  @FunctionalInterface
  private interface ValueRule {
    String canonicalize(Object value, String field);
  }

  public static final class Builder {
    private final String workload;
    private final EnumMap<Dimension, String> dimensions = new EnumMap<>(Dimension.class);

    private Builder(String workload) {
      this.workload = canonicalName(workload, "workload");
    }

    public Builder dimension(Dimension dimension, Object value) {
      Objects.requireNonNull(dimension, "dimension");
      String canonical = dimension.canonicalValue(value);
      if (dimensions.putIfAbsent(dimension, canonical) != null) {
        throw new IllegalArgumentException("Duplicate identity dimension: " + dimension.key());
      }
      return this;
    }

    public WorkloadIdentity build(String displayLabel) {
      return new WorkloadIdentity(Namespace.E5, workload, dimensions, displayLabel);
    }
  }
}
