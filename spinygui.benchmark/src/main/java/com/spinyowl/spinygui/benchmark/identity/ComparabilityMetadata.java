package com.spinyowl.spinygui.benchmark.identity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** Equality metadata used to decide whether two benchmark results may present a signed delta. */
public final class ComparabilityMetadata {
  public static final int FINGERPRINT_SCHEMA_VERSION = 2;

  private static final Pattern CANONICAL_KEY = Pattern.compile("[a-z][a-z0-9-]*");
  private static final Pattern SHA_256 = Pattern.compile("sha256:[0-9a-f]{64}");
  private static final Set<String> ROOT_FIELDS =
      Set.of(
          "fingerprintSchemaVersion",
          "benchmarkVersion",
          "workloadVersion",
          "resultSchemaVersion",
          "behaviorContractVersion",
          "evidenceMode",
          "semanticId",
          "displayLabel",
          "workloadContentSha256",
          "workloadShapeSha256",
          "fontInputsSha256",
          "environment",
          "benchmarkSettings",
          "implementation",
          "extensions");
  private static final Set<String> ENVIRONMENT_FIELDS =
      Set.of(
          "scope",
          "jvmVendor",
          "jvmVersion",
          "osName",
          "osVersion",
          "osArchitecture",
          "cpuModel",
          "glVendor",
          "glRenderer",
          "glDriverVersion",
          "glVersion",
          "extensions");
  private static final Set<String> IMPLEMENTATION_FIELDS =
      Set.of("implementationRevision", "buildRevision", "commitRevision", "extensions");
  private static final Set<String> CPU_SETTINGS =
      Set.of(
          "benchmark-mode",
          "forks",
          "measurement-batch-size",
          "measurement-iterations",
          "measurement-time",
          "native-access",
          "output-time-unit",
          "profiler",
          "state-scope",
          "threads",
          "warmup-batch-size",
          "warmup-forks",
          "warmup-iterations",
          "warmup-time");
  private static final Set<String> RENDERING_SETTINGS =
      Set.of(
          "alternating-warmup-frames-pair",
          "alternating-warmup-frames-scene",
          "clear-policy",
          "context-visibility",
          "measured-frames",
          "measurement-order",
          "measurement-order-index",
          "native-access",
          "premeasure-exposures-scene",
          "premeasure-sequence",
          "swap-interval",
          "synchronization",
          "validation-policy",
          "validation-exposures-scene",
          "validation-synchronization",
          "warmup-order",
          "window-resizable");
  private static final Set<String> CPU_COUNTER_SETTINGS =
      Set.of(
          "native-access",
          "prewarm-operation-count",
          "recorded-operation-count",
          "reset-policy",
          "setup-policy",
          "snapshot-policy",
          "thread-count",
          "timing");
  private static final Set<String> RENDERING_COUNTER_SETTINGS =
      Set.of(
          "clear-policy",
          "context-visibility",
          "native-access",
          "predecessor-frame-count",
          "prewarm-operation-count",
          "recorded-frame-count",
          "renderer-context-policy",
          "reset-policy",
          "setup-policy",
          "snapshot-policy",
          "swap-interval",
          "thread-count",
          "timing",
          "window-resizable");

  private final String benchmarkVersion;
  private final String workloadVersion;
  private final String resultSchemaVersion;
  private final String behaviorContractVersion;
  private final EvidenceMode evidenceMode;
  private final String semanticId;
  private final String displayLabel;
  private final String workloadContentSha256;
  private final String workloadShapeSha256;
  private final String fontInputsSha256;
  private final Environment environment;
  private final SortedMap<String, String> benchmarkSettings;
  private final Implementation implementation;
  private final Fingerprints fingerprints;

