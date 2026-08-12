package com.spinyowl.spinygui.benchmark.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ComparabilityMetadataTest {
  @Test
  void canonicalFingerprintsIgnoreMapOrderAndEquivalentUnicodeRepresentation() {
    Map<String, String> firstSettings = new LinkedHashMap<>(settings("3"));
    Map<String, String> secondSettings = new LinkedHashMap<>();
    firstSettings.entrySet().stream().toList().reversed()
        .forEach(entry -> secondSettings.put(entry.getKey(), entry.getValue()));

    ComparabilityMetadata first = metadata("behavior-1", hash('a'), "JVM e\u0301", firstSettings, "impl-1");
    ComparabilityMetadata second = metadata("behavior-1", hash('a'), "JVM \u00e9", secondSettings, "impl-1");

    assertEquals(first.fingerprints(), second.fingerprints());
    assertTrue(first.compare(second).comparable());
  }

  @Test
  void goldenCanonicalSerializationAndFingerprintsRemainStable() {
    ComparabilityMetadata metadata =
        metadata("behavior-1", hash('a'), "JVM", settings("3"), "impl-1");

    assertEquals(
        """
        spinygui-comparability:v2
        group=8:identity
        behavior-contract-version=10:behavior-1
        benchmark-version=11:benchmark-1
        evidence-mode=37:timed-allocation-diagnostics-disabled
        fingerprint-schema-version=1:2
        result-schema-version=15:result-schema-1
        semantic-id=10:semantic-1
        workload-version=10:workload-1
        """,
        metadata.canonicalSerialization(
            ComparabilityMetadata.FingerprintComponent.IDENTITY));
    assertEquals(
        new ComparabilityMetadata.Fingerprints(
            "sha256:1f00db2cc3fb784a31d069144edaa593a30131c8e557963ba1595aff4ecc8c3e",
            "sha256:67890bb5f5b031921e508a03912378413e6d0f2d8a08cbe28645b1c381270658",
            "sha256:14a970bcfac4a64c71bce4105d22746d24cf5b0a84f682854c0471d362334db8",
            "sha256:3157419d7c484547463e41754836d38116d4f25db892f7930a58c1ce0b9ae866",
            "sha256:06d539bf56476b1897d038826041c3521b6b93f93a9bf3a41299dbb75af7cda9"),
        metadata.fingerprints());
  }

  @Test
  void identifiesOneFieldMismatchInEveryRequiredEqualityFingerprint() {
    ComparabilityMetadata baseline = metadata("behavior-1", hash('a'), "JVM", settings("3"), "impl-1");

    assertOnlyFingerprintDiffers(
        baseline, metadata("behavior-2", hash('a'), "JVM", settings("3"), "impl-1"), "identity");
    assertOnlyFingerprintDiffers(
        baseline, metadata("behavior-1", hash('b'), "JVM", settings("3"), "impl-1"), "workload");
    assertOnlyFingerprintDiffers(
        baseline, metadata("behavior-1", hash('a'), "Other JVM", settings("3"), "impl-1"), "environment");
    assertOnlyFingerprintDiffers(
        baseline, metadata("behavior-1", hash('a'), "JVM", settings("4"), "impl-1"), "settings");
  }

  @Test
  void evidenceModeIsRequiredIdentityAndFingerprintMetadata() {
    ComparabilityMetadata timed =
        metadata("behavior-1", hash('a'), "JVM", settings("3"), "impl-1");
    JsonObject counterJson = timed.toJson();
    counterJson.addProperty("evidenceMode", "counter-only-diagnostics-enabled");
    JsonObject counterSettings = new JsonObject();
    counterSettings.addProperty("native-access", "all-unnamed");
    counterSettings.addProperty("prewarm-operation-count", "1");
    counterSettings.addProperty("recorded-operation-count", "1");
    counterSettings.addProperty("reset-policy", "immediately-before-recorded-operation");
    counterSettings.addProperty("setup-policy", "same-exact-scenario-operation-prewarmed-once");
    counterSettings.addProperty("snapshot-policy", "immediately-after-recorded-operation");
    counterSettings.addProperty("thread-count", "1");
    counterSettings.addProperty("timing", "none");
    counterJson.add("benchmarkSettings", counterSettings);
    ComparabilityMetadata counter = ComparabilityMetadata.fromJson(counterJson);

    assertEquals(
        EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, timed.evidenceMode());
    assertEquals(EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED, counter.evidenceMode());
    assertNotEquals(timed.fingerprints().identity(), counter.fingerprints().identity());
    assertEquals(timed.fingerprints().workload(), counter.fingerprints().workload());
    assertEquals(timed.fingerprints().environment(), counter.fingerprints().environment());
    assertNotEquals(timed.fingerprints().settings(), counter.fingerprints().settings());
    assertNotEquals(timed.fingerprints().required(), counter.fingerprints().required());
    assertTrue(timed.compare(counter).reason().startsWith("identity.evidence-mode differs"));
  }

  @Test
  void schemaAndBehaviorVersionsAreExplicitIdentityMismatches() {
    ComparabilityMetadata baseline = metadata("behavior-1", hash('a'), "JVM", settings("3"), "impl-1");
    ComparabilityMetadata behavior = metadata("behavior-2", hash('a'), "JVM", settings("3"), "impl-1");
    ComparabilityMetadata schema =
        new ComparabilityMetadata(
            "benchmark-1", "workload-1", "result-schema-2", "behavior-1",
            EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, "semantic-1",
            "CPU fixture", hash('a'), hash('c'), hash('d'), cpuEnvironment("JVM"), settings("3"),
            implementation("impl-1"));

    assertEquals("identity.behavior-contract-version differs", baseline.compare(behavior).reason());
    assertEquals("identity.result-schema-version differs", baseline.compare(schema).reason());
  }

  @Test
  void implementationRevisionAndDisplayLabelAreReportedButExcludedFromEquality() {
    ComparabilityMetadata first = metadata("behavior-1", hash('a'), "JVM", settings("3"), "impl-1");
    ComparabilityMetadata second =
        new ComparabilityMetadata(
            "benchmark-1", "workload-1", "result-schema-1", "behavior-1",
            EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, "semantic-1",
            "Renamed presentation label", hash('a'), hash('c'), hash('d'), cpuEnvironment("JVM"),
            settings("3"), new ComparabilityMetadata.Implementation("impl-2", "build-2", "commit-2"));

    assertEquals(first.fingerprints(), second.fingerprints());
    assertTrue(first.compare(second).comparable());
    assertNotEquals(first.displayLabel(), second.displayLabel());
    assertNotEquals(first.implementation(), second.implementation());
  }

  @Test
  void jsonEvolutionFailsClosedForUnknownEqualityFieldsAndAllowsExplicitExtensions() {
    JsonObject valid = JsonParser.parseString(json()).getAsJsonObject();
    valid.add("extensions", new JsonObject());
    valid.getAsJsonObject("environment").add("extensions", new JsonObject());
    valid.getAsJsonObject("implementation").add("extensions", new JsonObject());
    assertEquals("semantic-1", ComparabilityMetadata.fromJson(valid).semanticId());

    JsonObject unknown = JsonParser.parseString(json()).getAsJsonObject();
    unknown.addProperty("observedGlyphCount", 42);
    IllegalArgumentException unknownFailure =
        assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(unknown));
    assertTrue(unknownFailure.getMessage().contains("Unknown comparability field"));

    JsonObject missing = JsonParser.parseString(json()).getAsJsonObject();
    missing.remove("workloadShapeSha256");
    IllegalArgumentException missingFailure =
        assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(missing));
    assertTrue(missingFailure.getMessage().contains("Missing required comparability field"));

    JsonObject futureSchema = JsonParser.parseString(json()).getAsJsonObject();
    futureSchema.addProperty("fingerprintSchemaVersion", 3);
    IllegalArgumentException schemaFailure =
        assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(futureSchema));
    assertTrue(schemaFailure.getMessage().contains("Unsupported comparability fingerprint schema version"));
    JsonObject withoutExtensions = JsonParser.parseString(json()).getAsJsonObject();
    assertEquals(withoutExtensions, ComparabilityMetadata.fromJson(withoutExtensions).toJson());
  }

  @Test
  void renderingEnvironmentRequiresDriverIdentityAndCpuEnvironmentRejectsGlNoise() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.RENDERING, "Vendor", "25", "OS", "1", "x64", "CPU",
            "GL vendor", "Renderer", null, "4.6"));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.CPU, "Vendor", "25", "OS", "1", "x64", "CPU",
            "irrelevant", null, null, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.CPU, "Vendor", "25", "OS", "1", "x64", null,
            null, null, null, null));
  }

  @Test
  void jsonRejectsWrongPrimitiveTypesAndEveryMissingRequiredField() {
    for (String field : List.of(
        "benchmarkVersion", "workloadVersion", "resultSchemaVersion", "behaviorContractVersion", "evidenceMode",
        "semanticId", "displayLabel", "workloadContentSha256", "workloadShapeSha256",
        "fontInputsSha256")) {
      JsonObject wrong = JsonParser.parseString(json()).getAsJsonObject();
      wrong.addProperty(field, 1);
      assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(wrong), field);
    }
    JsonObject schemaString = JsonParser.parseString(json()).getAsJsonObject();
    schemaString.addProperty("fingerprintSchemaVersion", "2");
    assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(schemaString));

    JsonObject settingNumber = JsonParser.parseString(json()).getAsJsonObject();
    settingNumber.getAsJsonObject("benchmarkSettings").addProperty("threads", 1);
    assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(settingNumber));

    JsonObject environmentBoolean = JsonParser.parseString(json()).getAsJsonObject();
    environmentBoolean.getAsJsonObject("environment").addProperty("cpuModel", true);
    assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(environmentBoolean));

    JsonObject implementationNumber = JsonParser.parseString(json()).getAsJsonObject();
    implementationNumber.getAsJsonObject("implementation").addProperty("commitRevision", 1);
    assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(implementationNumber));

    JsonObject uppercaseDigest = JsonParser.parseString(json()).getAsJsonObject();
    uppercaseDigest.addProperty("workloadContentSha256", hash('a').toUpperCase());
    assertThrows(IllegalArgumentException.class, () -> ComparabilityMetadata.fromJson(uppercaseDigest));

    for (String field : List.of(
        "scope", "jvmVendor", "jvmVersion", "osName", "osVersion", "osArchitecture", "cpuModel")) {
      JsonObject missingCpu = JsonParser.parseString(json()).getAsJsonObject();
      missingCpu.getAsJsonObject("environment").remove(field);
      assertThrows(
          IllegalArgumentException.class,
          () -> ComparabilityMetadata.fromJson(missingCpu),
          "missing CPU environment field " + field);
    }

    for (String setting : settings("3").keySet()) {
      JsonObject missing = JsonParser.parseString(json()).getAsJsonObject();
      missing.getAsJsonObject("benchmarkSettings").remove(setting);
      assertThrows(
          IllegalArgumentException.class,
          () -> ComparabilityMetadata.fromJson(missing),
          "missing CPU setting " + setting);
    }
    Map<String, String> unstableExtra = new LinkedHashMap<>(settings("3"));
    unstableExtra.put("timestamp", "2026-07-26T00:00:00Z");
    assertThrows(
        IllegalArgumentException.class,
        () -> metadata("behavior-1", hash('a'), "JVM", unstableExtra, "impl-1"));
  }

  @Test
  void renderingSettingsUseAnExactCompleteScopeSpecificSchema() {
    ComparabilityMetadata.Environment environment =
        new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.RENDERING, "Vendor", "25", "OS", "1", "x64", "CPU",
            "GL vendor", "Renderer", "driver", "4.6");
    ComparabilityMetadata metadata =
        new ComparabilityMetadata(
            "benchmark-1", "workload-1", "result-schema-1", "behavior-1",
            EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, "semantic-1",
            "Rendering", hash('a'), hash('c'), hash('d'), environment, renderingSettings(),
            implementation("impl-1"));
    assertEquals(renderingSettings(), metadata.benchmarkSettings());
    JsonObject complete = metadata.toJson();
    for (String field : List.of(
        "scope", "jvmVendor", "jvmVersion", "osName", "osVersion", "osArchitecture", "cpuModel",
        "glVendor", "glRenderer", "glDriverVersion", "glVersion")) {
      JsonObject missing = complete.deepCopy();
      missing.getAsJsonObject("environment").remove(field);
      assertThrows(
          IllegalArgumentException.class,
          () -> ComparabilityMetadata.fromJson(missing),
          "missing rendering environment field " + field);
    }
    for (String setting : renderingSettings().keySet()) {
      Map<String, String> incomplete = new LinkedHashMap<>(renderingSettings());
      incomplete.remove(setting);
      assertThrows(
          IllegalArgumentException.class,
          () -> new ComparabilityMetadata(
              "benchmark-1", "workload-1", "result-schema-1", "behavior-1",
              EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, "semantic-1",
              "Rendering", hash('a'), hash('c'), hash('d'), environment, incomplete,
              implementation("impl-1")),
          "missing rendering setting " + setting);
      JsonObject missingJson = complete.deepCopy();
      missingJson.getAsJsonObject("benchmarkSettings").remove(setting);
      assertThrows(
          IllegalArgumentException.class,
          () -> ComparabilityMetadata.fromJson(missingJson),
          "missing rendering JSON setting " + setting);
    }
  }

  private static void assertOnlyFingerprintDiffers(
      ComparabilityMetadata baseline, ComparabilityMetadata changed, String group) {
    ComparabilityMetadata.Fingerprints first = baseline.fingerprints();
    ComparabilityMetadata.Fingerprints second = changed.fingerprints();
    assertEquals(group.equals("identity"), !first.identity().equals(second.identity()));
    assertEquals(group.equals("workload"), !first.workload().equals(second.workload()));
    assertEquals(group.equals("environment"), !first.environment().equals(second.environment()));
    assertEquals(group.equals("settings"), !first.settings().equals(second.settings()));
    assertNotEquals(first.required(), second.required());
    ComparabilityMetadata.Comparison comparison = baseline.compare(changed);
    assertFalse(comparison.comparable());
    assertTrue(comparison.reason().startsWith(group + "."));
  }

  private static ComparabilityMetadata metadata(
      String behaviorVersion,
      String contentHash,
      String jvmVendor,
      Map<String, String> settings,
      String implementationRevision) {
    return new ComparabilityMetadata(
        "benchmark-1", "workload-1", "result-schema-1", behaviorVersion,
        EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, "semantic-1",
        "CPU fixture", contentHash, hash('c'), hash('d'), cpuEnvironment(jvmVendor), settings,
        implementation(implementationRevision));
  }

  private static ComparabilityMetadata.Environment cpuEnvironment(String jvmVendor) {
    return new ComparabilityMetadata.Environment(
        ComparabilityMetadata.Scope.CPU, jvmVendor, "25.0.1", "OS", "1", "x64", "CPU model",
        null, null, null, null);
  }

  private static ComparabilityMetadata.Implementation implementation(String revision) {
    return new ComparabilityMetadata.Implementation(revision, "build-1", "commit-1");
  }

  private static Map<String, String> settings(String warmupIterations) {
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
        Map.entry("warmup-iterations", warmupIterations),
        Map.entry("warmup-time", "PT0.5S"));
  }

  private static Map<String, String> renderingSettings() {
    return Map.ofEntries(
        Map.entry("alternating-warmup-frames-pair", "60"),
        Map.entry("alternating-warmup-frames-scene", "30"),
        Map.entry("clear-policy", "color-stencil-before-sample"),
        Map.entry("context-visibility", "hidden"),
        Map.entry("measured-frames", "200"),
        Map.entry("measurement-order", "small-then-large"),
        Map.entry("measurement-order-index", "1"),
        Map.entry("native-access", "all-unnamed"),
        Map.entry("premeasure-exposures-scene", "31"),
        Map.entry("premeasure-sequence", "alternating-small-large-plus-small-validation"),
        Map.entry("swap-interval", "0"),
        Map.entry("synchronization", "gl-finish"),
        Map.entry(
            "validation-policy", "small-scene-production-command-recording-before-measurement"),
        Map.entry("validation-exposures-scene", "1"),
        Map.entry(
            "validation-synchronization",
            "render-and-gl-finish-then-production-command-recording"),
        Map.entry("warmup-order", "alternating-small-large-starting-small"),
        Map.entry("window-resizable", "false"));
  }

  private static String hash(char value) {
    return "sha256:" + String.valueOf(value).repeat(64);
  }

  private static String json() {
    return """
        {
          "fingerprintSchemaVersion": 2,
          "benchmarkVersion": "benchmark-1",
          "workloadVersion": "workload-1",
          "resultSchemaVersion": "result-schema-1",
          "behaviorContractVersion": "behavior-1",
          "evidenceMode": "timed-allocation-diagnostics-disabled",
          "semanticId": "semantic-1",
          "displayLabel": "CPU fixture",
          "workloadContentSha256": "%s",
          "workloadShapeSha256": "%s",
          "fontInputsSha256": "%s",
          "environment": {
            "scope": "cpu",
            "jvmVendor": "Vendor",
            "jvmVersion": "25.0.1",
            "osName": "OS",
            "osVersion": "1",
            "osArchitecture": "x64",
            "cpuModel": "CPU model"
          },
          "benchmarkSettings": {
            "benchmark-mode": "average-time",
            "forks": "2",
            "measurement-batch-size": "1",
            "measurement-iterations": "5",
            "measurement-time": "PT0.5S",
            "native-access": "all-unnamed",
            "output-time-unit": "microseconds",
            "profiler": "gc",
            "state-scope": "benchmark",
            "threads": "1",
            "warmup-batch-size": "1",
            "warmup-forks": "0",
            "warmup-iterations": "3",
            "warmup-time": "PT0.5S"
          },
          "implementation": {
            "implementationRevision": "impl-1",
            "buildRevision": "build-1",
            "commitRevision": "commit-1"
          }
        }
        """.formatted(hash('a'), hash('c'), hash('d'));
  }
}
