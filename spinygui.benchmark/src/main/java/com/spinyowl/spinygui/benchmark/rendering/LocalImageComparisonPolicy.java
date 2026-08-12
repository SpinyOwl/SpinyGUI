package com.spinyowl.spinygui.benchmark.rendering;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.imageio.ImageIO;

/** Fail-closed implementation for optional machine-local renderer boundary image evidence. */
public final class LocalImageComparisonPolicy {
  public static final String POLICY_VERSION = "local-text-image-policy-v1";
  public static final String REFERENCE_VERSION = "reference-v1";
  public static final String OPT_IN_PROPERTY = "spinygui.rendering.localImageComparison";

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Set<String> REFERENCE_FIELDS =
      Set.of("policyVersion", "referenceVersion", "sceneId", "environment");
  private static final Set<String> ENVIRONMENT_FIELDS =
      Set.of(
          "jvmVendor",
          "jvmVersion",
          "osName",
          "osVersion",
          "osArchitecture",
          "glVendor",
          "glRenderer",
          "glDriverVersion",
          "glVersion",
          "backend",
          "antialiasing",
          "width",
          "height",
          "pixelRatio");

  private LocalImageComparisonPolicy() {}

  public static Policy policy() {
    return new Policy(
        POLICY_VERSION,
        REFERENCE_VERSION,
        new Tolerance(2, 2, 6, 0.005, 1),
        List.of(
            new BoundaryScene("fallback-overhang", List.of("fallback", "overhang")),
            new BoundaryScene("nested-clipping", List.of("clipping")),
            new BoundaryScene("selection-caret", List.of("selection", "caret")),
            new BoundaryScene("transformed-text", List.of("transform"))));
  }

  /** Evaluates policy state without reading or comparing any image. */
  public static Evaluation evaluate(Request request) {
    Objects.requireNonNull(request, "request");
    if (!request.structuralEvidencePassed()) {
      return unvalidated(Status.FAILED_STRUCTURAL, "portable structural evidence failed");
    }
    if (!request.optedIn()) {
      return unvalidated(Status.UNVALIDATED_OPT_OUT, "local image comparison not opted in");
    }
    if (request.reference() == null) {
      return unvalidated(Status.UNVALIDATED_REFERENCE_MISSING, "compatible reference missing");
    }
    Reference reference = request.reference();
    if (!approvedScene(reference.sceneId())) {
      return unvalidated(Status.UNVALIDATED_SCENE_MISMATCH, "reference scene is not approved");
    }
    if (!request.currentEnvironment().equals(reference.environment())) {
      return unvalidated(
          Status.UNVALIDATED_ENVIRONMENT_MISMATCH, "exact rendering environment differs");
    }
    if (!reference.policyVersion().equals(POLICY_VERSION)
        || !reference.referenceVersion().equals(REFERENCE_VERSION)) {
      return unvalidated(
          Status.UNVALIDATED_REFERENCE_VERSION, "reference policy/version differs");
    }
    return new Evaluation(Status.READY_TO_COMPARE, true, "explicit opt-in and exact reference match");
  }

  /** Executes the complete file-based workflow after the portable structural gate passes. */
  public static ComparisonOutcome compareConfigured(
      boolean structuralEvidencePassed,
      String sceneId,
      EnvironmentFingerprint currentEnvironment,
      Path actualImage,
      Path referenceRoot,
      Path artifactRoot)
      throws IOException {
    boolean optedIn = Boolean.getBoolean(OPT_IN_PROPERTY);
    ReferenceFiles files = referenceFiles(referenceRoot, sceneId, currentEnvironment);
    if (!structuralEvidencePassed) {
      return new ComparisonOutcome(
          unvalidated(Status.FAILED_STRUCTURAL, "portable structural evidence failed"), null, null);
    }
    if (!optedIn) {
      return new ComparisonOutcome(
          unvalidated(Status.UNVALIDATED_OPT_OUT, "local image comparison not opted in"), null, null);
    }
    Reference reference;
    try {
      reference = Files.isRegularFile(files.manifest()) ? readReference(files.manifest()) : null;
    } catch (IllegalArgumentException failure) {
      return new ComparisonOutcome(
          unvalidated(Status.UNVALIDATED_REFERENCE_MANIFEST, failure.getMessage()), null, null);
    }
    Evaluation evaluation =
        evaluate(new Request(structuralEvidencePassed, optedIn, currentEnvironment, reference));
    if (!evaluation.comparisonAllowed()) {
      return new ComparisonOutcome(evaluation, null, null);
    }
    if (!reference.sceneId().equals(sceneId)) {
      return new ComparisonOutcome(
          unvalidated(Status.UNVALIDATED_SCENE_MISMATCH, "reference scene differs"), null, null);
    }
    if (!Files.isRegularFile(files.image())) {
      return new ComparisonOutcome(
          unvalidated(Status.UNVALIDATED_REFERENCE_MISSING, "reference image missing"), null, null);
    }
    BufferedImage actual = readImage(actualImage, "actual");
    BufferedImage expected = readImage(files.image(), "reference");
    ComparisonResult comparison = compare(actual, expected, policy().tolerance());
    if (comparison.passed()) {
      return new ComparisonOutcome(
          new Evaluation(Status.PASSED, false, "local image comparison passed"), comparison, null);
    }
    Path mismatch = writeMismatchArtifacts(
        artifactRoot, sceneId, actual, expected, currentEnvironment, comparison);
    return new ComparisonOutcome(
        new Evaluation(Status.FAILED_IMAGE_COMPARISON, false, "local image comparison failed"),
        comparison,
        mismatch);
  }

