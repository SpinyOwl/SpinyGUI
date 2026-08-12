package com.spinyowl.spinygui.benchmark.diagnostic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonPrimitive;
import com.spinyowl.spinygui.benchmark.diagnostic.CounterDiagnosticArtifact.Entry;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Category;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Dimension;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DiagnosticWorkloadSpecificationsTest {
  private static final ComparabilityMetadata.Environment CPU_ENVIRONMENT =
      new ComparabilityMetadata.Environment(
          ComparabilityMetadata.Scope.CPU,
          "Vendor", "25", "OS", "1", "x64", "CPU", null, null, null, null);
  private static final ComparabilityMetadata.Environment RENDERING_ENVIRONMENT =
      new ComparabilityMetadata.Environment(
          ComparabilityMetadata.Scope.RENDERING,
          "Vendor", "25", "OS", "1", "x64", "CPU",
          "GL vendor", "renderer", "driver", "4.6");
  private static final ComparabilityMetadata.Implementation IMPLEMENTATION =
      new ComparabilityMetadata.Implementation("impl", "build", "commit");

  @Test
  void declaredInputsAreSourceBoundAndRendererFontsCoverEveryReachableFace() {
    for (var scenario : DiagnosticWorkloadSpecifications.CPU_SCENARIOS) {
      var dimensions = scenario.identity().dimensions();
      assertEquals(scenario.text().codePointCount(0, scenario.text().length()),
          Integer.parseInt(dimensions.get(Dimension.SOURCE_CODE_POINT_COUNT)));
      assertEquals(decimal(scenario.wrapWidthPx()),
          dimensions.get(Dimension.WRAP_WIDTH_PX));
      assertEquals(scenario.wordWrap() ? "word-wrap" : "character-wrap",
          dimensions.get(Dimension.WRAPPING_POLICY));
      assertTrue(scenario.inputManifests().content().canonicalSerialization().contains(scenario.text()));
      assertFalse(scenario.inputManifests().shape().canonicalSerialization().contains(scenario.text()));
      assertTrue(
          scenario
              .inputManifests()
              .fonts()
              .canonicalSerialization()
              .contains(
                  "configuration-font-weight="
                      + scenario.fonts().getFirst().weight().name().length()
                      + ":"
                      + scenario.fonts().getFirst().weight().name()));
    }

    List<Font> completeLayoutFonts =
        List.of(
            Font.ROBOTO_REGULAR,
            Font.ROBOTO_LIGHT,
            Font.ROBOTO_BOLD,
            Font.NOTO_SANS_CJK_SC_REGULAR);
    for (var scenario : DiagnosticWorkloadSpecifications.RENDERER_SCENARIOS) {
      var dimensions = scenario.identity().dimensions();
      assertEquals(completeLayoutFonts, scenario.layoutFonts());
      assertEquals(Integer.toString(scenario.itemCount()), dimensions.get(Dimension.TEXT_NODE_COUNT));
      assertEquals(decimal(scenario.container().xPx()),
          dimensions.get(Dimension.CONTAINER_POSITION_X_PX));
      assertEquals(decimal(scenario.container().yPx()),
          dimensions.get(Dimension.CONTAINER_POSITION_Y_PX));
      assertEquals(decimal(scenario.container().widthPx()),
          dimensions.get(Dimension.CONTAINER_WIDTH_PX));
      assertEquals(decimal(scenario.container().heightPx()),
          dimensions.get(Dimension.CONTAINER_HEIGHT_PX));
      assertEquals(scenario.visibility(), dimensions.get(Dimension.VISIBILITY));
      assertEquals(scenario.submissionState(), dimensions.get(Dimension.SUBMISSION_STATE));
      String contentManifest = scenario.inputManifests().content().canonicalSerialization();
      for (int index = 0; index < scenario.itemCount(); index++) {
        assertTrue(contentManifest.contains(scenario.sourceContent(index)), scenario.name());
      }
      String fontManifest = scenario.inputManifests().fonts().canonicalSerialization();
      for (Font font : completeLayoutFonts) {
        assertTrue(fontManifest.contains(font.path()), font.path());
      }
      assertTrue(fontManifest.contains("resource-sha256"));
      assertFalse(scenario.inputManifests().shape().canonicalSerialization()
          .contains(scenario.sourceContent(0)));
    }
  }

  @Test
  void manifestsContainOnlyCategoryConsumedControlStateAndKeepCpuTypographyIsolated() {
    var normal = scenario(Category.NORMAL_TEXT, "normal-visible-changed");
    var normalWithUnusedControlState =
        copyRenderer(
            normal,
            normal.controlWidthPx() + 1,
            normal.controlHeightPx() + 1,
            3,
            9,
            9,
            7,
            11,
            normal.container());
    assertEquals(normal.identity(), normalWithUnusedControlState.identity());
    assertEquals(normal.inputManifests(), normalWithUnusedControlState.inputManifests());
    assertEquals(
        DiagnosticWorkloadSpecifications.comparability(
                normal, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints(),
        DiagnosticWorkloadSpecifications.comparability(
                normalWithUnusedControlState, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints());
    String normalShape = normal.inputManifests().shape().canonicalSerialization();
    for (String field :
        Set.of(
            "caret-index-utf16",
            "control-height-px",
            "control-width-px",
            "scroll-x-px",
            "scroll-y-px",
            "selection-end-utf16",
            "selection-start-utf16",
            "wrap-width-px",
            "deferred-suffix-code-point-count",
            "line-start-kerning-transition-count")) {
      assertFalse(normalShape.contains(field), field);
    }

    var input = scenario(Category.INPUT, "input-visible-changed");
    var changedInput =
        copyRenderer(
            input,
            input.controlWidthPx() + 1,
            input.controlHeightPx() + 1,
            3,
            13,
            13,
            input.scrollXPx() + 1,
            input.scrollYPx() + 1,
            input.container());
    assertNotEquals(input.identity(), changedInput.identity());
    assertNotEquals(input.inputManifests().shape(), changedInput.inputManifests().shape());
    assertNotEquals(
        DiagnosticWorkloadSpecifications.comparability(input, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints(),
        DiagnosticWorkloadSpecifications.comparability(
                changedInput, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints());
    String inputShape = input.inputManifests().shape().canonicalSerialization();
    assertTrue(inputShape.contains("caret-index-utf16"));
    assertTrue(inputShape.contains("control-width-px"));
    assertTrue(inputShape.contains("scroll-x-px"));
    assertFalse(inputShape.contains("scroll-y-px"));
    assertFalse(inputShape.contains("wrap-width-px"));
    var inputWithUnusedVerticalScroll =
        copyRenderer(
            input,
            input.controlWidthPx(),
            input.controlHeightPx(),
            input.selectionStartUtf16(),
            input.selectionEndUtf16(),
            input.caretIndexUtf16(),
            input.scrollXPx(),
            input.scrollYPx() + 1,
            input.container());
    assertEquals(input.inputManifests(), inputWithUnusedVerticalScroll.inputManifests());
    assertEquals(
        DiagnosticWorkloadSpecifications.comparability(input, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints(),
        DiagnosticWorkloadSpecifications.comparability(
                inputWithUnusedVerticalScroll, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints());

    var textarea = scenario(Category.TEXTAREA, "textarea-visible-changed");
    var textareaContainer =
        new DiagnosticWorkloadSpecifications.Rect(
            textarea.container().xPx(),
            textarea.container().yPx(),
            textarea.container().widthPx() + 1,
            textarea.container().heightPx());
    var changedTextarea =
        copyRenderer(
            textarea,
            textarea.controlWidthPx() + 1,
            textarea.controlHeightPx() + 1,
            3,
            15,
            15,
            textarea.scrollXPx() + 1,
            textarea.scrollYPx() + 1,
            textareaContainer);
    assertNotEquals(textarea.identity(), changedTextarea.identity());
    assertNotEquals(textarea.inputManifests().shape(), changedTextarea.inputManifests().shape());
    assertNotEquals(
        DiagnosticWorkloadSpecifications.comparability(
                textarea, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints(),
        DiagnosticWorkloadSpecifications.comparability(
                changedTextarea, RENDERING_ENVIRONMENT, IMPLEMENTATION)
            .fingerprints());
    String textareaShape = textarea.inputManifests().shape().canonicalSerialization();
    for (String field :
        Set.of(
            "caret-index-utf16",
            "control-width-px",
            "scroll-x-px",
            "scroll-y-px",
            "selection-start-utf16",
            "wrap-width-px",
            "deferred-suffix-code-point-count",
            "line-start-kerning-transition-count")) {
      assertTrue(textareaShape.contains(field), field);
    }

    var cpu = DiagnosticWorkloadSpecifications.CPU_SCENARIOS.getFirst();
    var changedCpuFont =
        new DiagnosticWorkloadSpecifications.CpuScenario(
            cpu.name(),
            cpu.workloadContent(),
            cpu.text(),
            List.of(Font.ROBOTO_BOLD, Font.NOTO_SANS_CJK_SC_REGULAR),
            cpu.fontSizePx(),
            cpu.lineHeight(),
            cpu.measurementOffsetXPx(),
            cpu.wrapWidthPx(),
            cpu.wordWrap(),
            cpu.expectedShape());
    assertNotEquals(cpu.inputManifests().fonts(), changedCpuFont.inputManifests().fonts());
    assertTrue(
        cpu.inputManifests().fonts().canonicalSerialization().contains("configuration-font-weight=7:regular"));
    assertTrue(
        changedCpuFont
            .inputManifests()
            .fonts()
            .canonicalSerialization()
            .contains("configuration-font-weight=4:bold"));
    assertEquals(
        normal.inputManifests().fonts(),
        normalWithUnusedControlState.inputManifests().fonts());
  }

  @Test
  void corpusDriftFailsClosedAndEachDeclaredCpuShapeOutputIsExecutionChecked() {
    var cpu = DiagnosticWorkloadSpecifications.CPU_SCENARIOS.getLast();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new DiagnosticWorkloadSpecifications.CpuScenario(
                cpu.name(), cpu.workloadContent(), cpu.text() + "x", cpu.fonts(), cpu.fontSizePx(),
                cpu.lineHeight(), cpu.measurementOffsetXPx(), cpu.wrapWidthPx(), cpu.wordWrap(),
                cpu.expectedShape()));

    var renderer = DiagnosticWorkloadSpecifications.RENDERER_SCENARIOS.getFirst();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new DiagnosticWorkloadSpecifications.RendererScenario(
                renderer.name(), renderer.category(), renderer.workloadContent(), List.of("drift"),
                renderer.itemCount(), renderer.container(), renderer.controlWidthPx(),
                renderer.controlHeightPx(), renderer.selectionStartUtf16(), renderer.selectionEndUtf16(),
                renderer.caretIndexUtf16(), renderer.scrollXPx(), renderer.scrollYPx(),
                renderer.submissionState(), renderer.expectedShape()));

    var shape = cpu.expectedShape();
    List<DiagnosticWorkloadSpecifications.ExpectedShape> outputDrifts =
        List.of(
            new DiagnosticWorkloadSpecifications.ExpectedShape(
                shape.sourceCodePointCount(), shape.sourceLineCount(), shape.visualLineCount() + 1,
                shape.paragraphCount(), shape.fallbackTransitionCount(),
                shape.deferredSuffixCodePointCount(), shape.lineStartKerningTransitionCount()),
            new DiagnosticWorkloadSpecifications.ExpectedShape(
                shape.sourceCodePointCount(), shape.sourceLineCount(), shape.visualLineCount(),
                shape.paragraphCount(), shape.fallbackTransitionCount() + 1,
                shape.deferredSuffixCodePointCount(), shape.lineStartKerningTransitionCount()),
            new DiagnosticWorkloadSpecifications.ExpectedShape(
                shape.sourceCodePointCount(), shape.sourceLineCount(), shape.visualLineCount(),
                shape.paragraphCount(), shape.fallbackTransitionCount(),
                shape.deferredSuffixCodePointCount() + 1, shape.lineStartKerningTransitionCount()),
            new DiagnosticWorkloadSpecifications.ExpectedShape(
                shape.sourceCodePointCount(), shape.sourceLineCount(), shape.visualLineCount(),
                shape.paragraphCount(), shape.fallbackTransitionCount(),
                shape.deferredSuffixCodePointCount(), shape.lineStartKerningTransitionCount() + 1));
    for (var drift : outputDrifts) {
      var changed = copyCpu(cpu, drift);
      assertThrows(
          IllegalStateException.class,
          () -> CounterDiagnosticsMain.runCpuScenario(changed, CPU_ENVIRONMENT, IMPLEMENTATION));
    }
  }

  @Test
  void matrixIdentifiesEveryScaleControlVisibilitySelectionAndSubmissionVariant() {
    var scenarios = new ArrayList<DiagnosticWorkloadSpecifications.Scenario>();
    scenarios.addAll(DiagnosticWorkloadSpecifications.CPU_SCENARIOS);
    scenarios.addAll(DiagnosticWorkloadSpecifications.RENDERER_SCENARIOS);

    Set<String> semanticIds = new HashSet<>();
    Set<String> requiredFingerprints = new HashSet<>();
    Set<String> series = new HashSet<>();
    Set<Category> categories = new HashSet<>();
    Set<String> visibility = new HashSet<>();
    Set<String> submission = new HashSet<>();
    Set<String> wrapWidths = new HashSet<>();
    Set<String> offsets = new HashSet<>();
    Set<Integer> selectionSpans = new HashSet<>();

    for (var scenario : scenarios) {
      var identity = scenario.identity();
      var environment =
          scenario instanceof DiagnosticWorkloadSpecifications.CpuScenario
              ? CPU_ENVIRONMENT
              : RENDERING_ENVIRONMENT;
      var comparability =
          DiagnosticWorkloadSpecifications.comparability(scenario, environment, IMPLEMENTATION);
      assertEquals(
          com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.requiredDimensions(
              Category.valueOf(
                  identity
                      .dimensions()
                      .get(Dimension.CATEGORY)
                      .replace('-', '_')
                      .toUpperCase(Locale.ROOT)),
              identity.dimensions().get(Dimension.OPERATION)),
          identity.dimensions().keySet());
      assertTrue(semanticIds.add(identity.semanticId()), scenario.name());
      assertTrue(requiredFingerprints.add(comparability.fingerprints().required()), scenario.name());
      assertTrue(series.add(identity.seriesId()), scenario.name());
      assertEquals(identity.semanticId(), comparability.semanticId());
      assertEquals(
          ComparabilityMetadata.EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED,
          comparability.evidenceMode());
      assertEquals(scenario.executionSettings(), comparability.benchmarkSettings());
      categories.add(
          Category.valueOf(
              identity
                  .dimensions()
                  .get(Dimension.CATEGORY)
                  .replace('-', '_')
                  .toUpperCase(Locale.ROOT)));
      add(identity, Dimension.VISIBILITY, visibility);
      add(identity, Dimension.SUBMISSION_STATE, submission);
      add(identity, Dimension.WRAP_WIDTH_PX, wrapWidths);
      add(identity, Dimension.MEASUREMENT_OFFSET_X_PX, offsets);
      if (scenario instanceof DiagnosticWorkloadSpecifications.RendererScenario renderer
          && renderer.category() != Category.NORMAL_TEXT) {
        selectionSpans.add(renderer.selectionEndUtf16() - renderer.selectionStartUtf16());
      }
    }

    assertEquals(scenarios.size(), semanticIds.size());
    assertEquals(
        Set.of(Category.CPU, Category.NORMAL_TEXT, Category.INPUT, Category.TEXTAREA), categories);
    assertEquals(Set.of("visible", "offscreen"), visibility);
    assertEquals(Set.of("changed", "unchanged"), submission);
    assertTrue(wrapWidths.containsAll(Set.of("0", "24", "48", "100000")));
    assertTrue(offsets.containsAll(Set.of("0", "0.5")));
    assertTrue(selectionSpans.containsAll(Set.of(8, 12, 28, 35)));
    assertTrue(
        DiagnosticWorkloadSpecifications.RENDERER_SCENARIOS.stream()
            .anyMatch(
                scenario ->
                    scenario.category() == Category.TEXTAREA
                        && scenario.expectedShape().paragraphCount() == 4
                        && scenario.expectedShape().lineStartKerningTransitionCount() == 4
                        && scenario.expectedShape().fallbackTransitionCount() == 2));
  }

  @Test
  void scaledCpuFixturesExposeCurrentQuadraticGlyphMovementWithoutClocks() throws Exception {
    List<Entry> entries = CounterDiagnosticsMain.runCpuScenarios();
    Map<String, Entry> byName =
        entries.stream()
            .collect(java.util.stream.Collectors.toMap(Entry::scenarioName, entry -> entry));

    assertEquals(28, moved(byName.get("run-assembly-8")));
    assertEquals(120, moved(byName.get("run-assembly-16")));
    assertEquals(496, moved(byName.get("run-assembly-32")));
    assertEquals(4, moved(byName.get("run-assembly-16")) / moved(byName.get("run-assembly-8")));
    assertTrue(moved(byName.get("run-assembly-32")) > 4 * moved(byName.get("run-assembly-16")));
    assertTrue(
        observed(
                byName.get("multi-paragraph-fallback-deferred-suffix"),
                "observed-deferred-suffix-code-point-count")
            > 0);
    assertTrue(
        observed(
                byName.get("multi-paragraph-fallback-deferred-suffix"),
                "observed-fallback-transition-count")
            > 0);
    assertTrue(
        observed(
                byName.get("multi-paragraph-fallback-line-start"),
                "observed-line-start-kerning-transition-count")
            > 0);
    assertEquals(
        0,
        observed(byName.get("zero-width-boundary"), "observed-visual-line-count"));

    String runnerSource =
        Files.readString(
            repositoryRoot().resolve(
                "spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/diagnostic/CounterDiagnosticsMain.java"));
    assertFalse(runnerSource.contains("System.nanoTime"));
    assertFalse(runnerSource.contains("System.currentTimeMillis"));
    assertFalse(runnerSource.contains("Instant.now"));
  }

  @Test
  void outputOnlyCounterDriftKeepsIdentityFingerprintAndSeriesFixed() {
    var scenario =
        DiagnosticWorkloadSpecifications.RENDERER_SCENARIOS.stream()
            .filter(candidate -> candidate.category() == Category.NORMAL_TEXT)
            .filter(candidate -> "changed".equals(candidate.submissionState()))
            .findFirst()
            .orElseThrow();
    var identity = scenario.identity();
    var comparability =
        DiagnosticWorkloadSpecifications.comparability(
            scenario, RENDERING_ENVIRONMENT, IMPLEMENTATION);
    var prepared =
        CounterDiagnosticsMain.prepareScene(
            scenario,
            DiagnosticSession.enabled(
                java.util.stream.Stream.concat(
                        java.util.Arrays.stream(TextDiagnosticCounter.values()),
                        java.util.Arrays.stream(NvgDiagnosticCounter.values()))
                    .toList()));
    CounterDiagnosticsMain.validatePreparedScene(scenario, prepared);
    Map<String, JsonPrimitive> beforeOutputs =
        new java.util.LinkedHashMap<>(CounterDiagnosticsMain.preparedEvidence(prepared));
    Map<String, JsonPrimitive> afterOutputs = new java.util.LinkedHashMap<>(beforeOutputs);
    afterOutputs.compute(
        "observed-resolved-glyph-count", (key, value) -> number(value.getAsLong() + 3));
    afterOutputs.compute(
        "observed-resolved-run-count", (key, value) -> number(value.getAsLong() + 2));
    afterOutputs.compute(
        "observed-text-fragment-count", (key, value) -> number(value.getAsLong() + 1));
    Map<String, String> declared = declaredInputs(scenario);
    Entry before =
        new Entry(
            scenario.name(), scenario.evidenceScope(), identity.semanticId(), identity.seriesId(),
            declared, comparability.toJson(),
            Map.of(
                NvgDiagnosticCounter.SAVE_CALLS.id(), 1L,
                NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CULLED.id(), 0L),
            Set.of(), beforeOutputs);
    Entry after =
        new Entry(
            scenario.name(), scenario.evidenceScope(), identity.semanticId(), identity.seriesId(),
            declared, comparability.toJson(),
            Map.of(
                NvgDiagnosticCounter.SAVE_CALLS.id(), 2L,
                NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CULLED.id(), 1L),
            Set.of(), afterOutputs);

    assertNotEquals(before.counters(), after.counters());
    assertNotEquals(before.observedOutputs(), after.observedOutputs());
    assertNotEquals(
        before.observedOutputs().get("observed-resolved-glyph-count"),
        after.observedOutputs().get("observed-resolved-glyph-count"));
    assertNotEquals(
        before.observedOutputs().get("observed-resolved-run-count"),
        after.observedOutputs().get("observed-resolved-run-count"));
    assertNotEquals(
        before.observedOutputs().get("observed-text-fragment-count"),
        after.observedOutputs().get("observed-text-fragment-count"));
    assertNotEquals(
        before.counters().get(NvgDiagnosticCounter.SAVE_CALLS.id()),
        after.counters().get(NvgDiagnosticCounter.SAVE_CALLS.id()));
    assertNotEquals(
        before.counters().get(NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CULLED.id()),
        after.counters().get(NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CULLED.id()));
    assertEquals(before.semanticId(), after.semanticId());
    assertEquals(before.seriesId(), after.seriesId());
    assertEquals(
        ComparabilityMetadata.fromJson(before.comparability()).fingerprints(),
        ComparabilityMetadata.fromJson(after.comparability()).fingerprints());
  }

  @Test
  void preparedObjectDriftFailsClosedInsteadOfEchoingDeclarations() {
    var normal = scenario(Category.NORMAL_TEXT, "normal-visible-changed");
    var normalPrepared = prepare(normal);
    CounterDiagnosticsMain.validatePreparedScene(normal, normalPrepared);
    ((Text) normalPrepared.nodes().getFirst()).content("same setup changed after preparation");
    assertThrows(
        IllegalStateException.class,
        () -> CounterDiagnosticsMain.validatePreparedScene(normal, normalPrepared));

    var input = scenario(Category.INPUT, "input-visible-changed");
    var inputPrepared = prepare(input);
    CounterDiagnosticsMain.validatePreparedScene(input, inputPrepared);
    ((InputElement) inputPrepared.nodes().getFirst()).box().content().width(321);
    assertThrows(
        IllegalStateException.class,
        () -> CounterDiagnosticsMain.validatePreparedScene(input, inputPrepared));

    var textarea = scenario(Category.TEXTAREA, "textarea-visible-changed");
    var textareaPrepared = prepare(textarea);
    CounterDiagnosticsMain.validatePreparedScene(textarea, textareaPrepared);
    ((TextareaElement) textareaPrepared.nodes().getFirst()).select(0, 1);
    assertThrows(
        IllegalArgumentException.class,
        () -> CounterDiagnosticsMain.validatePreparedScene(textarea, textareaPrepared));

    var placement = prepare(normal);
    placement.container().box().content().x(1279);
    assertThrows(
        IllegalArgumentException.class,
        () -> CounterDiagnosticsMain.validatePreparedScene(normal, placement));
  }

  @Test
  void categorySpecificObservedSchemasRejectMissingExtraAndInapplicableFields() {
    var normal = scenario(Category.NORMAL_TEXT, "normal-visible-changed");
    var prepared = prepare(normal);
    var identity = normal.identity();
    var comparability =
        DiagnosticWorkloadSpecifications.comparability(
            normal, RENDERING_ENVIRONMENT, IMPLEMENTATION);
    Map<String, JsonPrimitive> valid =
        new java.util.LinkedHashMap<>(CounterDiagnosticsMain.preparedEvidence(prepared));
    Map<String, String> declared = declaredInputs(normal);

    Map<String, JsonPrimitive> missing = new java.util.LinkedHashMap<>(valid);
    missing.remove("observed-resolved-glyph-count");
    assertThrows(
        IllegalArgumentException.class,
        () -> entry(normal, identity, comparability, declared, missing));

    for (String inapplicable :
        Set.of(
            "observed-control-width-px",
            "observed-selection-start-utf16",
            "observed-wrap-width-px")) {
      Map<String, JsonPrimitive> extra = new java.util.LinkedHashMap<>(valid);
      extra.put(inapplicable, number(1));
      assertThrows(
          IllegalArgumentException.class,
          () -> entry(normal, identity, comparability, declared, extra),
          inapplicable);
    }

    Map<String, String> extraDeclared = new java.util.LinkedHashMap<>(declared);
    extraDeclared.put("selection-start-utf16", "0");
    assertThrows(
        IllegalArgumentException.class,
        () -> entry(normal, identity, comparability, extraDeclared, valid));

    Map<String, JsonPrimitive> wrongType = new java.util.LinkedHashMap<>(valid);
    wrongType.put("observed-resolved-glyph-count", new JsonPrimitive("152"));
    assertThrows(
        IllegalArgumentException.class,
        () -> entry(normal, identity, comparability, declared, wrongType));
  }

  @Test
  void artifactRejectsMergedVariantsAndTimedFactoriesUseTheDisabledSingleton() {
    var scenario = DiagnosticWorkloadSpecifications.CPU_SCENARIOS.getFirst();
    var metadata =
        BenchmarkRunMetadata.investigation(
            "run-1",
            BenchmarkRunMetadata.Artifact.COUNTER_DIAGNOSTICS,
            ComparabilityMetadata.EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED);
    var comparability =
        DiagnosticWorkloadSpecifications.comparability(scenario, CPU_ENVIRONMENT, IMPLEMENTATION);
    Entry recorded = CounterDiagnosticsMain.runCpuScenarios().getFirst();
    Entry entry =
        new Entry(
            scenario.name(), scenario.evidenceScope(), scenario.identity().semanticId(),
            scenario.identity().seriesId(), recorded.declaredInputs(), comparability.toJson(),
            recorded.counters(), Set.of(), recorded.observedOutputs());
    Map<String, String> mismatchedDeclared = new java.util.LinkedHashMap<>(entry.declaredInputs());
    mismatchedDeclared.put("wrap-width-px", "999");
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Entry(
                entry.scenarioName(), entry.evidenceScope(), entry.semanticId(), entry.seriesId(),
                mismatchedDeclared, entry.comparability(), entry.counters(),
                entry.saturatedCounterIds(), entry.observedOutputs()));
    Map<String, JsonPrimitive> mismatchedObserved =
        new java.util.LinkedHashMap<>(entry.observedOutputs());
    mismatchedObserved.compute(
        "observed-visual-line-count", (key, value) -> number(value.getAsLong() + 1));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Entry(
                entry.scenarioName(), entry.evidenceScope(), entry.semanticId(), entry.seriesId(),
                entry.declaredInputs(), entry.comparability(), entry.counters(),
                entry.saturatedCounterIds(), mismatchedObserved));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CounterDiagnosticArtifact(
                CounterDiagnosticArtifact.SCHEMA_VERSION,
                metadata.toJson(),
                "core-v1",
                "nvg-v1",
                List.of(entry, entry)));

    assertSame(
        DiagnosticSession.disabled(),
        com.spinyowl.spinygui.benchmark.cpu.CpuWorkloadSpecifications.TRIAL_SETUP
            .createFontService()
            .diagnostics());
    assertSame(
        DiagnosticSession.disabled(),
        com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications.CURRENT
            .createFontService()
            .diagnostics());
  }

  private static long moved(Entry entry) {
    return entry.counters().get(TextDiagnosticCounter.GLYPH_SLOTS_MOVED.id());
  }

  private static long observed(Entry entry, String key) {
    return entry.observedOutputs().get(key).getAsLong();
  }

  private static DiagnosticWorkloadSpecifications.CpuScenario copyCpu(
      DiagnosticWorkloadSpecifications.CpuScenario source,
      DiagnosticWorkloadSpecifications.ExpectedShape expectedShape) {
    return new DiagnosticWorkloadSpecifications.CpuScenario(
        source.name(), source.workloadContent(), source.text(), source.fonts(), source.fontSizePx(),
        source.lineHeight(), source.measurementOffsetXPx(), source.wrapWidthPx(), source.wordWrap(),
        expectedShape);
  }

  private static DiagnosticWorkloadSpecifications.RendererScenario scenario(
      Category category, String name) {
    return DiagnosticWorkloadSpecifications.RENDERER_SCENARIOS.stream()
        .filter(candidate -> candidate.category() == category && candidate.name().equals(name))
        .findFirst()
        .orElseThrow();
  }

  private static DiagnosticWorkloadSpecifications.RendererScenario copyRenderer(
      DiagnosticWorkloadSpecifications.RendererScenario source,
      float controlWidth,
      float controlHeight,
      int selectionStart,
      int selectionEnd,
      int caret,
      float scrollX,
      float scrollY,
      DiagnosticWorkloadSpecifications.Rect container) {
    return new DiagnosticWorkloadSpecifications.RendererScenario(
        source.name(),
        source.category(),
        source.workloadContent(),
        source.sourceContents(),
        source.itemCount(),
        container,
        controlWidth,
        controlHeight,
        selectionStart,
        selectionEnd,
        caret,
        scrollX,
        scrollY,
        source.submissionState(),
        source.expectedShape());
  }

  private static CounterDiagnosticsMain.PreparedScene prepare(
      DiagnosticWorkloadSpecifications.RendererScenario scenario) {
    var counters =
        java.util.stream.Stream.concat(
                java.util.Arrays.stream(TextDiagnosticCounter.values()),
                java.util.Arrays.stream(NvgDiagnosticCounter.values()))
            .map(counter -> (com.spinyowl.spinygui.core.diagnostic.DiagnosticCounter) counter)
            .toList();
    return CounterDiagnosticsMain.prepareScene(scenario, DiagnosticSession.enabled(counters));
  }

  private static Map<String, String> declaredInputs(
      DiagnosticWorkloadSpecifications.Scenario scenario) {
    Map<String, String> declared = new java.util.LinkedHashMap<>();
    scenario
        .identity()
        .dimensions()
        .forEach((dimension, value) -> declared.put(dimension.key(), value));
    return Map.copyOf(declared);
  }

  private static Entry entry(
      DiagnosticWorkloadSpecifications.RendererScenario scenario,
      com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity identity,
      ComparabilityMetadata comparability,
      Map<String, String> declared,
      Map<String, JsonPrimitive> observed) {
    return new Entry(
        scenario.name(),
        scenario.evidenceScope(),
        identity.semanticId(),
        identity.seriesId(),
        declared,
        comparability.toJson(),
        Map.of(),
        Set.of(),
        observed);
  }

  private static JsonPrimitive number(long value) {
    return new JsonPrimitive(value);
  }

  private static String decimal(float value) {
    if (Float.compare(value, 0) == 0) return "0";
    return new java.math.BigDecimal(Float.toString(value)).stripTrailingZeros().toPlainString();
  }

  private static void add(
      com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity identity,
      Dimension dimension,
      Set<String> values) {
    String value = identity.dimensions().get(dimension);
    if (value != null) values.add(value);
  }

  private static Path repositoryRoot() {
    Path current = Path.of("").toAbsolutePath();
    while (current != null && !Files.exists(current.resolve("settings.gradle.kts"))) {
      current = current.getParent();
    }
    if (current == null) throw new IllegalStateException("Repository root not found");
    return current;
  }
}