  public ComparabilityMetadata(
      String benchmarkVersion,
      String workloadVersion,
      String resultSchemaVersion,
      String behaviorContractVersion,
      EvidenceMode evidenceMode,
      String semanticId,
      String displayLabel,
      String workloadContentSha256,
      String workloadShapeSha256,
      String fontInputsSha256,
      Environment environment,
      Map<String, String> benchmarkSettings,
      Implementation implementation) {
    this.benchmarkVersion = text(benchmarkVersion, "benchmarkVersion");
    this.workloadVersion = text(workloadVersion, "workloadVersion");
    this.resultSchemaVersion = text(resultSchemaVersion, "resultSchemaVersion");
    this.behaviorContractVersion = text(behaviorContractVersion, "behaviorContractVersion");
    this.evidenceMode = Objects.requireNonNull(evidenceMode, "evidenceMode");
    this.semanticId = text(semanticId, "semanticId");
    this.displayLabel = text(displayLabel, "displayLabel");
    this.workloadContentSha256 = digest(workloadContentSha256, "workloadContentSha256");
    this.workloadShapeSha256 = digest(workloadShapeSha256, "workloadShapeSha256");
    this.fontInputsSha256 = digest(fontInputsSha256, "fontInputsSha256");
    this.environment = Objects.requireNonNull(environment, "environment");
    this.benchmarkSettings = settings(benchmarkSettings, environment.scope(), evidenceMode);
    this.implementation = Objects.requireNonNull(implementation, "implementation");
    fingerprints = createFingerprints();
  }

  /** Parses schema-v2 metadata and fails closed for missing or unknown equality-bearing fields. */
  public static ComparabilityMetadata fromJson(JsonObject object) {
    Objects.requireNonNull(object, "object");
    rejectUnknown(object, ROOT_FIELDS, "comparability");
    int schemaVersion = integer(object, "fingerprintSchemaVersion");
    if (schemaVersion != FINGERPRINT_SCHEMA_VERSION) {
      throw new IllegalArgumentException(
          "Unsupported comparability fingerprint schema version: " + schemaVersion);
    }

    JsonObject environmentObject = object(object, "environment");
    rejectUnknown(environmentObject, ENVIRONMENT_FIELDS, "comparability.environment");
    Scope scope = Scope.fromJson(string(environmentObject, "scope"));
    Environment environment =
        new Environment(
            scope,
            string(environmentObject, "jvmVendor"),
            string(environmentObject, "jvmVersion"),
            string(environmentObject, "osName"),
            string(environmentObject, "osVersion"),
            string(environmentObject, "osArchitecture"),
            string(environmentObject, "cpuModel"),
            optionalString(environmentObject, "glVendor"),
            optionalString(environmentObject, "glRenderer"),
            optionalString(environmentObject, "glDriverVersion"),
            optionalString(environmentObject, "glVersion"));

    JsonObject settingsObject = object(object, "benchmarkSettings");
    Map<String, String> benchmarkSettings = new LinkedHashMap<>();
    for (Map.Entry<String, JsonElement> entry : settingsObject.entrySet()) {
      benchmarkSettings.put(
          entry.getKey(), string(entry.getValue(), "benchmarkSettings." + entry.getKey()));
    }

    JsonObject implementationObject = object(object, "implementation");
    rejectUnknown(implementationObject, IMPLEMENTATION_FIELDS, "comparability.implementation");
    Implementation implementation =
        new Implementation(
            string(implementationObject, "implementationRevision"),
            string(implementationObject, "buildRevision"),
            string(implementationObject, "commitRevision"));

    return new ComparabilityMetadata(
        string(object, "benchmarkVersion"),
        string(object, "workloadVersion"),
        string(object, "resultSchemaVersion"),
        string(object, "behaviorContractVersion"),
        EvidenceMode.fromJson(string(object, "evidenceMode")),
        string(object, "semanticId"),
        string(object, "displayLabel"),
        string(object, "workloadContentSha256"),
        string(object, "workloadShapeSha256"),
        string(object, "fontInputsSha256"),
        environment,
        benchmarkSettings,
        implementation);
  }