  public static Reference readReference(Path manifest) throws IOException {
    try {
      JsonElement parsed = JsonParser.parseString(Files.readString(manifest));
      if (!parsed.isJsonObject()) throw invalidManifest("root must be an object");
      JsonObject object = parsed.getAsJsonObject();
      exactFields(object, REFERENCE_FIELDS, "reference");
      JsonObject environment = requiredObject(object, "environment");
      exactFields(environment, ENVIRONMENT_FIELDS, "reference.environment");
      Reference reference =
          new Reference(
              requiredString(object, "policyVersion"),
              requiredString(object, "referenceVersion"),
              approvedSceneId(requiredString(object, "sceneId")),
              new EnvironmentFingerprint(
                  requiredString(environment, "jvmVendor"),
                  requiredString(environment, "jvmVersion"),
                  requiredString(environment, "osName"),
                  requiredString(environment, "osVersion"),
                  requiredString(environment, "osArchitecture"),
                  requiredString(environment, "glVendor"),
                  requiredString(environment, "glRenderer"),
                  requiredString(environment, "glDriverVersion"),
                  requiredString(environment, "glVersion"),
                  requiredString(environment, "backend"),
                  requiredBoolean(environment, "antialiasing"),
                  requiredPositiveInt(environment, "width"),
                  requiredPositiveInt(environment, "height"),
                  requiredPositiveFloat(environment, "pixelRatio")));
      if (!POLICY_VERSION.equals(reference.policyVersion())
          || !REFERENCE_VERSION.equals(reference.referenceVersion())) {
        throw invalidManifest("unsupported policy/reference version");
      }
      return reference;
    } catch (RuntimeException failure) {
      if (failure instanceof IllegalArgumentException illegal) throw illegal;
      throw invalidManifest(failure.getMessage());
    }
  }

  public static void writeReference(Path referenceRoot, Reference reference, BufferedImage image)
      throws IOException {
    Objects.requireNonNull(reference, "reference");
    Objects.requireNonNull(image, "image");
    Evaluation evaluation =
        evaluate(new Request(true, true, reference.environment(), reference));
    if (!evaluation.comparisonAllowed()) {
      throw new IllegalArgumentException(evaluation.detail());
    }
    ReferenceFiles files = referenceFiles(referenceRoot, reference.sceneId(), reference.environment());
    Files.createDirectories(files.image().getParent());
    writePng(image, files.image());
    Files.writeString(files.manifest(), GSON.toJson(reference), StandardCharsets.UTF_8);
  }

