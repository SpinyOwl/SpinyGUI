package com.spinyowl.spinygui.benchmark.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.rendering.LocalImageComparisonPolicy.EnvironmentFingerprint;
import com.spinyowl.spinygui.benchmark.rendering.LocalImageComparisonPolicy.Reference;
import com.spinyowl.spinygui.benchmark.rendering.LocalImageComparisonPolicy.Request;
import com.spinyowl.spinygui.benchmark.rendering.LocalImageComparisonPolicy.Status;
import java.nio.file.Path;
import java.nio.file.Files;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalImageComparisonPolicyTest {
  @TempDir Path temporaryDirectory;
  @Test
  void allWrongNonBlackFrameCannotBypassStructuralEvidence() {
    var evaluation =
        LocalImageComparisonPolicy.evaluate(
            new Request(false, true, environment("Renderer"), reference(environment("Renderer"))));

    assertEquals(Status.FAILED_STRUCTURAL, evaluation.status());
    assertFalse(evaluation.comparisonAllowed());
  }

  @Test
  void missingOrIncompatibleReferenceIsUnvalidatedAndNeverCompared() {
    var missing =
        LocalImageComparisonPolicy.evaluate(
            new Request(true, true, environment("Renderer"), null));
    var mismatch =
        LocalImageComparisonPolicy.evaluate(
            new Request(
                true,
                true,
                environment("Renderer"),
                reference(environment("Different renderer"))));
    var optedOut =
        LocalImageComparisonPolicy.evaluate(
            new Request(true, false, environment("Renderer"), reference(environment("Renderer"))));

    assertEquals(Status.UNVALIDATED_REFERENCE_MISSING, missing.status());
    assertEquals(Status.UNVALIDATED_ENVIRONMENT_MISMATCH, mismatch.status());
    assertEquals(Status.UNVALIDATED_OPT_OUT, optedOut.status());
    assertFalse(missing.comparisonAllowed());
    assertFalse(mismatch.comparisonAllowed());
    assertFalse(optedOut.comparisonAllowed());
  }

  @Test
  void exactOptInEnvironmentAndVersionAreRequiredBeforeComparison() {
    EnvironmentFingerprint environment = environment("Renderer");
    var ready =
        LocalImageComparisonPolicy.evaluate(
            new Request(true, true, environment, reference(environment)));
    var stale =
        LocalImageComparisonPolicy.evaluate(
            new Request(
                true,
                true,
                environment,
                new Reference("old-policy", "reference-v0", "fallback-overhang", environment)));

    assertEquals(Status.READY_TO_COMPARE, ready.status());
    assertTrue(ready.comparisonAllowed());
    assertEquals(Status.UNVALIDATED_REFERENCE_VERSION, stale.status());
    assertFalse(stale.comparisonAllowed());
  }

  @Test
  void configuredEvaluationRequiresTheExplicitSystemProperty() {
    String previous = System.getProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
    try {
      System.clearProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
      assertEquals(
          Status.UNVALIDATED_OPT_OUT,
          LocalImageComparisonPolicy.evaluateConfigured(
                  true, environment("Renderer"), reference(environment("Renderer")))
              .status());
      System.setProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY, "true");
      assertEquals(
          Status.READY_TO_COMPARE,
          LocalImageComparisonPolicy.evaluateConfigured(
                  true, environment("Renderer"), reference(environment("Renderer")))
              .status());
    } finally {
      if (previous == null) {
        System.clearProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
      } else {
        System.setProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY, previous);
      }
    }
  }

  @Test
  void policyFreezesBoundaryCoverageToleranceNamingAndArtifactRetention() {
    var policy = LocalImageComparisonPolicy.policy();
    Set<String> evidence =
        policy.boundaryScenes().stream()
            .flatMap(scene -> scene.requiredEvidence().stream())
            .collect(Collectors.toSet());

    assertEquals(
        Set.of("fallback", "overhang", "clipping", "selection", "caret", "transform"),
        evidence);
    assertEquals(2, policy.tolerance().maxRgbChannelDelta());
    assertEquals(2, policy.tolerance().maxAlphaDelta());
    assertEquals(6, policy.tolerance().maxAntialiasFringeChannelDelta());
    assertEquals(0.005, policy.tolerance().maxDifferingPixelRatio());
    assertEquals(1, policy.tolerance().antialiasFringeRadiusPixels());
    assertTrue(
        LocalImageComparisonPolicy.referenceName("fallback-overhang", environment("Renderer"))
            .startsWith("reference-v1--fallback-overhang--"));
    assertEquals(64, environment("Renderer").stableId().length());
    assertEquals(
        Path.of("artifacts", "mismatches", "local-text-image-policy-v1", "fallback-overhang"),
        LocalImageComparisonPolicy.mismatchDirectory(
            Path.of("artifacts"), "fallback-overhang"));
  }

  @Test
  void compatibleReferenceIsDecodedComparedAndPassesEndToEnd() throws Exception {
    EnvironmentFingerprint environment = environment("Renderer");
    Reference reference = reference(environment);
    BufferedImage image = image(Color.BLACK, Color.WHITE);
    Path references = temporaryDirectory.resolve("references");
    Path artifacts = temporaryDirectory.resolve("artifacts");
    Path actual = temporaryDirectory.resolve("actual.png");
    LocalImageComparisonPolicy.writeReference(references, reference, image);
    javax.imageio.ImageIO.write(image, "png", actual.toFile());

    String previous = System.getProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
    try {
      System.setProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY, "true");
      var outcome =
          LocalImageComparisonPolicy.compareConfigured(
              true,
              "fallback-overhang",
              environment,
              actual,
              references,
              artifacts);

      assertEquals(Status.PASSED, outcome.evaluation().status());
      assertTrue(outcome.comparison().passed());
      assertEquals(null, outcome.mismatchArtifacts());
    } finally {
      restoreProperty(previous);
    }
  }

  @Test
  void mismatchRetainsRequiredArtifactsAndSummary() throws Exception {
    EnvironmentFingerprint environment = environment("Renderer");
    Path references = temporaryDirectory.resolve("references");
    Path artifacts = temporaryDirectory.resolve("artifacts");
    Path actual = temporaryDirectory.resolve("actual.png");
    LocalImageComparisonPolicy.writeReference(
        references, reference(environment), image(Color.BLACK, Color.WHITE));
    javax.imageio.ImageIO.write(image(Color.BLACK, Color.RED), "png", actual.toFile());

    String previous = System.getProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
    try {
      System.setProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY, "true");
      var outcome =
          LocalImageComparisonPolicy.compareConfigured(
              true,
              "fallback-overhang",
              environment,
              actual,
              references,
              artifacts);

      assertEquals(Status.FAILED_IMAGE_COMPARISON, outcome.evaluation().status());
      assertFalse(outcome.comparison().passed());
      for (String file :
          List.of(
              "actual.png",
              "expected.png",
              "amplified-diff.png",
              "edge-mask.png",
              "environment.json",
              "summary.json")) {
        assertTrue(Files.isRegularFile(outcome.mismatchArtifacts().resolve(file)), file);
      }
    } finally {
      restoreProperty(previous);
    }
  }

  @Test
  void incompatibleManifestIsUnvalidatedWithoutDecodingActualPixels() throws Exception {
    EnvironmentFingerprint referenceEnvironment = environment("Different renderer");
    Path references = temporaryDirectory.resolve("references");
    LocalImageComparisonPolicy.writeReference(
        references, reference(referenceEnvironment), image(Color.BLACK, Color.WHITE));
    Path expectedFiles =
        LocalImageComparisonPolicy.referenceFiles(
                references, "fallback-overhang", environment("Renderer"))
            .manifest();
    Files.createDirectories(expectedFiles.getParent());
    Files.copy(
        LocalImageComparisonPolicy.referenceFiles(
                references, "fallback-overhang", referenceEnvironment)
            .manifest(),
        expectedFiles);

    String previous = System.getProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
    try {
      System.setProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY, "true");
      var outcome =
          LocalImageComparisonPolicy.compareConfigured(
              true,
              "fallback-overhang",
              environment("Renderer"),
              temporaryDirectory.resolve("does-not-exist.png"),
              references,
              temporaryDirectory.resolve("artifacts"));

      assertEquals(Status.UNVALIDATED_ENVIRONMENT_MISMATCH, outcome.evaluation().status());
      assertEquals(null, outcome.comparison());
    } finally {
      restoreProperty(previous);
    }
  }

  @Test
  void referenceManifestUsesClosedExactTypedSchema() throws Exception {
    Path manifest = temporaryDirectory.resolve("reference.json");
    String valid = manifestJson();
    Files.writeString(manifest, valid);
    assertEquals("fallback-overhang", LocalImageComparisonPolicy.readReference(manifest).sceneId());

    for (String invalid :
        List.of(
            valid.replaceFirst("\\{", "{\"unknown\":1,"),
            valid.replace("\"sceneId\":\"fallback-overhang\",", ""),
            valid.replace("\"sceneId\":\"fallback-overhang\"", "\"sceneId\":1"),
            valid.replace("\"sceneId\":\"fallback-overhang\"", "\"sceneId\":\"unknown\""),
            valid.replace("\"width\":1280", "\"width\":\"1280\""),
            valid.replace("\"pixelRatio\":1.0", "\"pixelRatio\":1e400"),
            valid.replace("\"antialiasing\":true", "\"antialiasing\":\"true\""),
            valid.replace("\"referenceVersion\":\"reference-v1\"", "\"referenceVersion\":\"old\""))) {
      Files.writeString(manifest, invalid);
      assertThrows(IllegalArgumentException.class, () -> LocalImageComparisonPolicy.readReference(manifest));
    }
  }

  @Test
  void malformedReferenceIsUnvalidatedBeforeActualImageDecode() throws Exception {
    EnvironmentFingerprint environment = environment("Renderer");
    var files =
        LocalImageComparisonPolicy.referenceFiles(
            temporaryDirectory.resolve("references"), "fallback-overhang", environment);
    Files.createDirectories(files.manifest().getParent());
    Files.writeString(files.manifest(), manifestJson().replace("\"width\":1280", "\"width\":0"));
    String previous = System.getProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
    try {
      System.setProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY, "true");
      var result =
          LocalImageComparisonPolicy.compareConfigured(
              true,
              "fallback-overhang",
              environment,
              temporaryDirectory.resolve("missing-actual.png"),
              temporaryDirectory.resolve("references"),
              temporaryDirectory.resolve("artifacts"));
      assertEquals(Status.UNVALIDATED_REFERENCE_MANIFEST, result.evaluation().status());
      assertEquals(null, result.comparison());
    } finally {
      restoreProperty(previous);
    }
  }

  @Test
  void toleranceHonorsEdgeRgbAndAlphaLimitsAndOnePixelExpansion() {
    var policyTolerance = LocalImageComparisonPolicy.policy().tolerance();
    var tolerance =
        new LocalImageComparisonPolicy.Tolerance(
            policyTolerance.maxRgbChannelDelta(),
            policyTolerance.maxAlphaDelta(),
            policyTolerance.maxAntialiasFringeChannelDelta(),
            1,
            policyTolerance.antialiasFringeRadiusPixels());
    BufferedImage expected = solid(5, 5, new Color(0, 0, 0, 100));
    expected.setRGB(2, 2, new Color(100, 100, 100, 100).getRGB());
    BufferedImage atLimit = copy(expected);
    atLimit.setRGB(3, 2, new Color(6, 6, 6, 106).getRGB());
    assertTrue(LocalImageComparisonPolicy.compare(atLimit, expected, tolerance).passed());

    BufferedImage overAlpha = copy(expected);
    overAlpha.setRGB(3, 2, new Color(6, 6, 6, 107).getRGB());
    assertFalse(LocalImageComparisonPolicy.compare(overAlpha, expected, tolerance).passed());

    BufferedImage overRgb = copy(expected);
    overRgb.setRGB(3, 2, new Color(7, 6, 6, 106).getRGB());
    assertFalse(LocalImageComparisonPolicy.compare(overRgb, expected, tolerance).passed());

    BufferedImage outside = copy(expected);
    outside.setRGB(0, 0, new Color(2, 0, 0, 102).getRGB());
    assertTrue(LocalImageComparisonPolicy.compare(outside, expected, tolerance).passed());
    outside.setRGB(0, 0, new Color(3, 0, 0, 102).getRGB());
    assertFalse(LocalImageComparisonPolicy.compare(outside, expected, tolerance).passed());
  }

  @Test
  void differingPixelRatioAcceptsExactlyPointFivePercentAndRejectsAbove() {
    var tolerance = LocalImageComparisonPolicy.policy().tolerance();
    BufferedImage expected = solid(20, 10, Color.BLACK);
    BufferedImage exact = copy(expected);
    exact.setRGB(0, 0, new Color(1, 0, 0).getRGB());
    assertEquals(0.005, LocalImageComparisonPolicy.compare(exact, expected, tolerance).differingPixelRatio());
    assertTrue(LocalImageComparisonPolicy.compare(exact, expected, tolerance).passed());

    BufferedImage above = copy(exact);
    above.setRGB(1, 0, new Color(1, 0, 0).getRGB());
    assertFalse(LocalImageComparisonPolicy.compare(above, expected, tolerance).passed());
  }

  private static String manifestJson() {
    return """
        {"policyVersion":"local-text-image-policy-v1","referenceVersion":"reference-v1","sceneId":"fallback-overhang","environment":{"jvmVendor":"Vendor","jvmVersion":"25","osName":"OS","osVersion":"1","osArchitecture":"x64","glVendor":"GL vendor","glRenderer":"Renderer","glDriverVersion":"driver","glVersion":"4.6","backend":"nanovg-gl3","antialiasing":true,"width":1280,"height":720,"pixelRatio":1.0}}
        """;
  }

  private static BufferedImage solid(int width, int height, Color color) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    var graphics = image.createGraphics();
    graphics.setColor(color);
    graphics.fillRect(0, 0, width, height);
    graphics.dispose();
    return image;
  }

  private static BufferedImage copy(BufferedImage source) {
    BufferedImage copy =
        new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
    var graphics = copy.createGraphics();
    graphics.drawImage(source, 0, 0, null);
    graphics.dispose();
    return copy;
  }

  private static BufferedImage image(Color background, Color foreground) {
    BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    var graphics = image.createGraphics();
    try {
      graphics.setColor(background);
      graphics.fillRect(0, 0, 20, 20);
      graphics.setColor(foreground);
      graphics.fillRect(5, 5, 10, 10);
    } finally {
      graphics.dispose();
    }
    return image;
  }

  private static void restoreProperty(String previous) {
    if (previous == null) {
      System.clearProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY);
    } else {
      System.setProperty(LocalImageComparisonPolicy.OPT_IN_PROPERTY, previous);
    }
  }

  private static Reference reference(EnvironmentFingerprint environment) {
    return new Reference(
        LocalImageComparisonPolicy.POLICY_VERSION,
        LocalImageComparisonPolicy.REFERENCE_VERSION,
        "fallback-overhang",
        environment);
  }

  private static EnvironmentFingerprint environment(String renderer) {
    ComparabilityMetadata.Environment environment =
        new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.RENDERING,
            "Vendor",
            "25",
            "OS",
            "1",
            "x64",
            "CPU",
            "GL vendor",
            renderer,
            "driver",
            "4.6");
    return LocalImageComparisonPolicy.environment(
        environment, "nanovg-gl3", true, 1280, 720, 1f);
  }
}
