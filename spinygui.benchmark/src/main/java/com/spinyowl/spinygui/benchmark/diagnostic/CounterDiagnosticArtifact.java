package com.spinyowl.spinygui.benchmark.diagnostic;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Category;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Dimension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Identified, untimed structural evidence emitted by the counter-only runner. */
public record CounterDiagnosticArtifact(
    int schemaVersion,
    JsonObject benchmarkRun,
    String coreVocabularyVersion,
    String nanoVgVocabularyVersion,
    List<Entry> entries) {
  public static final int SCHEMA_VERSION = 2;

  private static final Set<String> TEXT_EVIDENCE =
      Set.of(
          "observed-fallback-transition-count",
          "observed-paragraph-count",
          "observed-resolved-glyph-count",
          "observed-resolved-run-count",
          "observed-source-code-point-count",
          "observed-source-line-count",
          "observed-source-text-sha256",
          "observed-source-utf16-length",
          "observed-visual-line-count");
  private static final Set<String> RENDERER_GEOMETRY_EVIDENCE =
      Set.of(
          "observed-container-height-px",
          "observed-container-position-x-px",
          "observed-container-position-y-px",
          "observed-container-width-px",
          "observed-effective-content-position-x-px",
          "observed-effective-content-position-y-px",
          "observed-frame-height-px",
          "observed-frame-width-px",
          "observed-offscreen-extent-px",
          "observed-offscreen-ratio",
          "observed-predecessor-render-execution-count");
  private static final Set<String> CONTROL_EVIDENCE =
      Set.of(
          "observed-caret-index-utf16",
          "observed-control-focused",
          "observed-control-height-px",
          "observed-control-width-px",
          "observed-scroll-x-px",
          "observed-selection-end-utf16",
          "observed-selection-start-utf16");
  private static final Map<Category, Set<String>> OBSERVED_SCHEMAS =
      Map.of(
          Category.CPU,
          union(
              TEXT_EVIDENCE,
              Set.of(
                  "observed-deferred-suffix-code-point-count",
                  "observed-line-start-kerning-transition-count",
                  "observed-processed-source-code-point-count")),
          Category.NORMAL_TEXT,
          union(
              TEXT_EVIDENCE,
              RENDERER_GEOMETRY_EVIDENCE,
              Set.of("observed-text-fragment-count")),
          Category.INPUT,
          union(TEXT_EVIDENCE, RENDERER_GEOMETRY_EVIDENCE, CONTROL_EVIDENCE),
          Category.TEXTAREA,
          union(
              TEXT_EVIDENCE,
              RENDERER_GEOMETRY_EVIDENCE,
              CONTROL_EVIDENCE,
              Set.of(
                  "observed-deferred-suffix-code-point-count",
                  "observed-line-start-kerning-transition-count",
                  "observed-scroll-y-px",
                  "observed-wrap-width-px")));
  private static final Map<Category, String> COUNTER_OPERATIONS =
      Map.of(
          Category.CPU, "measureParameterizedText",
          Category.NORMAL_TEXT, "render-normal-text-scenario",
          Category.INPUT, "render-input-scenario",
          Category.TEXTAREA, "render-textarea-scenario");

  public CounterDiagnosticArtifact {
    if (schemaVersion != SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported counter artifact schema: " + schemaVersion);
    }
    BenchmarkRunMetadata run = BenchmarkRunMetadata.fromJson(benchmarkRun);
    if (run.artifact() != BenchmarkRunMetadata.Artifact.COUNTER_DIAGNOSTICS
        || run.evidenceMode() != EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED
        || run.baselineEligible()) {
      throw new IllegalArgumentException(
          "Counter artifact metadata is not counter-only investigation evidence");
    }
    if (coreVocabularyVersion == null || coreVocabularyVersion.isBlank()
        || nanoVgVocabularyVersion == null || nanoVgVocabularyVersion.isBlank()) {
      throw new IllegalArgumentException("Counter artifact vocabulary versions are required");
    }
    benchmarkRun = benchmarkRun.deepCopy();
    entries = List.copyOf(entries);
    if (entries.isEmpty()) {
      throw new IllegalArgumentException("Counter artifact requires at least one entry");
    }
    Set<String> series = new HashSet<>();
    for (Entry entry : entries) {
      if (!series.add(entry.seriesId())) {
        throw new IllegalArgumentException("Duplicate counter artifact series: " + entry.seriesId());
      }
    }
  }

  @Override
  public JsonObject benchmarkRun() {
    return benchmarkRun.deepCopy();
  }

  public record Entry(
      String scenarioName,
      String evidenceScope,
      String semanticId,
      String seriesId,
      Map<String, String> declaredInputs,
      JsonObject comparability,
      Map<String, Long> counters,
      Set<String> saturatedCounterIds,
      Map<String, JsonPrimitive> observedOutputs) {

    public Entry {
      requireText(scenarioName, "scenarioName");
      if (!Set.of("single-recorded-operation", "aggregate-rendered-items")
          .contains(evidenceScope)) {
        throw new IllegalArgumentException("Unknown counter evidence scope: " + evidenceScope);
      }
      requireText(semanticId, "semanticId");
      requireText(seriesId, "seriesId");
      declaredInputs =
          Collections.unmodifiableSortedMap(
              new TreeMap<>(Objects.requireNonNull(declaredInputs, "declaredInputs")));
      ComparabilityMetadata metadata = ComparabilityMetadata.fromJson(comparability);
      if (metadata.evidenceMode() != EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED
          || !metadata.semanticId().equals(semanticId)
          || !seriesId.equals(semanticId)) {
        throw new IllegalArgumentException(
            "Counter entry identity, series, and comparability metadata must agree");
      }
      validateDeclaredIdentity(scenarioName, semanticId, declaredInputs);
      comparability = comparability.deepCopy();
      counters =
          Collections.unmodifiableSortedMap(
              new TreeMap<>(Objects.requireNonNull(counters, "counters")));
      saturatedCounterIds =
          Collections.unmodifiableSortedSet(new TreeSet<>(saturatedCounterIds));
      observedOutputs =
          Collections.unmodifiableSortedMap(
              new TreeMap<>(Objects.requireNonNull(observedOutputs, "observedOutputs")));
      if (!counters.keySet().containsAll(saturatedCounterIds)) {
        throw new IllegalArgumentException("Saturated IDs must belong to the counter snapshot");
      }
      if (counters.values().stream().anyMatch(value -> value == null || value < 0)) {
        throw new IllegalArgumentException("Counter evidence values must be non-negative");
      }
      validateDeclaredObservedAgreement(
          scenarioName, evidenceScope, declaredInputs, observedOutputs);
    }

    @Override
    public JsonObject comparability() {
      return comparability.deepCopy();
    }

    private static void requireText(String value, String field) {
      if (value == null || value.isBlank()) {
        throw new IllegalArgumentException(field + " is required");
      }
    }

    static void validateDeclaredObservedAgreement(
        String scenarioName,
        String evidenceScope,
        Map<String, String> declared,
        Map<String, JsonPrimitive> observed) {
      Category category = category(declared.get("category"));
      String operation = declared.get("operation");
      if (!COUNTER_OPERATIONS.get(category).equals(operation)) {
        throw new IllegalArgumentException(
            "Operation is inapplicable to counter artifact category " + category + ": " + operation);
      }
      Set<String> expectedDeclared = new TreeSet<>();
      for (Dimension dimension : WorkloadIdentity.requiredDimensions(category, operation)) {
        expectedDeclared.add(dimension.key());
      }
      if (!declared.keySet().equals(expectedDeclared)) {
        throw new IllegalArgumentException(
            "Declared artifact schema mismatch for "
                + scenarioName
                + "; missing="
                + difference(expectedDeclared, declared.keySet())
                + "; unexpected="
                + difference(declared.keySet(), expectedDeclared));
      }
      if ((category == Category.CPU) != "single-recorded-operation".equals(evidenceScope)) {
        throw new IllegalArgumentException(
            "Evidence scope is inapplicable to " + category + ": " + evidenceScope);
      }
      Set<String> expectedObserved = OBSERVED_SCHEMAS.get(category);
      if (!observed.keySet().equals(expectedObserved)) {
        throw new IllegalArgumentException(
            "Observed artifact schema mismatch for "
                + scenarioName
                + "; missing="
                + difference(expectedObserved, observed.keySet())
                + "; unexpected="
                + difference(observed.keySet(), expectedObserved));
      }
      validateObservedTypes(scenarioName, observed);

      Map<String, String> exactMappings =
          Map.ofEntries(
              Map.entry("source-code-point-count", "observed-source-code-point-count"),
              Map.entry("source-utf16-length", "observed-source-utf16-length"),
              Map.entry("declared-source-line-count", "observed-source-line-count"),
              Map.entry("declared-visual-line-count", "observed-visual-line-count"),
              Map.entry("paragraph-count", "observed-paragraph-count"),
              Map.entry("fallback-transition-count", "observed-fallback-transition-count"),
              Map.entry(
                  "deferred-suffix-code-point-count",
                  "observed-deferred-suffix-code-point-count"),
              Map.entry(
                  "line-start-kerning-transition-count",
                  "observed-line-start-kerning-transition-count"),
              Map.entry("container-height-px", "observed-container-height-px"),
              Map.entry("container-position-x-px", "observed-container-position-x-px"),
              Map.entry("container-position-y-px", "observed-container-position-y-px"),
              Map.entry("container-width-px", "observed-container-width-px"),
              Map.entry("frame-height-px", "observed-frame-height-px"),
              Map.entry("frame-width-px", "observed-frame-width-px"),
              Map.entry("control-height-px", "observed-control-height-px"),
              Map.entry("control-width-px", "observed-control-width-px"),
              Map.entry("offscreen-extent-px", "observed-offscreen-extent-px"),
              Map.entry("caret-index-utf16", "observed-caret-index-utf16"),
              Map.entry("scroll-x-px", "observed-scroll-x-px"),
              Map.entry("scroll-y-px", "observed-scroll-y-px"),
              Map.entry("selection-end-utf16", "observed-selection-end-utf16"),
              Map.entry("selection-start-utf16", "observed-selection-start-utf16"),
              Map.entry("wrap-width-px", "observed-wrap-width-px"));
      exactMappings.forEach(
          (declaredKey, observedKey) -> {
            if (declared.containsKey(declaredKey) && observed.containsKey(observedKey)) {
              JsonPrimitive observedValue = observed.get(observedKey);
              if (observedValue == null
                  || new BigDecimal(declared.get(declaredKey))
                          .compareTo(observedValue.getAsBigDecimal())
                      != 0) {
                throw new IllegalArgumentException(
                    "Declared/observed disagreement for " + scenarioName + ": " + declaredKey);
              }
            }
          });
      if (declared.containsKey("offscreen-ratio")) {
        BigDecimal expected = new BigDecimal(declared.get("offscreen-ratio"));
        if (expected.compareTo(observed.get("observed-offscreen-ratio").getAsBigDecimal()) != 0) {
          throw new IllegalArgumentException(
              "Declared/observed disagreement for " + scenarioName + ": offscreen-ratio");
        }
      }
      if (declared.containsKey("submission-state")) {
        long expected = "unchanged".equals(declared.get("submission-state")) ? 1 : 0;
        if (observed.get("observed-predecessor-render-execution-count").getAsLong() != expected) {
          throw new IllegalArgumentException(
              "Declared/observed disagreement for " + scenarioName + ": submission-state");
        }
      }
      if (declared.containsKey("control-state")) {
        boolean expected = "focused".equals(declared.get("control-state"));
        if (observed.get("observed-control-focused").getAsBoolean() != expected) {
          throw new IllegalArgumentException(
              "Declared/observed disagreement for " + scenarioName + ": control-state");
        }
      }
      compareEffectivePosition(scenarioName, declared, observed, "x");
      compareEffectivePosition(scenarioName, declared, observed, "y");
    }

    private static void validateDeclaredIdentity(
        String scenarioName, String semanticId, Map<String, String> declared) {
      String prefix = "spinygui-benchmark:v1:e5:";
      int dimensionsStart = semanticId.indexOf(';', prefix.length());
      if (!semanticId.startsWith(prefix) || dimensionsStart < 0) {
        throw new IllegalArgumentException(
            "Counter entry does not use an E5 schema-v1 identity: " + scenarioName);
      }
      String workload = semanticId.substring(prefix.length(), dimensionsStart);
      WorkloadIdentity.Builder builder = WorkloadIdentity.e5(workload);
      declared.forEach((key, value) -> builder.dimension(Dimension.fromKey(key), value));
      String rebuilt = builder.build(scenarioName).semanticId();
      if (!rebuilt.equals(semanticId)) {
        throw new IllegalArgumentException(
            "Declared artifact inputs do not reproduce semantic identity for " + scenarioName);
      }
    }

    private static void compareEffectivePosition(
        String scenarioName,
        Map<String, String> declared,
        Map<String, JsonPrimitive> observed,
        String axis) {
      String declaredKey = "container-position-" + axis + "-px";
      String observedKey = "observed-effective-content-position-" + axis + "-px";
      if (!declared.containsKey(declaredKey)) return;
      if (new BigDecimal(declared.get(declaredKey))
              .compareTo(observed.get(observedKey).getAsBigDecimal())
          != 0) {
        throw new IllegalArgumentException(
            "Prepared control/content placement disagrees with " + scenarioName + ": " + axis);
      }
    }

    private static void validateObservedTypes(
        String scenarioName, Map<String, JsonPrimitive> observed) {
      for (Map.Entry<String, JsonPrimitive> entry : observed.entrySet()) {
        JsonPrimitive value = entry.getValue();
        if (value == null) {
          throw new IllegalArgumentException("Null observed evidence for " + scenarioName);
        }
        if ("observed-source-text-sha256".equals(entry.getKey())) {
          if (!value.isString() || !value.getAsString().matches("sha256:[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Invalid observed source hash for " + scenarioName);
          }
        } else if ("observed-control-focused".equals(entry.getKey())) {
          if (!value.isBoolean()) {
            throw new IllegalArgumentException(
                "Observed control focus evidence has the wrong type for " + scenarioName);
          }
        } else {
          if (!value.isNumber()) {
            throw new IllegalArgumentException(
                "Observed numeric evidence has the wrong primitive type: " + entry.getKey());
          }
          try {
            BigDecimal number = value.getAsBigDecimal();
            if ((entry.getKey().endsWith("-count")
                    || entry.getKey().endsWith("-length")
                    || entry.getKey().endsWith("-utf16"))
                && number.toBigIntegerExact().signum() < 0) {
              throw new IllegalArgumentException(
                  "Observed count/index evidence cannot be negative: " + entry.getKey());
            }
            if ("observed-offscreen-ratio".equals(entry.getKey())
                && (number.signum() < 0 || number.compareTo(BigDecimal.ONE) > 0)) {
              throw new IllegalArgumentException("Observed offscreen ratio is outside [0, 1]");
            }
            if (!entry.getKey().contains("position-") && number.signum() < 0) {
              throw new IllegalArgumentException(
                  "Observed evidence cannot be negative: " + entry.getKey());
            }
          } catch (ArithmeticException | NumberFormatException exception) {
            throw new IllegalArgumentException(
                "Observed numeric evidence has the wrong type: " + entry.getKey(), exception);
          }
        }
      }
    }

    private static Category category(String value) {
      return switch (Objects.requireNonNull(value, "category")) {
        case "cpu" -> Category.CPU;
        case "normal-text" -> Category.NORMAL_TEXT;
        case "input" -> Category.INPUT;
        case "textarea" -> Category.TEXTAREA;
        default -> throw new IllegalArgumentException("Unknown artifact category: " + value);
      };
    }

    private static Set<String> difference(Set<String> left, Set<String> right) {
      TreeSet<String> difference = new TreeSet<>(left);
      difference.removeAll(right);
      return difference;
    }
  }

  @SafeVarargs
  private static Set<String> union(Set<String>... sets) {
    Set<String> values = new LinkedHashSet<>();
    for (Set<String> set : sets) values.addAll(set);
    return Collections.unmodifiableSet(values);
  }
}