  /** Serializes the complete schema-v2 metadata object emitted by benchmark producers. */
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("fingerprintSchemaVersion", FINGERPRINT_SCHEMA_VERSION);
    object.addProperty("benchmarkVersion", benchmarkVersion);
    object.addProperty("workloadVersion", workloadVersion);
    object.addProperty("resultSchemaVersion", resultSchemaVersion);
    object.addProperty("behaviorContractVersion", behaviorContractVersion);
    object.addProperty("evidenceMode", evidenceMode.json);
    object.addProperty("semanticId", semanticId);
    object.addProperty("displayLabel", displayLabel);
    object.addProperty("workloadContentSha256", workloadContentSha256);
    object.addProperty("workloadShapeSha256", workloadShapeSha256);
    object.addProperty("fontInputsSha256", fontInputsSha256);

    JsonObject environmentObject = new JsonObject();
    environmentObject.addProperty("scope", environment.scope().json);
    environmentObject.addProperty("jvmVendor", environment.jvmVendor());
    environmentObject.addProperty("jvmVersion", environment.jvmVersion());
    environmentObject.addProperty("osName", environment.osName());
    environmentObject.addProperty("osVersion", environment.osVersion());
    environmentObject.addProperty("osArchitecture", environment.osArchitecture());
    environmentObject.addProperty("cpuModel", environment.cpuModel());
    if (environment.scope() == Scope.RENDERING) {
      environmentObject.addProperty("glVendor", environment.glVendor());
      environmentObject.addProperty("glRenderer", environment.glRenderer());
      environmentObject.addProperty("glDriverVersion", environment.glDriverVersion());
      environmentObject.addProperty("glVersion", environment.glVersion());
    }
    object.add("environment", environmentObject);

    JsonObject settingsObject = new JsonObject();
    benchmarkSettings.forEach(settingsObject::addProperty);
    object.add("benchmarkSettings", settingsObject);

