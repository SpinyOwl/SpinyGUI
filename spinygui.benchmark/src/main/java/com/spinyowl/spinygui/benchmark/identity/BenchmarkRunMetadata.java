package com.spinyowl.spinygui.benchmark.identity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import java.util.Objects;
import java.util.Set;

/** Shared run ownership and baseline-eligibility metadata for one benchmark artifact. */
public record BenchmarkRunMetadata(
    int schemaVersion,
    String runId,
    Artifact artifact,
    Pairing pairing,
    EvidenceMode evidenceMode) {
  public static final int SCHEMA_VERSION = 1;

  private static final Set<String> FIELDS =
      Set.of("schemaVersion", "runId", "artifact", "pairing", "evidenceMode");

  public BenchmarkRunMetadata {
    if (schemaVersion != SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported benchmark run schema version: " + schemaVersion);
    }
    runId = requiredText(runId, "runId");
    Objects.requireNonNull(artifact, "artifact");
    Objects.requireNonNull(pairing, "pairing");
    Objects.requireNonNull(evidenceMode, "evidenceMode");
  }

  public static BenchmarkRunMetadata paired(
      String runId, Artifact artifact, EvidenceMode evidenceMode) {
    return new BenchmarkRunMetadata(
        SCHEMA_VERSION, runId, artifact, Pairing.PAIRED_REPORT, evidenceMode);
  }

  public static BenchmarkRunMetadata investigation(
      String runId, Artifact artifact, EvidenceMode evidenceMode) {
    return new BenchmarkRunMetadata(
        SCHEMA_VERSION, runId, artifact, Pairing.UNPAIRED_INVESTIGATION, evidenceMode);
  }

  public boolean baselineEligible() {
    return pairing == Pairing.PAIRED_REPORT
        && evidenceMode == EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED;
  }

  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("schemaVersion", schemaVersion);
    object.addProperty("runId", runId);
    object.addProperty("artifact", artifact.json);
    object.addProperty("pairing", pairing.json);
    object.addProperty("evidenceMode", evidenceMode.json());
    return object;
  }

  public static BenchmarkRunMetadata fromJson(JsonObject object) {
    Objects.requireNonNull(object, "object");
    for (String field : object.keySet()) {
      if (!FIELDS.contains(field)) {
        throw new IllegalArgumentException("Unknown benchmark run metadata field: " + field);
      }
    }
    return new BenchmarkRunMetadata(
        integer(object, "schemaVersion"),
        string(object, "runId"),
        Artifact.fromJson(string(object, "artifact")),
        Pairing.fromJson(string(object, "pairing")),
        evidenceMode(string(object, "evidenceMode")));
  }

  private static EvidenceMode evidenceMode(String value) {
    for (EvidenceMode mode : EvidenceMode.values()) {
      if (mode.json().equals(value)) return mode;
    }
    throw new IllegalArgumentException("Unknown benchmark evidence mode: " + value);
  }

  private static int integer(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
      throw new IllegalArgumentException("Benchmark run field must be a JSON integer: " + name);
    }
    try {
      return value.getAsBigDecimal().intValueExact();
    } catch (ArithmeticException | NumberFormatException exception) {
      throw new IllegalArgumentException(
          "Benchmark run field must be a JSON integer: " + name, exception);
    }
  }

  private static String string(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
      throw new IllegalArgumentException("Benchmark run field must be a JSON string: " + name);
    }
    return value.getAsString();
  }

  private static JsonElement required(JsonObject object, String name) {
    JsonElement value = object.get(name);
    if (value == null || value.isJsonNull()) {
      throw new IllegalArgumentException("Missing required benchmark run field: " + name);
    }
    return value;
  }

  private static String requiredText(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(name + " must not be blank");
    }
    return value;
  }

  public enum Artifact {
    CPU("cpu"),
    RENDERING("rendering"),
    COUNTER_DIAGNOSTICS("counter-diagnostics"),
    FRAME_BASELINE("frame-baseline");

    private final String json;

    Artifact(String json) {
      this.json = json;
    }

    private static Artifact fromJson(String value) {
      for (Artifact artifact : values()) if (artifact.json.equals(value)) return artifact;
      throw new IllegalArgumentException("Unknown benchmark artifact role: " + value);
    }
  }

  public enum Pairing {
    PAIRED_REPORT("paired-report"),
    UNPAIRED_INVESTIGATION("unpaired-investigation");

    private final String json;

    Pairing(String json) {
      this.json = json;
    }

    public String json() {
      return json;
    }

    public static Pairing fromJson(String value) {
      for (Pairing pairing : values()) if (pairing.json.equals(value)) return pairing;
      throw new IllegalArgumentException("Unknown benchmark pairing eligibility: " + value);
    }
  }
}