  public static ComparisonResult compare(
      BufferedImage actual, BufferedImage expected, Tolerance tolerance) {
    Objects.requireNonNull(actual, "actual");
    Objects.requireNonNull(expected, "expected");
    Objects.requireNonNull(tolerance, "tolerance");
    if (actual.getWidth() != expected.getWidth() || actual.getHeight() != expected.getHeight()) {
      return new ComparisonResult(
          false,
          0,
          0,
          1,
          Integer.MAX_VALUE,
          true,
          new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
          new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    }
    int width = expected.getWidth();
    int height = expected.getHeight();
    boolean[] edges = edgeMask(expected, tolerance.antialiasFringeRadiusPixels());
    BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    int different = 0;
    int overLimit = 0;
    int maxDelta = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int index = y * width + x;
        int expectedArgb = expected.getRGB(x, y);
        int actualArgb = actual.getRGB(x, y);
        int[] deltas = channelDeltas(expectedArgb, actualArgb);
        int rgbDelta = Math.max(deltas[1], Math.max(deltas[2], deltas[3]));
        int alphaDelta = deltas[0];
        int applicableRgb =
            edges[index]
                ? tolerance.maxAntialiasFringeChannelDelta()
                : tolerance.maxRgbChannelDelta();
        boolean pixelDifferent = rgbDelta > 0 || alphaDelta > 0;
        int applicableAlpha =
            edges[index]
                ? tolerance.maxAntialiasFringeChannelDelta()
                : tolerance.maxAlphaDelta();
        boolean pixelOverLimit = rgbDelta > applicableRgb || alphaDelta > applicableAlpha;
        if (pixelDifferent) different++;
        if (pixelOverLimit) overLimit++;
        maxDelta = Math.max(maxDelta, Math.max(rgbDelta, alphaDelta));
        int amplified = Math.min(255, Math.max(rgbDelta, alphaDelta) * 16);
        diff.setRGB(x, y, new Color(amplified, 0, pixelOverLimit ? 255 : amplified, 255).getRGB());
        mask.setRGB(x, y, edges[index] ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
      }
    }
    double ratio = different / (double) (width * height);
    boolean passed = overLimit == 0 && ratio <= tolerance.maxDifferingPixelRatio();
    return new ComparisonResult(
        passed, different, overLimit, ratio, maxDelta, false, diff, mask);
  }

  public static Evaluation evaluateConfigured(
      boolean structuralEvidencePassed,
      EnvironmentFingerprint currentEnvironment,
      Reference reference) {
    return evaluate(
        new Request(
            structuralEvidencePassed,
            Boolean.getBoolean(OPT_IN_PROPERTY),
            currentEnvironment,
            reference));
  }

  public static String referenceName(String sceneId, EnvironmentFingerprint environment) {
    return "%s--%s--%s.png"
        .formatted(REFERENCE_VERSION, approvedSceneId(sceneId), environment.stableId());
  }

  public static ReferenceFiles referenceFiles(
      Path referenceRoot, String sceneId, EnvironmentFingerprint environment) {
    Path image = referenceRoot.resolve(approvedSceneId(sceneId)).resolve(referenceName(sceneId, environment));
    return new ReferenceFiles(image, image.resolveSibling(image.getFileName() + ".json"));
  }

  public static Path mismatchDirectory(Path artifactRoot, String sceneId) {
    return artifactRoot
        .resolve("mismatches")
        .resolve(POLICY_VERSION)
        .resolve(approvedSceneId(sceneId));
  }

  public static EnvironmentFingerprint environment(
      ComparabilityMetadata.Environment environment,
      String backend,
      boolean antialiasing,
      int width,
      int height,
      float pixelRatio) {
    if (environment.scope() != ComparabilityMetadata.Scope.RENDERING) {
      throw new IllegalArgumentException("Local image references require rendering environment data");
    }
    return new EnvironmentFingerprint(
        environment.jvmVendor(), environment.jvmVersion(), environment.osName(),
        environment.osVersion(), environment.osArchitecture(), environment.glVendor(),
        environment.glRenderer(), environment.glDriverVersion(), environment.glVersion(), backend,
        antialiasing, width, height, pixelRatio);
  }

  private static Path writeMismatchArtifacts(
      Path artifactRoot,
      String sceneId,
      BufferedImage actual,
      BufferedImage expected,
      EnvironmentFingerprint environment,
      ComparisonResult result)
      throws IOException {
    Path directory = mismatchDirectory(artifactRoot, sceneId);
    Files.createDirectories(directory);
    writePng(actual, directory.resolve("actual.png"));
    writePng(expected, directory.resolve("expected.png"));
    writePng(result.amplifiedDiff(), directory.resolve("amplified-diff.png"));
    writePng(result.edgeMask(), directory.resolve("edge-mask.png"));
    Files.writeString(directory.resolve("environment.json"), GSON.toJson(environment));
    Files.writeString(
        directory.resolve("summary.json"),
        GSON.toJson(
            new ComparisonSummary(
                result.passed(),
                result.differingPixels(),
                result.overLimitPixels(),
                result.differingPixelRatio(),
                result.maximumChannelDelta(),
                result.dimensionMismatch())));
    return directory;
  }

  private static BufferedImage readImage(Path path, String role) throws IOException {
    BufferedImage image = ImageIO.read(path.toFile());
    if (image == null) throw new IOException("Unable to decode " + role + " PNG: " + path);
    return image;
  }

