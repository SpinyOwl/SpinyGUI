package com.spinyowl.spinygui.benchmark.rendering;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation.Evidence;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation.Path;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;

/** Strict versioned root evidence shared by rendering producer and report eligibility. */
public record StructuralValidationReport(
    String schemaVersion,
    String validatorVersion,
    String synchronizedExposureScene,
    Evidence synchronizedExposureEvidence,
    List<Evidence> scenes) {
  public static final String SCHEMA_VERSION = "structural-validation-report-v1";
  public static final List<String> APPROVED_SCENE_ORDER =
      List.of(
          "fallback-overhang",
          "nested-clipping",
          "selection-caret",
          "transformed-text");

  private static final Pattern SHA256 = Pattern.compile("sha256:[0-9a-f]{64}");
  private static final Set<String> ROOT_FIELDS =
      Set.of(
          "schemaVersion",
          "validatorVersion",
          "synchronizedExposureScene",
          "synchronizedExposureEvidence",
          "scenes");
  private static final Set<String> EVIDENCE_FIELDS =
      Set.of(
          "validatorVersion",
          "sceneId",
          "sourceExpectationSha256",
          "commandDigestSha256",
          "commandCount",
          "submittedText",
          "submittedTextByPath",
          "selectedFaceIds",
          "faceCommands",
          "alignmentCommands",
          "transformCommands",
          "clipCommands",
          "selectionCommands",
          "caretCommands",
          "nonIdentityTransform",
          "replacementSubmitted",
          "overhangSubmitted",
          "evidenceDigestSha256");

  public StructuralValidationReport {
    if (!SCHEMA_VERSION.equals(schemaVersion)) throw invalid("unsupported schema version");
    if (!NvgStructuralValidation.VALIDATOR_VERSION.equals(validatorVersion)) {
      throw invalid("unsupported validator version");
    }
    if (!"small".equals(synchronizedExposureScene)) {
      throw invalid("synchronized exposure must be small");
    }
    validateEvidence(synchronizedExposureEvidence);
    if (!"benchmark-small".equals(synchronizedExposureEvidence.sceneId())) {
      throw invalid("synchronized exposure evidence must be benchmark-small");
    }
    scenes = List.copyOf(scenes);
    if (!scenes.stream().map(Evidence::sceneId).toList().equals(APPROVED_SCENE_ORDER)) {
      throw invalid("approved scene order differs");
    }
    for (Evidence evidence : scenes) validateEvidence(evidence);
    List<Evidence> expected =
        RenderingBoundaryScenes.validateAll(validationFontService());
    if (!scenes.equals(expected)) {
      throw invalid("approved source-bound scene commands differ from executable definitions");
    }
    if (!synchronizedExposureEvidence.equals(
        RenderingBoundaryScenes.synchronizedSmallFixtureEvidence(validationFontService()))) {
      throw invalid("synchronized small-scene commands differ from executable definition");
    }
  }

  public static StructuralValidationReport create(
      Evidence synchronizedExposureEvidence, List<Evidence> scenes) {
    return new StructuralValidationReport(
        SCHEMA_VERSION,
        NvgStructuralValidation.VALIDATOR_VERSION,
        "small",
        synchronizedExposureEvidence,
        scenes);
  }

  public static StructuralValidationReport fromJson(JsonObject object) {
    exactFields(object, ROOT_FIELDS, "structuralValidation");
    String schemaVersion = string(object, "schemaVersion");
    String validatorVersion = string(object, "validatorVersion");
    String synchronizedExposureScene = string(object, "synchronizedExposureScene");
    Evidence synchronizedExposureEvidence =
        evidenceFromJson(object(object, "synchronizedExposureEvidence"));
    JsonArray array = array(object, "scenes");
    List<Evidence> evidence = new ArrayList<>();
    for (int index = 0; index < array.size(); index++) {
      JsonElement element = array.get(index);
      if (!element.isJsonObject()) throw invalid("scene evidence must be an object");
      evidence.add(evidenceFromJson(element.getAsJsonObject()));
    }
    return new StructuralValidationReport(
        schemaVersion,
        validatorVersion,
        synchronizedExposureScene,
        synchronizedExposureEvidence,
        evidence);
  }

  public JsonObject toJson() {
    JsonObject root = new JsonObject();
    root.addProperty("schemaVersion", schemaVersion);
    root.addProperty("validatorVersion", validatorVersion);
    root.addProperty("synchronizedExposureScene", synchronizedExposureScene);
    root.add("synchronizedExposureEvidence", evidenceToJson(synchronizedExposureEvidence));
    JsonArray array = new JsonArray();
    scenes.stream().map(StructuralValidationReport::evidenceToJson).forEach(array::add);
    root.add("scenes", array);
    return root;
  }

  public int commandCount() {
    return synchronizedExposureEvidence.commandCount()
        + scenes.stream().mapToInt(Evidence::commandCount).sum();
  }

  public String status() {
    return "passed (" + scenes.size() + " approved scenes, " + commandCount() + " commands)";
  }

  private static Evidence evidenceFromJson(JsonObject object) {
    exactFields(object, EVIDENCE_FIELDS, "structuralValidation.scenes[]");
    Map<Path, Long> byPath = new LinkedHashMap<>();
    JsonObject paths = object(object, "submittedTextByPath");
    exactFields(paths, Set.of("NORMAL", "INPUT", "TEXTAREA"), "submittedTextByPath");
    for (Path path : Path.values()) byPath.put(path, nonNegativeLong(paths, path.name()));
    return new Evidence(
        string(object, "validatorVersion"),
        string(object, "sceneId"),
        digest(object, "sourceExpectationSha256"),
        digest(object, "commandDigestSha256"),
        positiveInt(object, "commandCount"),
        strings(object, "submittedText", false),
        byPath,
        strings(object, "selectedFaceIds", true),
        nonNegativeLong(object, "faceCommands"),
        nonNegativeLong(object, "alignmentCommands"),
        nonNegativeLong(object, "transformCommands"),
        nonNegativeLong(object, "clipCommands"),
        nonNegativeLong(object, "selectionCommands"),
        nonNegativeLong(object, "caretCommands"),
        bool(object, "nonIdentityTransform"),
        bool(object, "replacementSubmitted"),
        bool(object, "overhangSubmitted"),
        digest(object, "evidenceDigestSha256"));
  }

  private static JsonObject evidenceToJson(Evidence evidence) {
    JsonObject object = new JsonObject();
    object.addProperty("validatorVersion", evidence.validatorVersion());
    object.addProperty("sceneId", evidence.sceneId());
    object.addProperty("sourceExpectationSha256", evidence.sourceExpectationSha256());
    object.addProperty("commandDigestSha256", evidence.commandDigestSha256());
    object.addProperty("commandCount", evidence.commandCount());
    object.add("submittedText", strings(evidence.submittedText()));
    JsonObject paths = new JsonObject();
    for (Path path : Path.values()) paths.addProperty(path.name(), evidence.submittedTextByPath().get(path));
    object.add("submittedTextByPath", paths);
    object.add("selectedFaceIds", strings(evidence.selectedFaceIds()));
    object.addProperty("faceCommands", evidence.faceCommands());
    object.addProperty("alignmentCommands", evidence.alignmentCommands());
    object.addProperty("transformCommands", evidence.transformCommands());
    object.addProperty("clipCommands", evidence.clipCommands());
    object.addProperty("selectionCommands", evidence.selectionCommands());
    object.addProperty("caretCommands", evidence.caretCommands());
    object.addProperty("nonIdentityTransform", evidence.nonIdentityTransform());
    object.addProperty("replacementSubmitted", evidence.replacementSubmitted());
    object.addProperty("overhangSubmitted", evidence.overhangSubmitted());
    object.addProperty("evidenceDigestSha256", evidence.evidenceDigestSha256());
    return object;
  }

  private static void validateEvidence(Evidence evidence) {
    if (!NvgStructuralValidation.VALIDATOR_VERSION.equals(evidence.validatorVersion())
        || evidence.commandCount() <= 0
        || evidence.submittedText().isEmpty()
        || evidence.submittedText().stream().anyMatch(String::isEmpty)
        || !SHA256.matcher(evidence.sourceExpectationSha256()).matches()
        || !SHA256.matcher(evidence.commandDigestSha256()).matches()
        || !SHA256.matcher(evidence.evidenceDigestSha256()).matches()
        || !NvgStructuralValidation.evidenceDigestValid(evidence)) {
      throw invalid("scene evidence is not a valid validator success proof: " + evidence.sceneId());
    }
    long submitted = evidence.submittedTextByPath().values().stream().mapToLong(Long::longValue).sum();
    if (submitted != evidence.submittedText().size()
        || evidence.faceCommands() != submitted
        || evidence.alignmentCommands() == 0
        || evidence.transformCommands() == 0) {
      throw invalid("scene command evidence does not reconcile: " + evidence.sceneId());
    }
    switch (evidence.sceneId()) {
      case "fallback-overhang" -> {
        if (!evidence.submittedText().equals(List.of("A", "雪", "�"))
            || evidence.selectedFaceIds().stream().distinct().count() < 2
            || !evidence.replacementSubmitted()
            || !evidence.overhangSubmitted()) throw invalid("fallback-overhang semantics differ");
      }
      case "nested-clipping" -> {
        if (!evidence.submittedText().equals(List.of("nested", "clipping", "boundary"))
            || evidence.clipCommands() < 2) throw invalid("nested-clipping semantics differ");
      }
      case "selection-caret" -> {
        if (!evidence.submittedText().equals(List.of("selection caret"))
            || evidence.selectionCommands() != 1
            || evidence.caretCommands() != 1
            || evidence.clipCommands() == 0) throw invalid("selection-caret semantics differ");
      }
      case "transformed-text" -> {
        if (!evidence.submittedText().equals(List.of("transformed", " ", "text"))
            || !evidence.nonIdentityTransform()) throw invalid("transformed-text semantics differ");
      }
      case "benchmark-small" -> {
        if (evidence.submittedTextByPath().get(Path.NORMAL) != evidence.submittedText().size()) {
          throw invalid("benchmark-small must use normal text commands");
        }
      }
      default -> throw invalid("unknown scene");
    }
  }

  private static JsonArray strings(List<String> values) {
    JsonArray array = new JsonArray();
    values.forEach(array::add);
    return array;
  }

  private static List<String> strings(JsonObject object, String name, boolean allowEmpty) {
    JsonArray array = array(object, name);
    if (!allowEmpty && array.isEmpty()) throw invalid(name + " must not be empty");
    List<String> values = new ArrayList<>();
    for (JsonElement value : array) {
      if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
        throw invalid(name + " must contain strings");
      }
      String string = value.getAsString();
      if (string.isEmpty()) throw invalid(name + " must not contain empty strings");
      values.add(string);
    }
    return List.copyOf(values);
  }

  private static String digest(JsonObject object, String name) {
    String value = string(object, name);
    if (!SHA256.matcher(value).matches()) throw invalid(name + " must be SHA-256");
    return value;
  }

  private static String string(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
      throw invalid(name + " must be a string");
    }
    return value.getAsString();
  }

  private static boolean bool(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
      throw invalid(name + " must be a boolean");
    }
    return value.getAsBoolean();
  }

  private static int positiveInt(JsonObject object, String name) {
    long value = nonNegativeLong(object, name);
    if (value <= 0 || value > Integer.MAX_VALUE) throw invalid(name + " must be a positive integer");
    return (int) value;
  }

  private static long nonNegativeLong(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
      throw invalid(name + " must be an integer");
    }
    try {
      long result = new BigDecimal(value.getAsString()).longValueExact();
      if (result < 0) throw invalid(name + " must be non-negative");
      return result;
    } catch (ArithmeticException exception) {
      throw invalid(name + " must be an integer");
    }
  }

  private static JsonObject object(JsonObject parent, String name) {
    JsonElement value = required(parent, name);
    if (!value.isJsonObject()) throw invalid(name + " must be an object");
    return value.getAsJsonObject();
  }

  private static JsonArray array(JsonObject parent, String name) {
    JsonElement value = required(parent, name);
    if (!value.isJsonArray()) throw invalid(name + " must be an array");
    return value.getAsJsonArray();
  }

  private static JsonElement required(JsonObject object, String name) {
    JsonElement value = object.get(name);
    if (value == null || value.isJsonNull()) throw invalid("missing " + name);
    return value;
  }

  private static void exactFields(JsonObject object, Set<String> expected, String path) {
    if (!object.keySet().equals(expected)) {
      throw invalid(path + " fields differ; expected=" + expected + " actual=" + object.keySet());
    }
  }

  private static IllegalArgumentException invalid(String detail) {
    return new IllegalArgumentException("Invalid structural validation report: " + detail);
  }

  private static com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl validationFontService() {
    return RenderingWorkloadSpecifications.CURRENT.createFontService(DiagnosticSession.disabled());
  }
}