    JsonObject implementationObject = new JsonObject();
    implementationObject.addProperty(
        "implementationRevision", implementation.implementationRevision());
    implementationObject.addProperty("buildRevision", implementation.buildRevision());
    implementationObject.addProperty("commitRevision", implementation.commitRevision());
    object.add("implementation", implementationObject);
    return object;
  }

  public String semanticId() {
    return semanticId;
  }

  public String benchmarkVersion() {
    return benchmarkVersion;
  }

  public String workloadVersion() {
    return workloadVersion;
  }

  public String resultSchemaVersion() {
    return resultSchemaVersion;
  }

  public String behaviorContractVersion() {
    return behaviorContractVersion;
  }

  public EvidenceMode evidenceMode() {
    return evidenceMode;
  }

  /** Presentation-only label excluded from every equality fingerprint. */
  public String displayLabel() {
    return displayLabel;
  }

  public Environment environment() {
    return environment;
  }

  public SortedMap<String, String> benchmarkSettings() {
    return benchmarkSettings;
  }

  /** Traceability metadata deliberately excluded from every equality fingerprint. */
  public Implementation implementation() {
    return implementation;
  }

  public Fingerprints fingerprints() {
    return fingerprints;
  }

  /** Returns the exact UTF-8 text hashed for one golden fingerprint component. */
  public String canonicalSerialization(FingerprintComponent component) {
    Objects.requireNonNull(component, "component");
    return switch (component) {
      case IDENTITY -> canonical("identity", identityFields());
      case WORKLOAD -> canonical("workload", workloadFields());
      case ENVIRONMENT -> canonical("environment", environment.fields());
      case SETTINGS -> canonical("settings", benchmarkSettings);
      case REQUIRED ->
          canonical(
              "required",
              Map.of(
                  "environment", fingerprints.environment(),
                  "identity", fingerprints.identity(),
                  "settings", fingerprints.settings(),
                  "workload", fingerprints.workload()));
    };
  }

  /** Returns exact, human-readable equality-field differences in canonical group/key order. */
  public Comparison compare(ComparabilityMetadata other) {
    Objects.requireNonNull(other, "other");
    List<String> reasons = new ArrayList<>();
    differences("identity", identityFields(), other.identityFields(), reasons);
    differences("workload", workloadFields(), other.workloadFields(), reasons);
    differences("environment", environment.fields(), other.environment.fields(), reasons);
    differences("settings", benchmarkSettings, other.benchmarkSettings, reasons);
    return new Comparison(reasons.isEmpty(), List.copyOf(reasons));
  }

  private Fingerprints createFingerprints() {
    String identity = fingerprint("identity", identityFields());
    String workload = fingerprint("workload", workloadFields());
    String environmentFingerprint = fingerprint("environment", environment.fields());
    String settingsFingerprint = fingerprint("settings", benchmarkSettings);
    return new Fingerprints(
        identity,
        workload,
        environmentFingerprint,
        settingsFingerprint,
        fingerprint(
            "required",
            Map.of(
                "environment", environmentFingerprint,
                "identity", identity,
                "settings", settingsFingerprint,
                "workload", workload)));
  }

  private SortedMap<String, String> identityFields() {
    return sorted(
        Map.of(
            "behavior-contract-version", behaviorContractVersion,
            "benchmark-version", benchmarkVersion,
            "evidence-mode", evidenceMode.json,
            "fingerprint-schema-version", Integer.toString(FINGERPRINT_SCHEMA_VERSION),
            "result-schema-version", resultSchemaVersion,
            "semantic-id", semanticId,
            "workload-version", workloadVersion));
  }

  private SortedMap<String, String> workloadFields() {
    return sorted(
        Map.of(
            "font-inputs-sha256", fontInputsSha256,
            "workload-content-sha256", workloadContentSha256,
            "workload-shape-sha256", workloadShapeSha256));
  }

  private static void differences(
      String group,
      Map<String, String> current,
      Map<String, String> previous,
      List<String> reasons) {
    TreeMap<String, String> keys = new TreeMap<>(current);
    for (String key : previous.keySet()) keys.putIfAbsent(key, null);
    for (String key : keys.keySet()) {
      if (!Objects.equals(current.get(key), previous.get(key))) {
        reasons.add(group + "." + key + " differs");
      }
    }
  }

  private static String fingerprint(String group, Map<String, String> fields) {
    return hash(canonical(group, fields));
  }

  private static String canonical(String group, Map<String, String> fields) {
    StringBuilder canonical =
        new StringBuilder("spinygui-comparability:v")
            .append(FINGERPRINT_SCHEMA_VERSION)
            .append('\n');
    append(canonical, "group", group);
    for (Map.Entry<String, String> entry : new TreeMap<>(fields).entrySet()) {
      append(canonical, entry.getKey(), entry.getValue());
    }
    return canonical.toString();
  }

  private static String hash(String canonical) {
    try {
      byte[] hash =
          MessageDigest.getInstance("SHA-256")
              .digest(canonical.toString().getBytes(StandardCharsets.UTF_8));
      StringBuilder hexadecimal = new StringBuilder("sha256:");
      for (byte value : hash) hexadecimal.append(String.format(Locale.ROOT, "%02x", value & 0xff));
      return hexadecimal.toString();
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Required SHA-256 digest is unavailable", exception);
    }
  }

  private static void append(StringBuilder target, String key, String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    target.append(key).append('=').append(bytes.length).append(':').append(value).append('\n');
  }

  private static SortedMap<String, String> settings(
      Map<String, String> values, Scope scope, EvidenceMode evidenceMode) {
    Objects.requireNonNull(values, "benchmarkSettings");
    Set<String> required =
        evidenceMode == EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED
            ? scope == Scope.CPU ? CPU_COUNTER_SETTINGS : RENDERING_COUNTER_SETTINGS
            : scope == Scope.CPU ? CPU_SETTINGS : RENDERING_SETTINGS;
    String settingsSchema =
        evidenceMode == EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED
            ? "counter-only"
            : scope.json;
    if (!values.keySet().equals(required)) {
      TreeMap<String, String> missing = new TreeMap<>();
      for (String key : required) if (!values.containsKey(key)) missing.put(key, "missing");
      TreeMap<String, String> extra = new TreeMap<>();
      for (String key : values.keySet()) if (!required.contains(key)) extra.put(key, "unexpected");
      throw new IllegalArgumentException(
          "benchmarkSettings must match the complete " + settingsSchema
              + " schema; missing=" + missing.keySet() + ", unexpected=" + extra.keySet());
    }
    TreeMap<String, String> canonical = new TreeMap<>();
    for (Map.Entry<String, String> entry : values.entrySet()) {
      String key = Objects.requireNonNull(entry.getKey(), "benchmark setting key");
      if (!CANONICAL_KEY.matcher(key).matches()) {
        throw new IllegalArgumentException("Invalid benchmark setting key: " + key);
      }
      canonical.put(key, text(entry.getValue(), "benchmarkSettings." + key));
    }
    return Collections.unmodifiableSortedMap(canonical);
  }

  private static SortedMap<String, String> sorted(Map<String, String> values) {
    return Collections.unmodifiableSortedMap(new TreeMap<>(values));
  }

  private static String digest(String value, String name) {
    String canonical = text(value, name);
    if (!SHA_256.matcher(canonical).matches()) {
      throw new IllegalArgumentException(name + " must be a lowercase sha256 digest");
    }
    return canonical;
  }

  private static String text(String value, String name) {
    if (value == null) throw new IllegalArgumentException(name + " is required");
    String canonical = Normalizer.normalize(value, Normalizer.Form.NFC).trim();
    if (canonical.isEmpty()) throw new IllegalArgumentException(name + " must not be blank");
    return canonical;
  }

  private static JsonElement required(JsonObject object, String name) {
    JsonElement value = object.get(name);
    if (value == null || value.isJsonNull()) {
      throw new IllegalArgumentException("Missing required comparability field: " + name);
    }
    return value;
  }

  private static String string(JsonObject object, String name) {
    return string(required(object, name), name);
  }

  private static String string(JsonElement value, String name) {
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
      throw new IllegalArgumentException("Comparability field must be a JSON string: " + name);
    }
    return value.getAsString();
  }

  private static String optionalString(JsonObject object, String name) {
    JsonElement value = object.get(name);
    return value == null ? null : string(value, name);
  }

  private static JsonObject object(JsonObject parent, String name) {
    JsonElement value = required(parent, name);
    if (!value.isJsonObject()) {
      throw new IllegalArgumentException("Comparability field must be a JSON object: " + name);
    }
    return value.getAsJsonObject();
  }

  private static int integer(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
      throw new IllegalArgumentException("Comparability field must be a JSON integer: " + name);
    }
    try {
      return new BigDecimal(value.getAsString()).intValueExact();
    } catch (ArithmeticException | NumberFormatException exception) {
      throw new IllegalArgumentException(
          "Comparability field must be a JSON integer: " + name, exception);
    }
  }

  private static void rejectUnknown(JsonObject object, Set<String> fields, String path) {
    for (String name : object.keySet()) {
      if (!fields.contains(name)) {
        throw new IllegalArgumentException("Unknown " + path + " field: " + name);
      }
    }
    JsonElement extensions = object.get("extensions");
    if (extensions != null && !extensions.isJsonObject()) {
      throw new IllegalArgumentException(path + ".extensions must be an object");
    }
  }

  public enum Scope {
    CPU("cpu"),
    RENDERING("rendering");

    private final String json;

    Scope(String json) {
      this.json = json;
    }

    private static Scope fromJson(String value) {
      for (Scope scope : values()) if (scope.json.equals(value)) return scope;
      throw new IllegalArgumentException("Unknown comparability environment scope: " + value);
    }
  }

  public enum EvidenceMode {
    COUNTER_ONLY_DIAGNOSTICS_ENABLED("counter-only-diagnostics-enabled"),
    TIMED_ALLOCATION_DIAGNOSTICS_DISABLED("timed-allocation-diagnostics-disabled");

    private final String json;

    EvidenceMode(String json) {
      this.json = json;
    }

    public String json() {
      return json;
    }

    private static EvidenceMode fromJson(String value) {
      for (EvidenceMode mode : values()) if (mode.json.equals(value)) return mode;
      throw new IllegalArgumentException("Unknown benchmark evidence mode: " + value);
    }
  }

  /** Canonical relevant runtime environment; unavailable CPU identity is represented explicitly. */
  public record Environment(
      Scope scope,
      String jvmVendor,
      String jvmVersion,
      String osName,
      String osVersion,
      String osArchitecture,
      String cpuModel,
      String glVendor,
      String glRenderer,
      String glDriverVersion,
      String glVersion) {
    public Environment {
      Objects.requireNonNull(scope, "scope");
      jvmVendor = text(jvmVendor, "environment.jvmVendor");
      jvmVersion = text(jvmVersion, "environment.jvmVersion");
      osName = text(osName, "environment.osName");
      osVersion = text(osVersion, "environment.osVersion");
      osArchitecture = text(osArchitecture, "environment.osArchitecture");
      cpuModel = text(cpuModel, "environment.cpuModel");
      if (scope == Scope.RENDERING) {
        glVendor = text(glVendor, "environment.glVendor");
        glRenderer = text(glRenderer, "environment.glRenderer");
        glDriverVersion = text(glDriverVersion, "environment.glDriverVersion");
        glVersion = text(glVersion, "environment.glVersion");
      } else if (glVendor != null || glRenderer != null || glDriverVersion != null || glVersion != null) {
        throw new IllegalArgumentException("CPU environment must not include irrelevant GL fields");
      }
    }

    private SortedMap<String, String> fields() {
      TreeMap<String, String> fields = new TreeMap<>();
      fields.put("cpu-model", cpuModel);
      fields.put("jvm-vendor", jvmVendor);
      fields.put("jvm-version", jvmVersion);
      fields.put("os-architecture", osArchitecture);
      fields.put("os-name", osName);
      fields.put("os-version", osVersion);
      fields.put("scope", scope.json);
      if (scope == Scope.RENDERING) {
        fields.put("gl-driver-version", glDriverVersion);
        fields.put("gl-renderer", glRenderer);
        fields.put("gl-vendor", glVendor);
        fields.put("gl-version", glVersion);
      }
      return Collections.unmodifiableSortedMap(fields);
    }
  }

  /** Implementation-under-test provenance; none of these fields participate in equality. */
  public record Implementation(
      String implementationRevision, String buildRevision, String commitRevision) {
    public Implementation {
      implementationRevision = text(implementationRevision, "implementationRevision");
      buildRevision = text(buildRevision, "buildRevision");
      commitRevision = text(commitRevision, "commitRevision");
    }
  }

  public record Fingerprints(
      String identity,
      String workload,
      String environment,
      String settings,
      String required) {}

  public enum FingerprintComponent {
    IDENTITY,
    WORKLOAD,
    ENVIRONMENT,
    SETTINGS,
    REQUIRED
  }

  public record Comparison(boolean comparable, List<String> reasons) {
    public Comparison {
      reasons = List.copyOf(reasons);
      if (comparable != reasons.isEmpty()) {
        throw new IllegalArgumentException("Comparable results must have no mismatch reasons");
      }
    }

    public String reason() {
      return comparable ? "comparable" : String.join(", ", reasons);
    }
  }
}