  private static void writePng(BufferedImage image, Path path) throws IOException {
    Path temporary = path.resolveSibling(path.getFileName() + ".tmp");
    if (!ImageIO.write(image, "png", temporary.toFile())) {
      throw new IOException("PNG writer unavailable");
    }
    Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
  }

  private static boolean[] edgeMask(BufferedImage image, int radius) {
    int width = image.getWidth();
    int height = image.getHeight();
    boolean[] base = new boolean[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = image.getRGB(x, y);
        if ((x > 0 && image.getRGB(x - 1, y) != pixel)
            || (x + 1 < width && image.getRGB(x + 1, y) != pixel)
            || (y > 0 && image.getRGB(x, y - 1) != pixel)
            || (y + 1 < height && image.getRGB(x, y + 1) != pixel)) {
          base[y * width + x] = true;
        }
      }
    }
    if (radius == 0) return base;
    boolean[] expanded = Arrays.copyOf(base, base.length);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (!base[y * width + x]) continue;
        for (int dy = -radius; dy <= radius; dy++) {
          for (int dx = -radius; dx <= radius; dx++) {
            int xx = x + dx;
            int yy = y + dy;
            if (xx >= 0 && xx < width && yy >= 0 && yy < height) {
              expanded[yy * width + xx] = true;
            }
          }
        }
      }
    }
    return expanded;
  }

  private static int[] channelDeltas(int expected, int actual) {
    return new int[] {
      Math.abs((expected >>> 24) - (actual >>> 24)),
      Math.abs(((expected >>> 16) & 0xff) - ((actual >>> 16) & 0xff)),
      Math.abs(((expected >>> 8) & 0xff) - ((actual >>> 8) & 0xff)),
      Math.abs((expected & 0xff) - (actual & 0xff))
    };
  }

  private static boolean approvedScene(String sceneId) {
    return policy().boundaryScenes().stream().anyMatch(scene -> scene.id().equals(sceneId));
  }

  private static String approvedSceneId(String sceneId) {
    String token = requireToken(sceneId, "sceneId");
    if (!approvedScene(token)) throw new IllegalArgumentException("Unapproved boundary scene: " + token);
    return token;
  }

  private static String requireToken(String value, String name) {
    if (value == null || !value.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
      throw new IllegalArgumentException(name + " must be lowercase kebab-case");
    }
    return value;
  }

  private static void exactFields(JsonObject object, Set<String> expected, String path) {
    if (!object.keySet().equals(expected)) {
      throw invalidManifest(path + " fields differ; expected=" + expected + " actual=" + object.keySet());
    }
  }

  private static JsonElement required(JsonObject object, String name) {
    JsonElement value = object.get(name);
    if (value == null || value.isJsonNull()) throw invalidManifest("missing " + name);
    return value;
  }

  private static JsonObject requiredObject(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonObject()) throw invalidManifest(name + " must be an object");
    return value.getAsJsonObject();
  }

  private static String requiredString(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
      throw invalidManifest(name + " must be a string");
    }
    String result = value.getAsString();
    if (result.isBlank()) throw invalidManifest(name + " must not be blank");
    return result;
  }

  private static boolean requiredBoolean(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
      throw invalidManifest(name + " must be a boolean");
    }
    return value.getAsBoolean();
  }

  private static int requiredPositiveInt(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
      throw invalidManifest(name + " must be an integer");
    }
    try {
      int result = new BigDecimal(value.getAsString()).intValueExact();
      if (result <= 0) throw invalidManifest(name + " must be positive");
      return result;
    } catch (ArithmeticException failure) {
      throw invalidManifest(name + " must be an integer");
    }
  }

  private static float requiredPositiveFloat(JsonObject object, String name) {
    JsonElement value = required(object, name);
    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
      throw invalidManifest(name + " must be a number");
    }
    float result = value.getAsFloat();
    if (!Float.isFinite(result) || result <= 0) {
      throw invalidManifest(name + " must be positive and finite");
    }
    return result;
  }

  private static IllegalArgumentException invalidManifest(String detail) {
    return new IllegalArgumentException("Invalid local image reference manifest: " + detail);
  }

  private static Evaluation unvalidated(Status status, String detail) {
    return new Evaluation(status, false, detail);
  }

  public record Policy(
      String policyVersion,
      String referenceVersion,
      Tolerance tolerance,
      List<BoundaryScene> boundaryScenes) {
    public Policy {
      boundaryScenes = List.copyOf(boundaryScenes);
    }
  }

  /** Exact comparison outside antialias fringe; bounded channel error inside the edge fringe. */
  public record Tolerance(
      int maxRgbChannelDelta,
      int maxAlphaDelta,
      int maxAntialiasFringeChannelDelta,
      double maxDifferingPixelRatio,
      int antialiasFringeRadiusPixels) {
    public Tolerance {
      if (maxRgbChannelDelta < 0
          || maxAlphaDelta < 0
          || maxAntialiasFringeChannelDelta < 0
          || maxDifferingPixelRatio < 0
          || maxDifferingPixelRatio > 1
          || antialiasFringeRadiusPixels < 0) {
        throw new IllegalArgumentException("Invalid local image tolerance");
      }
    }
  }

  public record BoundaryScene(String id, List<String> requiredEvidence) {
    public BoundaryScene {
      id = requireToken(id, "boundaryScene.id");
      requiredEvidence = List.copyOf(requiredEvidence);
    }
  }

  public record EnvironmentFingerprint(
      String jvmVendor,
      String jvmVersion,
      String osName,
      String osVersion,
      String osArchitecture,
      String glVendor,
      String glRenderer,
      String glDriverVersion,
      String glVersion,
      String backend,
      boolean antialiasing,
      int width,
      int height,
      float pixelRatio) {
    public EnvironmentFingerprint {
      if (width <= 0 || height <= 0 || !Float.isFinite(pixelRatio) || pixelRatio <= 0) {
        throw new IllegalArgumentException("Invalid image environment geometry");
      }
      backend = requireToken(backend, "backend");
      Objects.requireNonNull(jvmVendor);
      Objects.requireNonNull(jvmVersion);
      Objects.requireNonNull(osName);
      Objects.requireNonNull(osVersion);
      Objects.requireNonNull(osArchitecture);
      Objects.requireNonNull(glVendor);
      Objects.requireNonNull(glRenderer);
      Objects.requireNonNull(glDriverVersion);
      Objects.requireNonNull(glVersion);
    }

    public String stableId() {
      try {
        byte[] hash = MessageDigest.getInstance("SHA-256")
            .digest(GSON.toJson(this).getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
      } catch (NoSuchAlgorithmException exception) {
        throw new IllegalStateException("Required SHA-256 digest is unavailable", exception);
      }
    }
  }

  public record Reference(
      String policyVersion,
      String referenceVersion,
      String sceneId,
      EnvironmentFingerprint environment) {
    public Reference {
      Objects.requireNonNull(policyVersion, "policyVersion");
      Objects.requireNonNull(referenceVersion, "referenceVersion");
      sceneId = requireToken(sceneId, "sceneId");
      Objects.requireNonNull(environment, "environment");
    }
  }

  public record ReferenceFiles(Path image, Path manifest) {}

  public record Request(
      boolean structuralEvidencePassed,
      boolean optedIn,
      EnvironmentFingerprint currentEnvironment,
      Reference reference) {
    public Request {
      Objects.requireNonNull(currentEnvironment, "currentEnvironment");
    }
  }

  public record Evaluation(Status status, boolean comparisonAllowed, String detail) {
    public Evaluation {
      if (status != Status.READY_TO_COMPARE && comparisonAllowed) {
        throw new IllegalArgumentException("Unvalidated image evidence cannot be compared");
      }
    }
  }

  public record ComparisonResult(
      boolean passed,
      int differingPixels,
      int overLimitPixels,
      double differingPixelRatio,
      int maximumChannelDelta,
      boolean dimensionMismatch,
      BufferedImage amplifiedDiff,
      BufferedImage edgeMask) {}

  private record ComparisonSummary(
      boolean passed,
      int differingPixels,
      int overLimitPixels,
      double differingPixelRatio,
      int maximumChannelDelta,
      boolean dimensionMismatch) {}

  public record ComparisonOutcome(
      Evaluation evaluation, ComparisonResult comparison, Path mismatchArtifacts) {}

  public enum Status {
    FAILED_STRUCTURAL,
    UNVALIDATED_OPT_OUT,
    UNVALIDATED_REFERENCE_MISSING,
    UNVALIDATED_ENVIRONMENT_MISMATCH,
    UNVALIDATED_REFERENCE_VERSION,
    UNVALIDATED_REFERENCE_MANIFEST,
    UNVALIDATED_SCENE_MISMATCH,
    READY_TO_COMPARE,
    PASSED,
    FAILED_IMAGE_COMPARISON
  }
}
