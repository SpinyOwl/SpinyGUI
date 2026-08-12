package com.spinyowl.spinygui.benchmark.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.spinyowl.spinygui.benchmark.TextStyleSpecification;
import com.spinyowl.spinygui.benchmark.TextWorkloads;
import com.spinyowl.spinygui.benchmark.cpu.CpuWorkloadSpecifications;
import com.spinyowl.spinygui.benchmark.cpu.CpuWorkloadSpecifications.MeasurementSpec;
import com.spinyowl.spinygui.benchmark.cpu.CpuWorkloadSpecifications.OperationSpec;
import com.spinyowl.spinygui.benchmark.cpu.TextCalculationBenchmark;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Category;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Dimension;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications.ClearSpecification;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications.ContainerSpecification;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications.StructuralValidationSpecification;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications.SceneSpecification;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications.Specification;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.runner.Defaults;

class WorkloadIdentityTest {

  @Test
  void matchesCompleteCanonicalGoldenFixturesForEveryRequiredCategory() {
    JsonObject golden = golden();
    assertEquals(
        WorkloadIdentity.IDENTITY_SCHEMA_VERSION,
        golden.get("identitySchemaVersion").getAsInt());

    Set<String> categories = new HashSet<>();
    Set<String> visibility = new HashSet<>();
    Set<String> submissionStates = new HashSet<>();
    for (JsonElement element : golden.getAsJsonArray("fixtures")) {
      JsonObject fixture = element.getAsJsonObject();
      WorkloadIdentity identity = identity(fixture, fixture.get("displayLabel").getAsString());
      assertEquals(fixture.get("expectedSemanticId").getAsString(), identity.semanticId());
      assertEquals(identity.semanticId(), identity.seriesId());
      assertEquals(requiredDimensions(fixture), identity.dimensions().keySet());
      categories.add(dimension(fixture, "category"));
      if (fixture.getAsJsonObject("dimensions").has("visibility")) {
        visibility.add(dimension(fixture, "visibility"));
        submissionStates.add(dimension(fixture, "submission-state"));
      }
    }

    assertTrue(categories.containsAll(Set.of("cpu", "normal-text", "input", "textarea")));
    assertTrue(visibility.containsAll(Set.of("visible", "offscreen")));
    assertTrue(submissionStates.contains("unchanged"));
  }

  @Test
  void alteredLiteralGoldenSemanticIdFailsComparison() {
    JsonObject fixture = fixture("cpu-wrapped-paragraph");
    WorkloadIdentity identity = identity(fixture, "Literal golden check");
    String staleGolden = fixture.get("expectedSemanticId").getAsString() + "-stale";

    assertThrows(AssertionError.class, () -> assertEquals(staleGolden, identity.semanticId()));
  }

  @Test
  void rejectsEveryOmittedOrUnexpectedDimensionFromCompleteSchemas() {
    for (JsonElement element : golden().getAsJsonArray("fixtures")) {
      JsonObject fixture = element.getAsJsonObject();
      for (String dimension : fixture.getAsJsonObject("dimensions").keySet()) {
        IllegalArgumentException failure =
            assertThrows(
                IllegalArgumentException.class,
                () -> identity(fixture, "Missing " + dimension, Map.of(), Set.of(dimension)));
        assertTrue(failure.getMessage().contains(dimension), dimension);
      }
    }
    for (JsonElement element : golden().getAsJsonArray("currentE4CpuCases")) {
      WorkloadIdentity current = currentCpuIdentity(element.getAsJsonObject());
      for (Dimension dimension : current.dimensions().keySet()) {
        IllegalArgumentException failure =
            assertThrows(
                IllegalArgumentException.class,
                () -> rebuild(current, Set.of(dimension), Map.of()));
        assertTrue(failure.getMessage().contains(dimension.key()), dimension.key());
      }
    }

    JsonObject normalText = fixture("normal-text-visible");
    IllegalArgumentException unexpected =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                identity(
                    normalText,
                    "Unexpected control state",
                    Map.of("caret-state", "none"),
                    Set.of()));
    assertTrue(unexpected.getMessage().contains("unexpected=[caret-state]"));
  }

  @Test
  void inventoriesEveryCurrentJmhOperationWithOperationSpecificSchemas() {
    Set<String> inventoried = new HashSet<>();
    for (JsonElement operation : golden().getAsJsonArray("currentE4CpuOperations")) {
      inventoried.add(operation.getAsString());
    }
    Set<String> implemented =
        jmhMethodsInHierarchy(TextCalculationBenchmark.class, Benchmark.class).stream()
            .map(java.lang.reflect.Method::getName)
            .collect(Collectors.toSet());

    assertEquals(9, inventoried.size());
    assertEquals(implemented, inventoried);
    assertTrue(WorkloadIdentity.supportedOperations(Category.CPU).containsAll(implemented));
    assertTrue(
        WorkloadIdentity.supportedOperations(Category.CPU).contains("measureParameterizedText"));
    Set<String> executableCases = new HashSet<>();
    for (JsonElement element : golden().getAsJsonArray("currentE4CpuCases")) {
      JsonObject currentCase = element.getAsJsonObject();
      String operation = currentCase.get("operation").getAsString();
      executableCases.add(operation);
      WorkloadIdentity identity = currentCpuIdentity(currentCase);
      assertEquals(
          WorkloadIdentity.requiredDimensions(Category.CPU, operation),
          identity.dimensions().keySet(),
          operation);
    }
    assertEquals(implemented, executableCases);
    for (String operation : implemented) {
      Set<Dimension> schema = WorkloadIdentity.requiredDimensions(Category.CPU, operation);
      assertTrue(schema.containsAll(commonCpuDimensions()), operation);
    }
    assertTrue(
        WorkloadIdentity.requiredDimensions(Category.CPU, "measureWrappedParagraph")
            .containsAll(
                Set.of(
                    Dimension.MEASUREMENT_OFFSET_X_PX,
                    Dimension.WRAP_WIDTH_PX,
                    Dimension.WRAP_WIDTH_POLICY,
                    Dimension.WRAPPING_POLICY)));
    assertTrue(
        WorkloadIdentity.requiredDimensions(Category.CPU, "measureLongSingleFont")
            .contains(Dimension.CONTENT_REPEAT_COUNT));
    assertTrue(
        WorkloadIdentity.requiredDimensions(Category.CPU, "findCaretNearEnd")
            .containsAll(
                Set.of(
                    Dimension.CARET_OFFSET_INSET_X_PX,
                    Dimension.CARET_OFFSET_POLICY,
                    Dimension.CONTENT_REPEAT_COUNT,
                    Dimension.LINE_HEIGHT)));
    assertTrue(
        WorkloadIdentity.requiredDimensions(Category.CPU, "layoutTextDenseInlineContent")
            .containsAll(
                Set.of(
                    Dimension.CONTAINER_HEIGHT_PX,
                    Dimension.CONTAINER_WIDTH_PX,
                    Dimension.INLINE_LAYOUT_START_Y_PX,
                    Dimension.DISPLAY,
                    Dimension.POSITION,
                    Dimension.WHITE_SPACE,
                    Dimension.TEXT_ALIGN,
                    Dimension.OVERFLOW_WRAP,
                    Dimension.WORD_BREAK,
                    Dimension.TAB_SIZE,
                    Dimension.TEXT_NODE_COUNT,
                    Dimension.COLOR)));
    assertTrue(
        WorkloadIdentity.requiredDimensions(Category.CPU, "measureParameterizedText")
            .containsAll(
                Set.of(
                    Dimension.DECLARED_SOURCE_LINE_COUNT,
                    Dimension.DECLARED_VISUAL_LINE_COUNT,
                    Dimension.DEFERRED_SUFFIX_CODE_POINT_COUNT,
                    Dimension.FALLBACK_TRANSITION_COUNT,
                    Dimension.LINE_START_KERNING_TRANSITION_COUNT,
                    Dimension.MEASUREMENT_OFFSET_X_PX,
                    Dimension.PARAGRAPH_COUNT,
                    Dimension.SOURCE_CODE_POINT_COUNT,
                    Dimension.WRAP_WIDTH_PX)));
  }

  @Test
  void exactCorpusDeclarationsMatchCurrentSourceContentAndDerivedShapes() throws Exception {
    JsonObject corpus = golden().getAsJsonObject("currentCorpusText");
    assertEquals(corpus.get("latin-v1").getAsString(), TextWorkloads.LATIN);
    assertEquals(
        corpus.get("wrapped-paragraph-v1").getAsString(), TextWorkloads.WRAPPED_PARAGRAPH);
    assertEquals(corpus.get("mixed-cjk-v1").getAsString(), TextWorkloads.MIXED_CJK);
    assertEquals(
        corpus.get("supplementary-unicode-v1").getAsString(),
        TextWorkloads.SUPPLEMENTARY_UNICODE);
    assertEquals(corpus.get("missing-glyphs-v1").getAsString(), TextWorkloads.MISSING_GLYPHS);

    JsonObject longSingleFont = golden().getAsJsonObject("currentLongSingleFont");
    assertEquals("long-single-font-v1", longSingleFont.get("workloadContent").getAsString());
    assertEquals("latin-v1", longSingleFont.get("baseWorkloadContent").getAsString());
    assertEquals(" ", longSingleFont.get("separator").getAsString());
    assertEquals(
        longSingleFont.get("repeatCount").getAsInt(),
        TextWorkloads.LONG_SINGLE_FONT_REPEAT_COUNT);
    assertEquals(
        (TextWorkloads.LATIN + " ").repeat(TextWorkloads.LONG_SINGLE_FONT_REPEAT_COUNT),
        TextWorkloads.LONG_SINGLE_FONT);

    Set<String> declaredCorpus = new HashSet<>(corpus.keySet());
    declaredCorpus.add(longSingleFont.get("workloadContent").getAsString());
    for (JsonElement element : golden().getAsJsonArray("currentE4CpuCases")) {
      assertTrue(
          declaredCorpus.contains(
              element
                  .getAsJsonObject()
                  .getAsJsonObject("dimensions")
                  .get("workload-content")
                  .getAsString()));
    }

    JsonObject renderingCorpus = golden().getAsJsonObject("currentRenderingCorpus");
    assertEquals(
        "alternating-latin-mixed-cjk-v1",
        renderingCorpus.get("workloadContent").getAsString());
    assertEquals("latin-v1", renderingCorpus.get("evenSourceWorkloadContent").getAsString());
    assertEquals("mixed-cjk-v1", renderingCorpus.get("oddSourceWorkloadContent").getAsString());
    assertEquals("remove-ascii-spaces", renderingCorpus.get("transform").getAsString());
    assertEquals("even-latin", renderingCorpus.get("alternationStart").getAsString());
    assertEquals(
        TextWorkloads.LATIN.replace(" ", ""),
        RenderingWorkloadSpecifications.CURRENT.transformedContent(0));
    assertEquals(
        TextWorkloads.MIXED_CJK.replace(" ", ""),
        RenderingWorkloadSpecifications.CURRENT.transformedContent(1));
  }

  @Test
  void declarativeSpecificationsMatchGoldenInventoryAndRuntimeContracts() throws Exception {
    WorkloadIdentity cpu = identity(fixture("cpu-wrapped-paragraph"), "CPU source inventory");
    BenchmarkMode benchmarkMode = TextCalculationBenchmark.class.getAnnotation(BenchmarkMode.class);
    OutputTimeUnit outputTimeUnit =
        TextCalculationBenchmark.class.getAnnotation(OutputTimeUnit.class);
    State state = TextCalculationBenchmark.class.getAnnotation(State.class);
    Setup setup =
        TextCalculationBenchmark.class
            .getDeclaredMethod("setUp", BenchmarkParams.class)
            .getAnnotation(Setup.class);
    assertEquals(List.of(Mode.AverageTime), List.of(benchmarkMode.value()));
    assertEquals(TimeUnit.MICROSECONDS, outputTimeUnit.value());
    assertEquals(Scope.Benchmark, state.value());
    assertEquals(Level.Trial, setup.value());
    assertDimension(cpu, Dimension.BENCHMARK_MODE, Mode.AverageTime);
    assertDimension(cpu, Dimension.OUTPUT_TIME_UNIT, outputTimeUnit.value());
    assertDimension(cpu, Dimension.STATE_SCOPE, state.value());
    assertDimension(cpu, Dimension.FONT_SIZE_PX, CpuWorkloadSpecifications.FONT_SIZE_PX);
    assertDimension(cpu, Dimension.LINE_HEIGHT, CpuWorkloadSpecifications.LINE_HEIGHT);
    assertDimension(cpu, Dimension.WRAP_WIDTH_PX, CpuWorkloadSpecifications.WRAP_WIDTH_PX);
    assertDimension(cpu, Dimension.MEASUREMENT_OFFSET_X_PX,
        CpuWorkloadSpecifications.MEASUREMENT_OFFSET_X_PX);
    assertDimension(cpu, Dimension.SETUP_LEVEL, setup.value());
    assertEquals(128, TextWorkloads.LONG_SINGLE_FONT_REPEAT_COUNT);
    assertDimension(
        currentCpuIdentity(currentCpuCase("measureLongSingleFont")),
        Dimension.CONTENT_REPEAT_COUNT,
        TextWorkloads.LONG_SINGLE_FONT_REPEAT_COUNT);
    List<Font> fallbackFontChain = CpuWorkloadSpecifications.FALLBACK_FONT_CHAIN;
    assertEquals(2, fallbackFontChain.size());
    assertSame(Font.ROBOTO_REGULAR, fallbackFontChain.get(0));
    assertSame(Font.NOTO_SANS_CJK_SC_REGULAR, fallbackFontChain.get(1));
    List<String> fallbackIdentities =
        fallbackFontChain.stream().map(TextStyleSpecification::fontObjectIdentity).toList();
    for (String operation :
        List.of("measureMixedCjk", "measureSupplementaryUnicode", "measureMissingGlyphs")) {
      assertDimension(
          currentCpuIdentity(currentCpuCase(operation)), Dimension.FONT_CHAIN, fallbackIdentities);
    }
    assertSame(Font.DEFAULT, CpuWorkloadSpecifications.MEASURE_LATIN.orderedFonts().getFirst());
    assertSame(
        Font.DEFAULT,
        CpuWorkloadSpecifications.MEASURE_WRAPPED_PARAGRAPH.orderedFonts().getFirst());
    assertSame(Font.DEFAULT, CpuWorkloadSpecifications.FIND_CARET_NEAR_BEGINNING.font());
    assertSame(Font.DEFAULT, CpuWorkloadSpecifications.FIND_CARET_NEAR_END.font());
    assertEquals(1.0f, CpuWorkloadSpecifications.FIND_CARET_NEAR_BEGINNING.offsetOrInsetXPx());
    assertEquals(1.0f, CpuWorkloadSpecifications.FIND_CARET_NEAR_END.offsetOrInsetXPx());
    assertEquals(
        "fixed",
        currentCpuIdentity(currentCpuCase("findCaretNearBeginning"))
            .dimensions()
            .get(Dimension.CARET_OFFSET_POLICY));
    assertEquals(
        "measured-width-minus-inset",
        currentCpuIdentity(currentCpuCase("findCaretNearEnd"))
            .dimensions()
            .get(Dimension.CARET_OFFSET_POLICY));
    WorkloadIdentity layout = currentCpuIdentity(currentCpuCase("layoutTextDenseInlineContent"));
    assertDimension(
        layout,
        Dimension.TEXT_NODE_COUNT,
        CpuWorkloadSpecifications.LAYOUT_DENSE_INLINE_CONTENT.textNodeCount());
    assertDimension(
        layout,
        Dimension.INLINE_LAYOUT_START_Y_PX,
        CpuWorkloadSpecifications.LAYOUT_DENSE_INLINE_CONTENT.layoutStartYPx());

    assertEquals(
        CpuWorkloadSpecifications.currentOperations().keySet(),
        golden().getAsJsonArray("currentE4CpuOperations").asList().stream()
            .map(JsonElement::getAsString)
            .collect(Collectors.toSet()));
    for (Map.Entry<String, OperationSpec> entry :
        CpuWorkloadSpecifications.currentOperations().entrySet()) {
      assertEquals(
          currentCpuIdentity(currentCpuCase(entry.getKey())),
          currentCpuIdentity(entry.getValue()),
          entry.getKey());
      assertEquals(
          currentCpuIdentity(currentCpuCase(entry.getKey())),
          CpuWorkloadSpecifications.identity(entry.getValue()),
          "producer " + entry.getKey());
    }
    assertCpuBenchmarkAnnotationsAndDispatchAreAligned(TextCalculationBenchmark.class, cpu);

    Specification renderingSpecification = RenderingWorkloadSpecifications.CURRENT;
    assertEquals(2, renderingSpecification.measurementOrder().size());
    assertSame(Font.ROBOTO_REGULAR, renderingSpecification.prewarmFonts().get(0));
    assertSame(Font.NOTO_SANS_CJK_SC_REGULAR, renderingSpecification.prewarmFonts().get(1));
    assertSame(Font.ROBOTO_REGULAR, renderingSpecification.style().orderedFonts().get(0));
    assertSame(Font.NOTO_SANS_CJK_SC_REGULAR, renderingSpecification.style().orderedFonts().get(1));
    assertSame(FontStretch.NORMAL, renderingSpecification.style().effectiveFontStretch());
    assertEquals(4, renderingSpecification.layoutFonts().size());
    assertSame(Font.ROBOTO_REGULAR, renderingSpecification.layoutFonts().get(0));
    assertSame(Font.ROBOTO_LIGHT, renderingSpecification.layoutFonts().get(1));
    assertSame(Font.ROBOTO_BOLD, renderingSpecification.layoutFonts().get(2));
    assertSame(Font.NOTO_SANS_CJK_SC_REGULAR, renderingSpecification.layoutFonts().get(3));
    for (SceneSpecification scene : renderingSpecification.measurementOrder()) {
      assertEquals(
          currentRenderingIdentity(currentRenderingCase(scene.name())),
          currentRenderingIdentity(renderingSpecification, scene),
          scene.name());
      assertEquals(
          currentRenderingIdentity(currentRenderingCase(scene.name())),
          renderingSpecification.identity(scene),
          "producer " + scene.name());
    }

    WorkloadIdentity rendering = currentRenderingIdentity(
        renderingSpecification, renderingSpecification.scene("small"));
    assertDimension(rendering, Dimension.FRAME_WIDTH_PX, renderingSpecification.window().widthPx());
    assertDimension(rendering, Dimension.FRAME_HEIGHT_PX, renderingSpecification.window().heightPx());
    assertDimension(
        rendering, Dimension.INLINE_LAYOUT_START_Y_PX,
        renderingSpecification.inlineLayoutStartYPx());
    assertDimension(rendering, Dimension.WARMUP_FRAMES, renderingSpecification.warmupFrames());
    assertDimension(rendering, Dimension.MEASURED_FRAMES, renderingSpecification.measuredFrames());
    assertDimension(rendering, Dimension.FONT_CHAIN,
        renderingSpecification.fontExecutionIdentities());
  }

  @Test
  void rejectsJmhParamFieldsAndEveryConfigurationAnnotationOverride() {
    assertThrows(
        IllegalArgumentException.class,
        () -> assertJmhMethodContract(ParamDriftFixture.class));
    for (Class<?> fixture :
        List.of(
            BenchmarkModeOverrideFixture.class,
            OutputTimeUnitOverrideFixture.class,
            ThreadsOverrideFixture.class,
            ForkOverrideFixture.class,
            WarmupOverrideFixture.class,
            MeasurementOverrideFixture.class)) {
      IllegalArgumentException failure =
          assertThrows(IllegalArgumentException.class, () -> assertJmhMethodContract(fixture));
      assertTrue(failure.getMessage().contains("Method-level JMH override"), fixture.getName());
    }

    WorkloadIdentity cpu = identity(fixture("cpu-wrapped-paragraph"), "JMH drift baseline");
    IllegalArgumentException stateFailure =
        assertThrows(
            IllegalArgumentException.class,
            () -> assertJmhClassAnnotationContract(StateDriftFixture.class, cpu));
    assertTrue(stateFailure.getMessage().contains("@State"));
    IllegalArgumentException forkShapeFailure =
        assertThrows(
            IllegalArgumentException.class,
            () -> assertJmhClassAnnotationContract(ForkJvmArgsDriftFixture.class, cpu));
    assertTrue(forkShapeFailure.getMessage().contains("@Fork"));

    IllegalArgumentException inheritedBenchmarkFailure =
        assertThrows(
            IllegalArgumentException.class,
            () -> assertJmhMethodContract(InheritedBenchmarkFixture.class));
    assertTrue(inheritedBenchmarkFailure.getMessage().contains("Inherited @Benchmark"));
    IllegalArgumentException inheritedSetupFailure =
        assertThrows(
            IllegalArgumentException.class,
            () -> assertJmhMethodContract(InheritedSetupFixture.class));
    assertTrue(inheritedSetupFailure.getMessage().contains("Inherited @Setup"));

    IllegalArgumentException inheritedClassOverrideFailure =
        assertThrows(
            IllegalArgumentException.class,
            () -> assertJmhClassAnnotationContract(InheritedWarmupClassOverrideFixture.class, cpu));
    assertTrue(inheritedClassOverrideFailure.getMessage().contains("@Warmup"));

    IllegalArgumentException inheritedUnsupportedClassFailure =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                assertJmhClassAnnotationContract(
                    InheritedUnsupportedClassAnnotationFixture.class, cpu));
    assertTrue(
        inheritedUnsupportedClassFailure
            .getMessage()
            .contains(OperationsPerInvocation.class.getName()));

    java.lang.reflect.Method inheritedOverride =
        java.util.Arrays.stream(InheritedMethodOverrideBase.class.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(Benchmark.class))
            .findFirst()
            .orElseThrow();
    IllegalArgumentException inheritedMethodOverrideFailure =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                assertEffectiveBenchmarkAnnotations(
                    InheritedMethodOverrideFixture.class, inheritedOverride, cpu));
    assertTrue(inheritedMethodOverrideFailure.getMessage().contains("effective @Warmup"));
  }

  @Test
  void jmhCpuGradleSettingsAreParsedAndAlignedOnlyFromActualTaskArguments() throws Exception {
    WorkloadIdentity cpu = identity(fixture("cpu-wrapped-paragraph"), "CPU task inventory");
    String buildScript =
        Files.readString(repositoryRoot().resolve("spinygui.benchmark/build.gradle.kts"));
    List<String> cpuArguments = javaExecTaskArguments(buildScript, "jmhCpu", "args");
    assertEquals(
        List.of(
            "com.spinyowl.spinygui.benchmark.cpu.*",
            "-wi", "3",
            "-i", "5",
            "-w", "500ms",
            "-r", "500ms",
            "-f", "2",
            "-jvmArgsAppend", "--enable-native-access=ALL-UNNAMED",
            "-prof", "gc",
            "-rf", "json"),
        cpuArguments);
    assertEquals(
        List.of("--enable-native-access=ALL-UNNAMED"),
        javaExecTaskArguments(buildScript, "jmhRendering", "jvmArgs"));
    assertEquals(
        List.of("com.spinyowl.spinygui.benchmark.cpu.CpuBenchmarkMain"),
        javaExecTaskArguments(buildScript, "jmhCpu", "mainClass.set"));
    String negativeFixture =
        "// tasks.register<JavaExec>(\"jmhCpu\") { args(\"-wi\", \"3\") }\n"
            + "tasks.register<JavaExec>(\"jmhCpu\") {\n"
            + " description = \"args(\\\"-wi\\\", \\\"3\\\") --enable-native-access=ALL-UNNAMED\"\n"
            + " val unused = listOf(\"-i\", \"5\", \"--enable-native-access=ALL-UNNAMED\")\n"
            + " args(\"com.spinyowl.spinygui.benchmark.cpu.*\")\n"
            + "}\n"
            + "tasks.register<JavaExec>(\"jmhRendering\") {"
            + " jvmArgs(\"--enable-native-access=ALL-UNNAMED\") }";
    assertEquals(
        List.of("com.spinyowl.spinygui.benchmark.cpu.*"),
        javaExecTaskArguments(negativeFixture, "jmhCpu", "args"));
    assertFalse(
        javaExecTaskArguments(negativeFixture, "jmhCpu", "args")
            .contains("--enable-native-access=ALL-UNNAMED"));

    assertOptionDimension(cpuArguments, "-wi", cpu, Dimension.WARMUP_ITERATIONS);
    assertOptionDimension(cpuArguments, "-w", cpu, Dimension.WARMUP_TIME);
    assertOptionDimension(cpuArguments, "-i", cpu, Dimension.MEASUREMENT_ITERATIONS);
    assertOptionDimension(cpuArguments, "-r", cpu, Dimension.MEASUREMENT_TIME);
    assertOptionDimension(cpuArguments, "-f", cpu, Dimension.FORKS);
    assertOptionDimension(cpuArguments, "-prof", cpu, Dimension.PROFILER);
    assertEquals("json", option(cpuArguments, "-rf"));
    assertEquals("--enable-native-access=ALL-UNNAMED", option(cpuArguments, "-jvmArgsAppend"));
    assertDimension(cpu, Dimension.NATIVE_ACCESS, "all-unnamed");
    assertFalse(cpuArguments.contains("-t"));
    assertFalse(cpuArguments.contains("-bs"));
    assertFalse(cpuArguments.contains("-wbs"));
    assertFalse(cpuArguments.contains("-wf"));
    assertDimension(cpu, Dimension.THREADS, Defaults.THREADS);
    assertDimension(cpu, Dimension.MEASUREMENT_BATCH_SIZE, Defaults.MEASUREMENT_BATCHSIZE);
    assertDimension(cpu, Dimension.WARMUP_BATCH_SIZE, Defaults.WARMUP_BATCHSIZE);
    assertDimension(cpu, Dimension.WARMUP_FORKS, Defaults.WARMUP_FORKS);
    assertEquals(
        "service-and-operation-fixtures-created-once-and-reused-through-trial",
        cpu.dimensions().get(Dimension.FIXTURE_PREPARATION_POLICY));
    assertEquals(
        "current-corpus-in-trial-setup",
        cpu.dimensions().get(Dimension.FONT_FIXTURE_POLICY));
  }

  @Test
  void declarativeSourceDriftChangesIdentityOrFailsClosed() {
    MeasurementSpec wrapped = CpuWorkloadSpecifications.MEASURE_WRAPPED_PARAGRAPH;
    WorkloadIdentity wrappedIdentity = currentCpuIdentity(wrapped);
    List<MeasurementSpec> changedMeasurements =
        List.of(
            new MeasurementSpec(
                wrapped.operation(),
                "wrapped-paragraph-v2",
                wrapped.text() + " changed",
                wrapped.api(),
                wrapped.orderedFonts(),
                wrapped.fontSizePx(),
                wrapped.lineHeight(),
                wrapped.measurementOffsetXPx(),
                wrapped.maximumWidthPx(),
                wrapped.wordWrap(),
                wrapped.contentRepeatCount()),
            new MeasurementSpec(
                wrapped.operation(),
                wrapped.workloadContent(),
                wrapped.text(),
                wrapped.api(),
                List.of(Font.ROBOTO_BOLD),
                wrapped.fontSizePx(),
                wrapped.lineHeight(),
                wrapped.measurementOffsetXPx(),
                wrapped.maximumWidthPx(),
                wrapped.wordWrap(),
                wrapped.contentRepeatCount()),
            new MeasurementSpec(
                wrapped.operation(), wrapped.workloadContent(), wrapped.text(), wrapped.api(),
                wrapped.orderedFonts(), 17, wrapped.lineHeight(), wrapped.measurementOffsetXPx(),
                wrapped.maximumWidthPx(), wrapped.wordWrap(), wrapped.contentRepeatCount()),
            new MeasurementSpec(
                wrapped.operation(), wrapped.workloadContent(), wrapped.text(), wrapped.api(),
                wrapped.orderedFonts(), wrapped.fontSizePx(), 1.3f,
                wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(), wrapped.wordWrap(),
                wrapped.contentRepeatCount()),
            new MeasurementSpec(
                wrapped.operation(), wrapped.workloadContent(), wrapped.text(), wrapped.api(),
                wrapped.orderedFonts(), wrapped.fontSizePx(), wrapped.lineHeight(), 0.5f,
                wrapped.maximumWidthPx(), wrapped.wordWrap(), wrapped.contentRepeatCount()),
            new MeasurementSpec(
                wrapped.operation(), wrapped.workloadContent(), wrapped.text(), wrapped.api(),
                wrapped.orderedFonts(), wrapped.fontSizePx(), wrapped.lineHeight(),
                wrapped.measurementOffsetXPx(), 241f, wrapped.wordWrap(),
                wrapped.contentRepeatCount()),
            new MeasurementSpec(
                wrapped.operation(), wrapped.workloadContent(), wrapped.text(), wrapped.api(),
                wrapped.orderedFonts(), wrapped.fontSizePx(), wrapped.lineHeight(),
                wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(), false,
                wrapped.contentRepeatCount()));
    for (MeasurementSpec changed : changedMeasurements) {
      assertNotEquals(wrappedIdentity.semanticId(), currentCpuIdentity(changed).semanticId());
    }
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new MeasurementSpec(
                wrapped.operation(), wrapped.workloadContent(), wrapped.text() + " drift",
                wrapped.api(), wrapped.orderedFonts(), wrapped.fontSizePx(), wrapped.lineHeight(),
                wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(), wrapped.wordWrap(),
                wrapped.contentRepeatCount()));

    MeasurementSpec fallback = CpuWorkloadSpecifications.MEASURE_MIXED_CJK;
    MeasurementSpec reversedFallback =
        new MeasurementSpec(
            fallback.operation(), fallback.workloadContent(), fallback.text(), fallback.api(),
            List.of(Font.NOTO_SANS_CJK_SC_REGULAR, Font.ROBOTO_REGULAR),
            fallback.fontSizePx(), fallback.lineHeight(), fallback.measurementOffsetXPx(),
            fallback.maximumWidthPx(), fallback.wordWrap(), fallback.contentRepeatCount());
    assertNotEquals(
        currentCpuIdentity(fallback).semanticId(),
        currentCpuIdentity(reversedFallback).semanticId());

    Specification rendering = RenderingWorkloadSpecifications.CURRENT;
    WorkloadIdentity renderingIdentity =
        currentRenderingIdentity(rendering, rendering.scene("small"));
    Specification movedContainer =
        copySpecification(
            rendering,
            new ContainerSpecification(
                rendering.container().positionXPx() + 1,
                rendering.container().positionYPx(),
                rendering.container().widthPx(),
                rendering.container().heightPx()),
            rendering.style(),
            rendering.prewarmFonts(),
            rendering.sourceContent(),
            rendering.workloadContent(),
            rendering.measurementOrder());
    assertNotEquals(
        renderingIdentity.semanticId(),
        currentRenderingIdentity(movedContainer, movedContainer.scene("small")).semanticId());

    TextStyleSpecification largerStyle =
        copyStyle(rendering.style(), rendering.style().orderedFonts(),
            rendering.style().fontSizePx() + 1, rendering.style().effectiveFontStretch());
    Specification largerText =
        copySpecification(
            rendering,
            rendering.container(),
            largerStyle,
            rendering.prewarmFonts(),
            rendering.sourceContent(),
            rendering.workloadContent(),
            rendering.measurementOrder());
    assertNotEquals(
        renderingIdentity.semanticId(),
        currentRenderingIdentity(largerText, largerText.scene("small")).semanticId());

    List<Font> reversedFonts = List.of(Font.NOTO_SANS_CJK_SC_REGULAR, Font.ROBOTO_REGULAR);
    TextStyleSpecification reversedStyle =
        copyStyle(
            rendering.style(),
            reversedFonts,
            rendering.style().fontSizePx(),
            rendering.style().effectiveFontStretch());
    Specification reversedFontPaths =
        copySpecification(
            rendering,
            rendering.container(),
            reversedStyle,
            reversedFonts,
            rendering.sourceContent(),
            rendering.workloadContent(),
            rendering.measurementOrder());
    assertNotEquals(
        renderingIdentity.semanticId(),
        currentRenderingIdentity(reversedFontPaths, reversedFontPaths.scene("small")).semanticId());

    List<SceneSpecification> changedCompanion =
        List.of(new SceneSpecification("small", 100), new SceneSpecification("large", 1_001));
    Specification companionDrift =
        copySpecification(
            rendering,
            rendering.container(),
            rendering.style(),
            rendering.prewarmFonts(),
            rendering.sourceContent(),
            rendering.workloadContent(),
            changedCompanion);
    assertNotEquals(
        renderingIdentity.semanticId(),
        currentRenderingIdentity(companionDrift, companionDrift.scene("small")).semanticId());

    ClearSpecification clear = rendering.clear();
    List<ClearSpecification> clearDrifts =
        List.of(
            new ClearSpecification(
                clear.enabled(), clear.red() + 0.25f, clear.green(), clear.blue(), clear.alpha(),
                clear.mask()),
            new ClearSpecification(
                clear.enabled(), clear.red(), clear.green() + 0.25f, clear.blue(), clear.alpha(),
                clear.mask()),
            new ClearSpecification(
                clear.enabled(), clear.red(), clear.green(), clear.blue() + 0.25f, clear.alpha(),
                clear.mask()),
            new ClearSpecification(
                clear.enabled(), clear.red(), clear.green(), clear.blue(), clear.alpha() - 0.25f,
                clear.mask()),
            new ClearSpecification(
                clear.enabled(), clear.red(), clear.green(), clear.blue(), clear.alpha(),
                clear.mask() + 1));
    for (ClearSpecification clearDrift : clearDrifts) {
      assertIdentityChangesOrFailsClosed(
          renderingIdentity,
          () ->
              currentRenderingIdentity(
                  copySpecificationWithBehavior(
                      rendering, clearDrift, rendering.structuralValidation()),
                  rendering.scene("small")));
    }

    StructuralValidationSpecification validation = rendering.structuralValidation();
    List<StructuralValidationSpecification> validationDrifts =
        List.of(
            new StructuralValidationSpecification(
                validation.enabled(),
                validation.sceneName(),
                "wrong-command-contract"),
            new StructuralValidationSpecification(
                validation.enabled(), "large", validation.commandContract()));
    for (StructuralValidationSpecification validationDrift : validationDrifts) {
      assertIdentityChangesOrFailsClosed(
          renderingIdentity,
          () ->
              currentRenderingIdentity(
                  copySpecificationWithBehavior(rendering, clear, validationDrift),
                  rendering.scene("small")));
    }

    assertThrows(
        IllegalArgumentException.class,
        () ->
            copySpecification(
                rendering,
                rendering.container(),
                rendering.style(),
                List.of(Font.ROBOTO_BOLD, Font.NOTO_SANS_CJK_SC_REGULAR),
                rendering.sourceContent(),
                rendering.workloadContent(),
                rendering.measurementOrder()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            copySpecification(
                rendering,
                rendering.container(),
                rendering.style(),
                rendering.prewarmFonts(),
                rendering.sourceContent(),
                rendering.workloadContent(),
                List.of(rendering.scene("large"), rendering.scene("small"))));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            copySpecification(
                rendering,
                rendering.container(),
                rendering.style(),
                rendering.prewarmFonts(),
                List.of(TextWorkloads.LATIN + " drift", TextWorkloads.MIXED_CJK),
                rendering.workloadContent(),
                rendering.measurementOrder()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CpuWorkloadSpecifications.TrialSetupSpec(
                CpuWorkloadSpecifications.TRIAL_SETUP.roundToPixel(),
                List.of(
                    CpuWorkloadSpecifications.MEASURE_WRAPPED_PARAGRAPH,
                    CpuWorkloadSpecifications.MEASURE_LATIN,
                    CpuWorkloadSpecifications.MEASURE_MIXED_CJK,
                    CpuWorkloadSpecifications.MEASURE_SUPPLEMENTARY_UNICODE,
                    CpuWorkloadSpecifications.MEASURE_MISSING_GLYPHS),
                CpuWorkloadSpecifications.FIND_CARET_NEAR_END,
                CpuWorkloadSpecifications.LAYOUT_DENSE_INLINE_CONTENT,
                CpuWorkloadSpecifications.TRIAL_SETUP.fixturePreparationPolicy(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fontFixturePolicy(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fontResolver()));
  }

  @Test
  void trialPreparedCaretAndInlineStateCannotDriftFromIdentitySpecifications() {
    var inline = CpuWorkloadSpecifications.LAYOUT_DENSE_INLINE_CONTENT;
    WorkloadIdentity inlineIdentity = currentCpuIdentity(inline);
    List<CpuWorkloadSpecifications.InlineLayoutSpec> geometryAndContentDrifts =
        List.of(
            copyInline(inline, "wrapped-paragraph-v2", inline.text() + " changed",
                inline.textNodeCount(), inline.containerWidthPx(), inline.containerHeightPx(),
                inline.layoutStartYPx(), inline.style()),
            copyInline(inline, inline.workloadContent(), inline.text(), inline.textNodeCount() + 1,
                inline.containerWidthPx(), inline.containerHeightPx(), inline.layoutStartYPx(),
                inline.style()),
            copyInline(inline, inline.workloadContent(), inline.text(), inline.textNodeCount(),
                inline.containerWidthPx() + 1, inline.containerHeightPx(), inline.layoutStartYPx(),
                inline.style()),
            copyInline(inline, inline.workloadContent(), inline.text(), inline.textNodeCount(),
                inline.containerWidthPx(), inline.containerHeightPx() + 1, inline.layoutStartYPx(),
                inline.style()),
            copyInline(inline, inline.workloadContent(), inline.text(), inline.textNodeCount(),
                inline.containerWidthPx(), inline.containerHeightPx(), inline.layoutStartYPx() + 1,
                inline.style()));
    for (var drift : geometryAndContentDrifts) {
      assertNotEquals(inlineIdentity.semanticId(), currentCpuIdentity(drift).semanticId());
      assertTrialPreparationRejects(CpuWorkloadSpecifications.FIND_CARET_NEAR_END, drift);
    }
    assertThrows(
        IllegalArgumentException.class,
        () ->
            copyInline(
                inline,
                inline.workloadContent(),
                inline.text() + " undeclared drift",
                inline.textNodeCount(),
                inline.containerWidthPx(),
                inline.containerHeightPx(),
                inline.layoutStartYPx(),
                inline.style()));

    for (InlineStyleDrift drift : InlineStyleDrift.values()) {
      if (drift == InlineStyleDrift.EFFECTIVE_STRETCH) {
        assertThrows(IllegalArgumentException.class, () -> driftInlineStyle(inline.style(), drift));
        continue;
      }
      var changed =
          copyInline(
              inline,
              inline.workloadContent(),
              inline.text(),
              inline.textNodeCount(),
              inline.containerWidthPx(),
              inline.containerHeightPx(),
              inline.layoutStartYPx(),
              driftInlineStyle(inline.style(), drift));
      assertIdentityChangesOrFailsClosed(inlineIdentity, () -> currentCpuIdentity(changed));
      assertTrialPreparationRejects(CpuWorkloadSpecifications.FIND_CARET_NEAR_END, changed);
    }

    var caret = CpuWorkloadSpecifications.FIND_CARET_NEAR_END;
    List<CpuWorkloadSpecifications.CaretSpec> caretDrifts =
        List.of(
            new CpuWorkloadSpecifications.CaretSpec(
                caret.operation(), "long-single-font-v2", caret.text() + " changed", caret.font(),
                caret.fontSizePx(), caret.contentRepeatCount(), caret.offsetPolicy(),
                caret.offsetOrInsetXPx(), caret.preparationLineHeight()),
            new CpuWorkloadSpecifications.CaretSpec(
                caret.operation(), caret.workloadContent(), caret.text(), Font.ROBOTO_BOLD,
                caret.fontSizePx(), caret.contentRepeatCount(), caret.offsetPolicy(),
                caret.offsetOrInsetXPx(), caret.preparationLineHeight()),
            new CpuWorkloadSpecifications.CaretSpec(
                caret.operation(), caret.workloadContent(), caret.text(), caret.font(), 17,
                caret.contentRepeatCount(), caret.offsetPolicy(), caret.offsetOrInsetXPx(),
                caret.preparationLineHeight()),
            new CpuWorkloadSpecifications.CaretSpec(
                caret.operation(), caret.workloadContent(), caret.text(), caret.font(),
                caret.fontSizePx(), caret.contentRepeatCount() + 1, caret.offsetPolicy(),
                caret.offsetOrInsetXPx(), caret.preparationLineHeight()),
            new CpuWorkloadSpecifications.CaretSpec(
                caret.operation(), caret.workloadContent(), caret.text(), caret.font(),
                caret.fontSizePx(), caret.contentRepeatCount(), caret.offsetPolicy(),
                caret.offsetOrInsetXPx() + 1, caret.preparationLineHeight()),
            new CpuWorkloadSpecifications.CaretSpec(
                caret.operation(), caret.workloadContent(), caret.text(), caret.font(),
                caret.fontSizePx(), caret.contentRepeatCount(), caret.offsetPolicy(),
                caret.offsetOrInsetXPx(), caret.preparationLineHeight() + 0.1f));
    for (var drift : caretDrifts) {
      assertNotEquals(currentCpuIdentity(caret).semanticId(), currentCpuIdentity(drift).semanticId());
      assertTrialPreparationRejects(drift, inline);
    }
  }

  @Test
  void trialFontWarmupsRequireEveryCompleteExecutionSpecificationAndSetupPolicy() {
    MeasurementSpec wrapped = CpuWorkloadSpecifications.MEASURE_WRAPPED_PARAGRAPH;
    Font changedPath =
        new Font(
            wrapped.orderedFonts().getFirst().fontFamily(),
            wrapped.orderedFonts().getFirst().style(),
            wrapped.orderedFonts().getFirst().stretch(),
            wrapped.orderedFonts().getFirst().weight(),
            "fonts/Drifted-Roboto-Regular.ttf");
    Font changedTrait =
        new Font(
            wrapped.orderedFonts().getFirst().fontFamily(),
            FontStyle.ITALIC,
            wrapped.orderedFonts().getFirst().stretch(),
            wrapped.orderedFonts().getFirst().weight(),
            wrapped.orderedFonts().getFirst().path());
    List<MeasurementSpec> sameNameDrifts =
        List.of(
            new MeasurementSpec(
                wrapped.operation(), "wrapped-paragraph-v2", wrapped.text() + " changed",
                wrapped.api(), wrapped.orderedFonts(), wrapped.fontSizePx(), wrapped.lineHeight(),
                wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(), wrapped.wordWrap(),
                wrapped.contentRepeatCount()),
            new MeasurementSpec(
                wrapped.operation(), wrapped.workloadContent(), wrapped.text(),
                CpuWorkloadSpecifications.MeasurementApi.DIRECT_FONT, wrapped.orderedFonts(),
                wrapped.fontSizePx(), wrapped.lineHeight(), wrapped.measurementOffsetXPx(),
                wrapped.maximumWidthPx(), wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, List.of(changedPath), wrapped.fontSizePx(),
                wrapped.lineHeight(), wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(),
                wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, List.of(changedTrait), wrapped.fontSizePx(),
                wrapped.lineHeight(), wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(),
                wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, wrapped.orderedFonts(), wrapped.fontSizePx() + 1,
                wrapped.lineHeight(), wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(),
                wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, wrapped.orderedFonts(), wrapped.fontSizePx(),
                wrapped.lineHeight() + 0.1f, wrapped.measurementOffsetXPx(),
                wrapped.maximumWidthPx(), wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, wrapped.orderedFonts(), wrapped.fontSizePx(),
                wrapped.lineHeight(), wrapped.measurementOffsetXPx() + 0.5f,
                wrapped.maximumWidthPx(), wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, wrapped.orderedFonts(), wrapped.fontSizePx(),
                wrapped.lineHeight(), wrapped.measurementOffsetXPx(),
                wrapped.maximumWidthPx() + 1, wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, wrapped.orderedFonts(), wrapped.fontSizePx(),
                wrapped.lineHeight(), wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(),
                !wrapped.wordWrap(), wrapped.contentRepeatCount()),
            copyMeasurement(wrapped, wrapped.orderedFonts(), wrapped.fontSizePx(),
                wrapped.lineHeight(), wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(),
                wrapped.wordWrap(), wrapped.contentRepeatCount() + 1));
    for (MeasurementSpec drift : sameNameDrifts) {
      assertTrialWarmupRejected(1, drift);
    }

    MeasurementSpec renamed =
        new MeasurementSpec(
            wrapped.operation() + "Drift", wrapped.workloadContent(), wrapped.text(), wrapped.api(),
            wrapped.orderedFonts(), wrapped.fontSizePx(), wrapped.lineHeight(),
            wrapped.measurementOffsetXPx(), wrapped.maximumWidthPx(), wrapped.wordWrap(),
            wrapped.contentRepeatCount());
    assertTrialWarmupRejected(1, renamed);

    MeasurementSpec fallback = CpuWorkloadSpecifications.MEASURE_MIXED_CJK;
    MeasurementSpec reversedFallback =
        new MeasurementSpec(
            fallback.operation(), fallback.workloadContent(), fallback.text(), fallback.api(),
            List.of(Font.NOTO_SANS_CJK_SC_REGULAR, Font.ROBOTO_REGULAR), fallback.fontSizePx(),
            fallback.lineHeight(), fallback.measurementOffsetXPx(), fallback.maximumWidthPx(),
            fallback.wordWrap(), fallback.contentRepeatCount());
    assertTrialWarmupRejected(2, reversedFallback);

    assertTrialSetupRejected(
        true,
        CpuWorkloadSpecifications.TRIAL_SETUP.fixturePreparationPolicy(),
        CpuWorkloadSpecifications.TRIAL_SETUP.fontFixturePolicy(),
        CpuWorkloadSpecifications.TRIAL_SETUP.fontResolver());
    assertTrialSetupRejected(
        CpuWorkloadSpecifications.TRIAL_SETUP.roundToPixel(),
        "parameterized-fixtures-created-in-trial-setup",
        CpuWorkloadSpecifications.TRIAL_SETUP.fontFixturePolicy(),
        CpuWorkloadSpecifications.TRIAL_SETUP.fontResolver());
    assertTrialSetupRejected(
        CpuWorkloadSpecifications.TRIAL_SETUP.roundToPixel(),
        CpuWorkloadSpecifications.TRIAL_SETUP.fixturePreparationPolicy(),
        "parameterized-corpus-in-trial-setup",
        CpuWorkloadSpecifications.TRIAL_SETUP.fontResolver());
    assertTrialSetupRejected(
        CpuWorkloadSpecifications.TRIAL_SETUP.roundToPixel(),
        CpuWorkloadSpecifications.TRIAL_SETUP.fixturePreparationPolicy(),
        CpuWorkloadSpecifications.TRIAL_SETUP.fontFixturePolicy(),
        "changed-resolver");
  }

  @Test
  void rendererPrewarmCorpusAndEveryFontChainUseExactIdentityStructure() {
    Specification rendering = RenderingWorkloadSpecifications.CURRENT;
    WorkloadIdentity baseline = currentRenderingIdentity(rendering, rendering.scene("small"));
    assertDimension(
        baseline,
        Dimension.PREWARM_WORKLOAD_CONTENT,
        rendering.prewarmWorkloadContent());
    assertThrows(
        IllegalArgumentException.class,
        () -> copySpecificationWithPrewarm(
            rendering,
            rendering.prewarmText() + " drift",
            rendering.prewarmWorkloadContent()));
    Specification changedPrewarm =
        copySpecificationWithPrewarm(
            rendering,
            rendering.prewarmText() + " changed",
            "mixed-cjk-remove-ascii-spaces-v2");
    assertNotEquals(
        baseline.semanticId(),
        currentRenderingIdentity(changedPrewarm, changedPrewarm.scene("small")).semanticId());

    for (JsonElement element : golden().getAsJsonArray("fixtures")) {
      JsonObject fixture = element.getAsJsonObject();
      String category = dimension(fixture, "category");
      String fontChain = dimension(fixture, "font-chain");
      for (String item : fontChain.split(",", -1)) {
        String exactFont = item;
        if (!"cpu".equals(category)) {
          assertTrue(item.startsWith("prewarm=") || item.startsWith("layout="), item);
          exactFont = item.substring(item.indexOf('=') + 1);
        }
        assertEquals(5, exactFont.split("\\|", -1).length, item);
      }
      if (!"cpu".equals(category)) {
        assertTrue(fontChain.contains("prewarm="), fixture.get("name").getAsString());
        assertTrue(fontChain.contains("layout="), fixture.get("name").getAsString());
      }
    }
  }

  @Test
  void dimensionSchemasCanonicalizeEquivalentRuntimeValuesWithoutForkingSeries() {
    JsonObject normal = fixture("normal-text-visible");
    WorkloadIdentity first = identity(normal, "Canonical input");
    WorkloadIdentity second =
        identity(
            normal,
            "Equivalent input",
            Map.of(
                "category", "Normal_Text",
                "font-chain",
                    List.of(dimension(normal, "font-chain").split(",", -1)),
                "font-size-px", "16.00",
                "submission-state", WorkloadIdentity.SubmissionState.CHANGED,
                "visibility", WorkloadIdentity.Visibility.VISIBLE,
                "warmup-frames", new BigDecimal("60.0")),
            Set.of());
    assertEquals(first, second);
    assertEquals(first.semanticId(), second.semanticId());

    WorkloadIdentity cpu = identity(fixture("cpu-wrapped-paragraph"), "Duration input");
    WorkloadIdentity equivalentCpu =
        identity(
            fixture("cpu-wrapped-paragraph"),
            "Equivalent duration input",
            Map.of(
                "measurement-time", "PT0.5S",
                "warmup-time", "500 ms",
                "measurement-offset-x-px", "0.000"),
            Set.of());
    assertEquals(cpu.semanticId(), equivalentCpu.semanticId());

    WorkloadIdentity escapedFirst =
        identity(
            normal,
            "Escaped font",
            Map.of(
                "font-chain",
                "prewarm=Cafe\u0301 / 100%|normal|normal|regular|fonts/Cafe.ttf,"
                    + "layout=Cafe\u0301 / 100%|normal|normal|regular|fonts/Cafe.ttf"),
            Set.of());
    WorkloadIdentity escapedSecond =
        identity(
            normal,
            "Equivalent escaped font",
            Map.of(
                "font-chain",
                List.of(
                    "prewarm=Caf\u00e9 / 100%|normal|normal|regular|fonts/Cafe.ttf",
                    "layout=Caf\u00e9 / 100%|normal|normal|regular|fonts/Cafe.ttf")),
            Set.of());
    assertEquals(escapedFirst.semanticId(), escapedSecond.semanticId());
    assertTrue(
        escapedFirst.semanticId().contains("font-chain=prewarm%3DCaf%C3%A9%20%2F%20100%25"));

    for (JsonElement element : golden().getAsJsonArray("fixtures")) {
      JsonObject fixture = element.getAsJsonObject();
      WorkloadIdentity canonical = identity(fixture, "Canonical runtime types");
      WorkloadIdentity equivalent =
          identity(
              fixture,
              "Equivalent runtime types",
              equivalentRepresentations(fixture),
              Set.of());
      assertEquals(canonical.semanticId(), equivalent.semanticId(), fixture.get("name").getAsString());
    }
  }

  @Test
  void rejectsInvalidDimensionTypesValuesAndFixedSchemaAliases() {
    JsonObject normal = fixture("normal-text-visible");
    assertThrows(
        IllegalArgumentException.class,
        () -> identity(normal, "Invalid visibility", Map.of("visibility", "nearby"), Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> identity(normal, "Invalid count", Map.of("warmup-frames", 1.5), Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> identity(normal, "Invalid font size", Map.of("font-size-px", true), Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> identity(normal, "Family-only font chain", Map.of("font-chain", "Roboto"), Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            identity(
                normal,
                "Unstaged renderer font chain",
                Map.of(
                    "font-chain",
                    "Roboto|normal|normal|regular|fonts/Roboto-Regular.ttf"),
                Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            identity(
                fixture("cpu-wrapped-paragraph"),
                "Staged CPU font chain",
                Map.of(
                    "font-chain",
                    "prewarm=Roboto|normal|normal|regular|fonts/Roboto-Regular.ttf,"
                        + "layout=Roboto|normal|normal|regular|fonts/Roboto-Regular.ttf"),
                Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> identity(normal, "Invalid fixed path", Map.of("renderer-path", "input-text"), Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> identity(normal, "Invalid workload", Map.of(), Set.of(), "renderer-control"));
    for (String observedOutput :
        Set.of(
            "command-count",
            "cull-count",
            "line-count",
            "resolved-glyph-count",
            "resolved-run-count",
            "text-fragment-count")) {
      assertThrows(
          IllegalArgumentException.class,
          () -> WorkloadIdentity.Dimension.fromKey(observedOutput),
          observedOutput);
    }
    assertThrows(
        IllegalArgumentException.class,
        () ->
            identity(
                fixture("cpu-parameterized-zero-width"),
                "Negative width",
                Map.of("wrap-width-px", -0.01),
                Set.of()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            identity(
                fixture("cpu-parameterized-zero-width"),
                "Non-finite offset",
                Map.of("measurement-offset-x-px", Double.NaN),
                Set.of()));
    assertEquals(
        "0",
        identity(fixture("cpu-parameterized-zero-width"), "Zero width")
            .dimensions()
            .get(Dimension.WRAP_WIDTH_PX));
    assertEquals(
        "character-wrap",
        identity(fixture("cpu-parameterized-character-wrap"), "Character wrapping")
            .dimensions()
            .get(Dimension.WRAPPING_POLICY));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            identity(
                fixture("cpu-parameterized-character-wrap"),
                "Finite width cannot be unwrapped",
                Map.of("wrapping-policy", "unwrapped"),
                Set.of()));
    assertEquals(
        "unwrapped",
        currentCpuIdentity(currentCpuCase("measureLatin"))
            .dimensions()
            .get(Dimension.WRAPPING_POLICY));
    assertThrows(
        IllegalArgumentException.class,
        () -> WorkloadIdentity.requiredDimensions(Category.INPUT, "render-textarea-scenario"));
    assertThrows(
        IllegalArgumentException.class,
        () -> WorkloadIdentity.requiredDimensions(Category.NORMAL_TEXT, "unknown-render-operation"));
  }

  @Test
  void coversEveryCurrentAndPlannedOperationWithAnAuthoritativeSchema() {
    Map<Category, Set<String>> covered =
        Map.of(
            Category.CPU, new HashSet<>(),
            Category.NORMAL_TEXT, new HashSet<>(),
            Category.INPUT, new HashSet<>(),
            Category.TEXTAREA, new HashSet<>());
    for (JsonElement element : golden().getAsJsonArray("currentE4CpuCases")) {
      covered.get(Category.CPU).add(element.getAsJsonObject().get("operation").getAsString());
    }
    for (JsonElement element : golden().getAsJsonArray("fixtures")) {
      JsonObject fixture = element.getAsJsonObject();
      Category category = Category.fromCanonicalValue(dimension(fixture, "category"));
      covered.get(category).add(dimension(fixture, "operation"));
    }
    Set<String> expectedCpu =
        new HashSet<>(
            Set.of(
                "findCaretNearBeginning",
                "findCaretNearEnd",
                "layoutTextDenseInlineContent",
                "measureLatin",
                "measureLongSingleFont",
                "measureMissingGlyphs",
                "measureMixedCjk",
                "measureSupplementaryUnicode",
                "measureWrappedParagraph"));
    expectedCpu.add("measureParameterizedText");
    Map<Category, Set<String>> expected =
        Map.of(
            Category.CPU, expectedCpu,
            Category.NORMAL_TEXT, Set.of("render-text", "render-normal-text-scenario"),
            Category.INPUT, Set.of("render-input-scenario"),
            Category.TEXTAREA, Set.of("render-textarea-scenario"));
    for (Category category : Category.values()) {
      assertEquals(
          expected.get(category),
          WorkloadIdentity.supportedOperations(category),
          category.toString());
      assertEquals(expected.get(category), covered.get(category), category.toString());
    }

    Set<Dimension> input =
        WorkloadIdentity.requiredDimensions(Category.INPUT, "render-input-scenario");
    Set<Dimension> textarea =
        WorkloadIdentity.requiredDimensions(Category.TEXTAREA, "render-textarea-scenario");
    assertTrue(
        input.containsAll(
            Set.of(
                Dimension.CARET_INDEX_UTF16,
                Dimension.SELECTION_START_UTF16,
                Dimension.SELECTION_END_UTF16,
                Dimension.SOURCE_UTF16_LENGTH)));
    assertTrue(
        textarea.containsAll(
            Set.of(
                Dimension.DECLARED_SOURCE_LINE_COUNT,
                Dimension.DECLARED_VISUAL_LINE_COUNT,
                Dimension.DEFERRED_SUFFIX_CODE_POINT_COUNT,
                Dimension.PARAGRAPH_COUNT,
                Dimension.WRAP_WIDTH_PX)));
    assertTrue(
        WorkloadIdentity.requiredDimensions(
                Category.NORMAL_TEXT, "render-normal-text-scenario")
            .containsAll(Set.of(Dimension.OFFSCREEN_RATIO, Dimension.OFFSCREEN_EXTENT_PX)));
    Set<Dimension> currentRendering =
        WorkloadIdentity.requiredDimensions(Category.NORMAL_TEXT, "render-text");
    assertTrue(
        currentRendering.containsAll(
            Set.of(
                Dimension.COMPANION_SCENE_SHAPE,
                Dimension.COMPANION_TEXT_NODE_COUNT,
                Dimension.MEASUREMENT_ORDER,
                Dimension.MEASUREMENT_ORDER_INDEX,
                Dimension.NATIVE_ACCESS,
                Dimension.SCENE_PAIR_COUNT,
                Dimension.WARMUP_ORDER)));
    for (Category category : Set.of(Category.NORMAL_TEXT, Category.INPUT, Category.TEXTAREA)) {
      for (String operation : WorkloadIdentity.supportedOperations(category)) {
        assertTrue(
            WorkloadIdentity.requiredDimensions(category, operation)
                .containsAll(
                    Set.of(
                        Dimension.MEASUREMENT_ORDER,
                        Dimension.MEASUREMENT_ORDER_INDEX,
                        Dimension.NATIVE_ACCESS)));
      }
    }
  }

  @Test
  void everyAcceptedDeclaredInputChangeChangesSemanticIdentity() {
    Set<Dimension> provenSensitive = new HashSet<>();
    Set<Dimension> independentlyVariable = new HashSet<>();
    for (JsonElement element : golden().getAsJsonArray("fixtures")) {
      JsonObject fixture = element.getAsJsonObject();
      WorkloadIdentity baseline = identity(fixture, "Input sensitivity baseline");
      for (Map.Entry<String, JsonElement> entry : fixture.getAsJsonObject("dimensions").entrySet()) {
        Dimension dimension = Dimension.fromKey(entry.getKey());
        Object replacement = sensitivityValue(dimension, entry.getValue());
        if (replacement == null) {
          continue;
        }
        independentlyVariable.add(dimension);
        try {
          WorkloadIdentity changed =
              identity(
                  fixture,
                  "Changed " + dimension.key(),
                  Map.of(dimension.key(), replacement),
                  Set.of());
          assertNotEquals(baseline.semanticId(), changed.semanticId(), dimension.key());
          provenSensitive.add(dimension);
        } catch (IllegalArgumentException ignoredInvalidCombination) {
          // Another fixture can prove a constrained dimension with a valid independent change.
        }
      }
    }

    assertTrue(provenSensitive.containsAll(independentlyVariable), provenSensitive.toString());
    assertTrue(
        provenSensitive.containsAll(
            Set.of(
                Dimension.CARET_INDEX_UTF16,
                Dimension.DECLARED_SOURCE_LINE_COUNT,
                Dimension.DECLARED_VISUAL_LINE_COUNT,
                Dimension.DEFERRED_SUFFIX_CODE_POINT_COUNT,
                Dimension.MEASUREMENT_OFFSET_X_PX,
                Dimension.OFFSCREEN_EXTENT_PX,
                Dimension.OFFSCREEN_RATIO,
                Dimension.PARAGRAPH_COUNT,
                Dimension.SELECTION_END_UTF16,
                Dimension.SELECTION_START_UTF16)),
        provenSensitive.toString());
  }

  @Test
  void behaviorChangesCannotShareIdentityWhileLabelsRemainPresentationOnly() {
    JsonObject normal = fixture("normal-text-visible");
    WorkloadIdentity baseline = identity(normal, "Baseline label");
    WorkloadIdentity relabeled = identity(normal, "Presentation-only label");
    assertEquals(baseline, relabeled);
    assertEquals(baseline.hashCode(), relabeled.hashCode());

    for (Map<String, ?> change :
        List.of(
            Map.of("measured-frames", 201),
            Map.of("container-position-x-px", 21),
            Map.of("font-size-px", 17),
            Map.of("synchronization", "none"),
            Map.of("submission-state", "unchanged"),
            Map.of("visibility", "offscreen", "clip-state", "outside"))) {
      WorkloadIdentity changed = identity(normal, "Changed behavior", change, Set.of());
      assertNotEquals(baseline.semanticId(), changed.semanticId(), change.toString());
    }

    for (Map.Entry<String, Map<Dimension, Integer>> currentChange :
        Map.of(
                "findCaretNearBeginning", Map.of(Dimension.CARET_OFFSET_X_PX, 2),
                "findCaretNearEnd", Map.of(Dimension.CARET_OFFSET_INSET_X_PX, 2),
                "layoutTextDenseInlineContent", Map.of(Dimension.INLINE_LAYOUT_START_Y_PX, 1),
                "measureLongSingleFont", Map.of(Dimension.CONTENT_REPEAT_COUNT, 129))
            .entrySet()) {
      WorkloadIdentity current = currentCpuIdentity(currentCpuCase(currentChange.getKey()));
      WorkloadIdentity changed = replaceDimensions(current, currentChange.getValue());
      assertNotEquals(current.semanticId(), changed.semanticId(), currentChange.getKey());
    }

    WorkloadIdentity currentSmall = currentRenderingIdentity(currentRenderingCase("small"));
    WorkloadIdentity differentCompanion =
        replaceDimensions(currentSmall, Map.of(Dimension.COMPANION_TEXT_NODE_COUNT, 2_000));
    assertNotEquals(currentSmall.semanticId(), differentCompanion.semanticId());
    WorkloadIdentity largeMeasuredFirst =
        replaceDimensions(
            currentSmall,
            Map.of(
                Dimension.MEASUREMENT_ORDER, "large-then-small",
                Dimension.MEASUREMENT_ORDER_INDEX, 2));
    assertNotEquals(currentSmall.semanticId(), largeMeasuredFirst.semanticId());
  }

  @Test
  void displayLabelAndObservedOutputsDoNotDefineIdentityOrSeries() {
    JsonObject regression = golden().getAsJsonObject("observedOutputRegression");
    JsonObject fixture = fixture(regression.get("fixtureName").getAsString());
    WorkloadIdentity before = identity(fixture, "Before optimization");
    WorkloadIdentity after = identity(fixture, "After optimization");

    assertNotEquals(regression.getAsJsonObject("before"), regression.getAsJsonObject("after"));
    assertEquals(fixture.get("expectedSemanticId").getAsString(), before.semanticId());
    assertEquals(fixture.get("expectedSemanticId").getAsString(), after.semanticId());
    assertEquals(before.semanticId(), after.semanticId());
    assertEquals(before.seriesId(), after.seriesId());
    assertEquals(before, after);

    ComparabilityMetadata.Environment environment =
        new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.RENDERING,
            "Vendor", "25", "OS", "1", "x64", "CPU",
            "GL vendor", "renderer", "driver", "4.6");
    ComparabilityMetadata.Implementation implementation =
        new ComparabilityMetadata.Implementation("impl", "build", "commit");
    var scene = RenderingWorkloadSpecifications.CURRENT.scene("small");
    ComparabilityMetadata beforeMetadata =
        RenderingWorkloadSpecifications.CURRENT.comparability(
            scene,
            ComparabilityMetadata.EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED,
            environment,
            implementation);
    ComparabilityMetadata afterMetadata =
        RenderingWorkloadSpecifications.CURRENT.comparability(
            scene,
            ComparabilityMetadata.EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED,
            environment,
            implementation);
    assertEquals(beforeMetadata.semanticId(), afterMetadata.semanticId());
    assertEquals(beforeMetadata.fingerprints(), afterMetadata.fingerprints());
  }

  @Test
  void legacyE4SeriesRemainAddressableAndCannotCollideWithE5() {
    JsonArray legacyFixtures = golden().getAsJsonArray("legacyFixtures");
    for (JsonElement element : legacyFixtures) {
      JsonObject fixture = element.getAsJsonObject();
      WorkloadIdentity legacy =
          WorkloadIdentity.legacyE4(
              fixture.get("historicalSeriesKey").getAsString(),
              fixture.get("displayLabel").getAsString());
      assertEquals(fixture.get("expectedSemanticId").getAsString(), legacy.semanticId());
      assertEquals(WorkloadIdentity.Namespace.E4_LEGACY, legacy.namespace());
      assertFalse(legacy.semanticId().contains(":e5:"));
    }

    WorkloadIdentity legacy =
        WorkloadIdentity.legacyE4(
            "rendering:fragments=100;nodes=100;code-points=3800;glyphs=3800;runs=300",
            "E4 scene");
    WorkloadIdentity e5 = identity(fixture("normal-text-visible"), "E5 scene");
    assertNotEquals(legacy.semanticId(), e5.semanticId());
    assertNotEquals(legacy, e5);
  }

  private static Set<Dimension> commonCpuDimensions() {
    return Set.of(
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
        Dimension.FIXTURE_PREPARATION_POLICY,
        Dimension.FORKS,
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
  }

  private static void assertCpuBenchmarkAnnotationsAndDispatchAreAligned(
      Class<?> benchmarkClass, WorkloadIdentity identity) {
    assertJmhClassAnnotationContract(benchmarkClass, identity);
    assertJmhMethodContract(benchmarkClass);
    Map<String, java.lang.reflect.Method> benchmarkMethods =
        jmhMethodsInHierarchy(benchmarkClass, Benchmark.class).stream()
            .collect(Collectors.toMap(java.lang.reflect.Method::getName, method -> method));
    assertEquals(CpuWorkloadSpecifications.currentOperations().keySet(), benchmarkMethods.keySet());
    benchmarkMethods.forEach(
        (methodName, method) -> {
          assertEffectiveBenchmarkAnnotations(benchmarkClass, method, identity);
          assertEquals(0, method.getParameterCount(), methodName);
          var dispatch =
              CpuWorkloadSpecifications.dispatchForBenchmark(
                  benchmarkClass.getName() + "." + methodName);
          assertSame(CpuWorkloadSpecifications.currentOperations().get(methodName), dispatch.specification());
          OperationSpec specification = dispatch.specification();
          if (specification instanceof CpuWorkloadSpecifications.MeasurementSpec) {
            assertEquals("TextMetrics", method.getReturnType().getSimpleName(), methodName);
            assertSame(specification, dispatch.measurement());
          } else if (specification instanceof CpuWorkloadSpecifications.CaretSpec) {
            assertEquals("TextCaretMetrics", method.getReturnType().getSimpleName(), methodName);
            assertSame(specification, dispatch.caret());
          } else {
            assertEquals(float.class, method.getReturnType(), methodName);
            assertSame(specification, dispatch.inlineLayout());
          }
        });
    assertThrows(
        IllegalArgumentException.class,
        () -> CpuWorkloadSpecifications.dispatchForBenchmark(benchmarkClass.getName() + ".unknown"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CpuWorkloadSpecifications.BenchmarkDispatch(
                "measureLatin", CpuWorkloadSpecifications.MEASURE_MIXED_CJK));
  }

  private static void assertJmhClassAnnotationContract(
      Class<?> benchmarkClass, WorkloadIdentity identity) {
    Set<Class<? extends Annotation>> supported =
        Set.of(
            BenchmarkMode.class,
            OutputTimeUnit.class,
            State.class,
            Threads.class,
            Fork.class,
            Warmup.class,
            Measurement.class);
    Set<Class<? extends Annotation>> effective =
        supported.stream()
            .filter(type -> closestClassAnnotation(benchmarkClass, type) != null)
            .collect(Collectors.toSet());
    Set<Class<? extends Annotation>> unsupported =
        jmhClassAnnotationsInHierarchy(benchmarkClass).stream()
            .filter(type -> !supported.contains(type))
            .collect(Collectors.toSet());
    if (!effective.equals(supported) || !unsupported.isEmpty()) {
      throw new IllegalArgumentException(
          "Unsupported or incomplete effective class-level JMH annotation contract: effective="
              + effective
              + "; unsupported="
              + unsupported);
    }

    BenchmarkMode benchmarkMode = closestClassAnnotation(benchmarkClass, BenchmarkMode.class);
    if (benchmarkMode.value().length != 1) {
      throw new IllegalArgumentException(
          "@BenchmarkMode requires exactly one identity-aligned mode");
    }
    requireAnnotationDimension(
        identity, Dimension.BENCHMARK_MODE, benchmarkMode.value()[0], "@BenchmarkMode");
    requireAnnotationDimension(
        identity,
        Dimension.OUTPUT_TIME_UNIT,
        closestClassAnnotation(benchmarkClass, OutputTimeUnit.class).value(),
        "@OutputTimeUnit");
    requireAnnotationDimension(
        identity,
        Dimension.STATE_SCOPE,
        closestClassAnnotation(benchmarkClass, State.class).value(),
        "@State");
    requireAnnotationDimension(
        identity,
        Dimension.THREADS,
        closestClassAnnotation(benchmarkClass, Threads.class).value(),
        "@Threads");

    Fork fork = closestClassAnnotation(benchmarkClass, Fork.class);
    requireAnnotationDimension(identity, Dimension.FORKS, fork.value(), "@Fork value");
    requireAnnotationDimension(
        identity, Dimension.WARMUP_FORKS, fork.warmups(), "@Fork warmups");
    requireSupportedForkJvmShape(fork, "@Fork");

    Warmup warmup = closestClassAnnotation(benchmarkClass, Warmup.class);
    requireAnnotationDimension(
        identity, Dimension.WARMUP_ITERATIONS, warmup.iterations(), "@Warmup iterations");
    requireAnnotationDimension(
        identity,
        Dimension.WARMUP_TIME,
        annotationDuration(warmup.time(), warmup.timeUnit()),
        "@Warmup time");
    requireAnnotationDimension(
        identity, Dimension.WARMUP_BATCH_SIZE, warmup.batchSize(), "@Warmup batchSize");

    Measurement measurement = closestClassAnnotation(benchmarkClass, Measurement.class);
    requireAnnotationDimension(
        identity,
        Dimension.MEASUREMENT_ITERATIONS,
        measurement.iterations(),
        "@Measurement iterations");
    requireAnnotationDimension(
        identity,
        Dimension.MEASUREMENT_TIME,
        annotationDuration(measurement.time(), measurement.timeUnit()),
        "@Measurement time");
    requireAnnotationDimension(
        identity,
        Dimension.MEASUREMENT_BATCH_SIZE,
        measurement.batchSize(),
        "@Measurement batchSize");
  }

  private static void assertEffectiveBenchmarkAnnotations(
      Class<?> benchmarkClass,
      java.lang.reflect.Method benchmarkMethod,
      WorkloadIdentity identity) {
    BenchmarkMode benchmarkMode =
        closestMethodAnnotation(benchmarkMethod, benchmarkClass, BenchmarkMode.class);
    if (benchmarkMode.value().length != 1) {
      throw new IllegalArgumentException(
          "Effective @BenchmarkMode requires exactly one identity-aligned mode");
    }
    requireAnnotationDimension(
        identity, Dimension.BENCHMARK_MODE, benchmarkMode.value()[0], "effective @BenchmarkMode");
    requireAnnotationDimension(
        identity,
        Dimension.OUTPUT_TIME_UNIT,
        closestMethodAnnotation(benchmarkMethod, benchmarkClass, OutputTimeUnit.class).value(),
        "effective @OutputTimeUnit");
    requireAnnotationDimension(
        identity,
        Dimension.THREADS,
        closestMethodAnnotation(benchmarkMethod, benchmarkClass, Threads.class).value(),
        "effective @Threads");

    Fork fork = closestMethodAnnotation(benchmarkMethod, benchmarkClass, Fork.class);
    requireAnnotationDimension(identity, Dimension.FORKS, fork.value(), "effective @Fork value");
    requireAnnotationDimension(
        identity, Dimension.WARMUP_FORKS, fork.warmups(), "effective @Fork warmups");
    requireSupportedForkJvmShape(fork, "effective @Fork");

    Warmup warmup = closestMethodAnnotation(benchmarkMethod, benchmarkClass, Warmup.class);
    requireAnnotationDimension(
        identity,
        Dimension.WARMUP_ITERATIONS,
        warmup.iterations(),
        "effective @Warmup iterations");
    requireAnnotationDimension(
        identity,
        Dimension.WARMUP_TIME,
        annotationDuration(warmup.time(), warmup.timeUnit()),
        "effective @Warmup time");
    requireAnnotationDimension(
        identity,
        Dimension.WARMUP_BATCH_SIZE,
        warmup.batchSize(),
        "effective @Warmup batchSize");

    Measurement measurement =
        closestMethodAnnotation(benchmarkMethod, benchmarkClass, Measurement.class);
    requireAnnotationDimension(
        identity,
        Dimension.MEASUREMENT_ITERATIONS,
        measurement.iterations(),
        "effective @Measurement iterations");
    requireAnnotationDimension(
        identity,
        Dimension.MEASUREMENT_TIME,
        annotationDuration(measurement.time(), measurement.timeUnit()),
        "effective @Measurement time");
    requireAnnotationDimension(
        identity,
        Dimension.MEASUREMENT_BATCH_SIZE,
        measurement.batchSize(),
        "effective @Measurement batchSize");
  }

  private static <T extends Annotation> T closestMethodAnnotation(
      java.lang.reflect.Method method, Class<?> benchmarkClass, Class<T> annotationType) {
    T methodAnnotation = method.getDeclaredAnnotation(annotationType);
    return methodAnnotation != null
        ? methodAnnotation
        : closestClassAnnotation(benchmarkClass, annotationType);
  }

  private static <T extends Annotation> T closestClassAnnotation(
      Class<?> benchmarkClass, Class<T> annotationType) {
    for (Class<?> current = benchmarkClass; current != null; current = current.getSuperclass()) {
      T annotation = current.getDeclaredAnnotation(annotationType);
      if (annotation != null) {
        return annotation;
      }
    }
    return null;
  }

  private static Set<Class<? extends Annotation>> jmhClassAnnotationsInHierarchy(
      Class<?> benchmarkClass) {
    Set<Class<? extends Annotation>> annotations = new HashSet<>();
    for (Class<?> current = benchmarkClass; current != null; current = current.getSuperclass()) {
      java.util.Arrays.stream(current.getDeclaredAnnotations())
          .map(Annotation::annotationType)
          .filter(type -> type.getPackageName().equals("org.openjdk.jmh.annotations"))
          .forEach(annotations::add);
    }
    return Set.copyOf(annotations);
  }

  private static void requireSupportedForkJvmShape(Fork fork, String source) {
    if (!fork.jvm().equals(Fork.BLANK_ARGS)
        || java.util.Arrays.stream(fork.jvmArgs())
            .anyMatch(argument -> !argument.equals(Fork.BLANK_ARGS))
        || java.util.Arrays.stream(fork.jvmArgsPrepend())
            .anyMatch(argument -> !argument.equals(Fork.BLANK_ARGS))
        || java.util.Arrays.stream(fork.jvmArgsAppend())
            .anyMatch(argument -> !argument.equals(Fork.BLANK_ARGS))) {
      throw new IllegalArgumentException(
          source + " JVM settings are unsupported; jmhCpu native access is aligned separately");
    }
  }

  private static Duration annotationDuration(int time, TimeUnit timeUnit) {
    return Duration.ofNanos(timeUnit.toNanos(time));
  }

  private static void requireAnnotationDimension(
      WorkloadIdentity identity, Dimension dimension, Object value, String annotation) {
    String canonical = dimension.canonicalValue(value);
    if (!canonical.equals(identity.dimensions().get(dimension))) {
      throw new IllegalArgumentException(
          annotation + " is not aligned with identity dimension " + dimension.key());
    }
  }

  private static void assertJmhMethodContract(Class<?> benchmarkClass) {
    for (Class<?> current = benchmarkClass; current != null; current = current.getSuperclass()) {
      for (Field field : current.getDeclaredFields()) {
        if (field.isAnnotationPresent(Param.class)) {
          throw new IllegalArgumentException("JMH @Param field is not identity-aligned: " + field);
        }
      }
      for (java.lang.reflect.Method method : current.getDeclaredMethods()) {
        boolean benchmark = method.isAnnotationPresent(Benchmark.class);
        boolean setup = method.isAnnotationPresent(Setup.class);
        if (current != benchmarkClass && benchmark) {
          throw new IllegalArgumentException(
              "Inherited @Benchmark method is not identity/dispatch-aligned: " + method);
        }
        if (current != benchmarkClass && setup) {
          throw new IllegalArgumentException(
              "Inherited @Setup method is not identity/fixture-aligned: " + method);
        }

        Set<Class<? extends Annotation>> jmhAnnotations =
            java.util.Arrays.stream(method.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .filter(type -> type.getPackageName().equals("org.openjdk.jmh.annotations"))
                .collect(Collectors.toSet());
        Set<Class<? extends Annotation>> expected;
        if (benchmark) {
          expected = Set.of(Benchmark.class);
        } else if (setup) {
          expected = Set.of(Setup.class);
          if (method.getAnnotation(Setup.class).value() != Level.Trial) {
            throw new IllegalArgumentException("Only @Setup(Level.Trial) is identity-aligned");
          }
        } else {
          expected = Set.of();
        }
        if (!jmhAnnotations.equals(expected)) {
          throw new IllegalArgumentException(
              "Method-level JMH override is not identity-aligned: "
                  + method
                  + " "
                  + jmhAnnotations);
        }
      }
    }
  }

  private static <T extends Annotation> List<java.lang.reflect.Method> jmhMethodsInHierarchy(
      Class<?> benchmarkClass, Class<T> annotationType) {
    List<java.lang.reflect.Method> methods = new ArrayList<>();
    for (Class<?> current = benchmarkClass; current != null; current = current.getSuperclass()) {
      java.util.Arrays.stream(current.getDeclaredMethods())
          .filter(method -> method.isAnnotationPresent(annotationType))
          .forEach(methods::add);
    }
    return List.copyOf(methods);
  }

  private static Set<Dimension> requiredDimensions(JsonObject fixture) {
    Category category = Category.fromCanonicalValue(dimension(fixture, "category"));
    return WorkloadIdentity.requiredDimensions(category, dimension(fixture, "operation"));
  }

  private static WorkloadIdentity currentCpuIdentity(JsonObject currentCase) {
    JsonObject base = fixture("cpu-wrapped-paragraph").getAsJsonObject("dimensions");
    JsonObject overrides = currentCase.getAsJsonObject("dimensions");
    String operation = currentCase.get("operation").getAsString();
    WorkloadIdentity.Builder builder = WorkloadIdentity.e5("cpu-text");
    for (Dimension dimension : WorkloadIdentity.requiredDimensions(Category.CPU, operation)) {
      JsonElement value =
          overrides.has(dimension.key())
              ? overrides.get(dimension.key())
              : base.get(dimension.key());
      if (value == null) {
        throw new IllegalArgumentException(
            "Missing current CPU inventory value for " + operation + ": " + dimension.key());
      }
      builder.dimension(dimension, value(value));
    }
    return builder.build("Current E4 " + operation);
  }

  private static WorkloadIdentity currentCpuIdentity(OperationSpec specification) {
    JsonObject base = fixture("cpu-wrapped-paragraph").getAsJsonObject("dimensions");
    Map<Dimension, Object> declared = specification.identityDimensions();
    WorkloadIdentity.Builder builder = WorkloadIdentity.e5("cpu-text");
    for (Dimension dimension :
        WorkloadIdentity.requiredDimensions(Category.CPU, specification.operation())) {
      Object value = declared.get(dimension);
      if (value == null) {
        JsonElement common = base.get(dimension.key());
        if (common == null) {
          throw new IllegalArgumentException(
              "Missing CPU setting for " + specification.operation() + ": " + dimension.key());
        }
        value = value(common);
      }
      builder.dimension(dimension, value);
    }
    return builder.build("Current CPU specification " + specification.operation());
  }

  private static WorkloadIdentity currentRenderingIdentity(JsonObject currentCase) {
    Map<String, Object> overrides = new java.util.LinkedHashMap<>();
    currentCase
        .getAsJsonObject("dimensions")
        .entrySet()
        .forEach(entry -> overrides.put(entry.getKey(), value(entry.getValue())));
    return identity(
        fixture("normal-text-visible"),
        "Current E4 rendering " + currentCase.get("name").getAsString(),
        overrides,
        Set.of());
  }

  private static WorkloadIdentity currentRenderingIdentity(
      Specification specification, SceneSpecification scene) {
    Map<String, Object> overrides = new java.util.LinkedHashMap<>();
    specification
        .identityDimensions(scene)
        .forEach((dimension, value) -> overrides.put(dimension.key(), value));
    return identity(
        fixture("normal-text-visible"),
        "Current renderer specification " + scene.name(),
        overrides,
        Set.of());
  }

  private static WorkloadIdentity rebuild(
      WorkloadIdentity source, Set<Dimension> omitted, Map<Dimension, ?> additions) {
    WorkloadIdentity.Builder builder = WorkloadIdentity.e5(source.workload());
    source.dimensions().forEach(
        (dimension, value) -> {
          if (!omitted.contains(dimension)) {
            builder.dimension(dimension, value);
          }
        });
    additions.forEach(builder::dimension);
    return builder.build("Rebuilt identity");
  }

  private static WorkloadIdentity replaceDimensions(
      WorkloadIdentity source, Map<Dimension, ?> replacements) {
    WorkloadIdentity.Builder builder = WorkloadIdentity.e5(source.workload());
    source
        .dimensions()
        .forEach(
            (dimension, value) ->
                builder.dimension(
                    dimension,
                    replacements.containsKey(dimension) ? replacements.get(dimension) : value));
    return builder.build("Changed declared inputs");
  }

  private static TextStyleSpecification copyStyle(
      TextStyleSpecification source,
      List<Font> orderedFonts,
      float fontSizePx,
      FontStretch effectiveStretch) {
    return new TextStyleSpecification(
        orderedFonts,
        source.fontStyle(),
        source.fontWeight(),
        effectiveStretch,
        fontSizePx,
        source.lineHeight(),
        source.color(),
        source.display(),
        source.position(),
        source.whiteSpace(),
        source.textAlign(),
        source.overflowWrap(),
        source.wordBreak(),
        source.tabSize());
  }

  private static TextStyleSpecification driftInlineStyle(
      TextStyleSpecification source, InlineStyleDrift drift) {
    return new TextStyleSpecification(
        drift == InlineStyleDrift.ORDERED_FONTS
            ? List.of(Font.NOTO_SANS_CJK_SC_REGULAR)
            : source.orderedFonts(),
        drift == InlineStyleDrift.FONT_STYLE ? FontStyle.ITALIC : source.fontStyle(),
        drift == InlineStyleDrift.FONT_WEIGHT ? FontWeight.BOLD : source.fontWeight(),
        drift == InlineStyleDrift.EFFECTIVE_STRETCH
            ? FontStretch.CONDENSED
            : source.effectiveFontStretch(),
        drift == InlineStyleDrift.FONT_SIZE ? source.fontSizePx() + 1 : source.fontSizePx(),
        drift == InlineStyleDrift.LINE_HEIGHT ? source.lineHeight() + 0.1f : source.lineHeight(),
        drift == InlineStyleDrift.COLOR ? Color.WHITE : source.color(),
        drift == InlineStyleDrift.DISPLAY ? Display.INLINE : source.display(),
        drift == InlineStyleDrift.POSITION ? Position.ABSOLUTE : source.position(),
        drift == InlineStyleDrift.WHITE_SPACE ? WhiteSpace.PRE : source.whiteSpace(),
        drift == InlineStyleDrift.TEXT_ALIGN ? TextAlign.RIGHT : source.textAlign(),
        drift == InlineStyleDrift.OVERFLOW_WRAP ? OverflowWrap.BREAK_WORD : source.overflowWrap(),
        drift == InlineStyleDrift.WORD_BREAK ? WordBreak.BREAK_ALL : source.wordBreak(),
        drift == InlineStyleDrift.TAB_SIZE ? source.tabSize() + 1 : source.tabSize());
  }

  private static CpuWorkloadSpecifications.InlineLayoutSpec copyInline(
      CpuWorkloadSpecifications.InlineLayoutSpec source,
      String workloadContent,
      String text,
      int textNodeCount,
      float containerWidthPx,
      float containerHeightPx,
      float layoutStartYPx,
      TextStyleSpecification style) {
    return new CpuWorkloadSpecifications.InlineLayoutSpec(
        source.operation(),
        workloadContent,
        text,
        textNodeCount,
        containerWidthPx,
        containerHeightPx,
        layoutStartYPx,
        style);
  }

  private static MeasurementSpec copyMeasurement(
      MeasurementSpec source,
      List<Font> orderedFonts,
      float fontSizePx,
      float lineHeight,
      float measurementOffsetXPx,
      Float maximumWidthPx,
      boolean wordWrap,
      int contentRepeatCount) {
    return new MeasurementSpec(
        source.operation(),
        source.workloadContent(),
        source.text(),
        source.api(),
        orderedFonts,
        fontSizePx,
        lineHeight,
        measurementOffsetXPx,
        maximumWidthPx,
        wordWrap,
        contentRepeatCount);
  }

  private static void assertTrialWarmupRejected(int index, MeasurementSpec changed) {
    List<MeasurementSpec> warmups =
        new ArrayList<>(CpuWorkloadSpecifications.TRIAL_SETUP.fontWarmups());
    warmups.set(index, changed);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CpuWorkloadSpecifications.TrialSetupSpec(
                CpuWorkloadSpecifications.TRIAL_SETUP.roundToPixel(),
                warmups,
                CpuWorkloadSpecifications.TRIAL_SETUP.preparedEndCaret(),
                CpuWorkloadSpecifications.TRIAL_SETUP.preparedInlineLayout(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fixturePreparationPolicy(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fontFixturePolicy(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fontResolver()));
  }

  private static void assertTrialSetupRejected(
      boolean roundToPixel,
      String fixturePreparationPolicy,
      String fontFixturePolicy,
      String fontResolver) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CpuWorkloadSpecifications.TrialSetupSpec(
                roundToPixel,
                CpuWorkloadSpecifications.TRIAL_SETUP.fontWarmups(),
                CpuWorkloadSpecifications.TRIAL_SETUP.preparedEndCaret(),
                CpuWorkloadSpecifications.TRIAL_SETUP.preparedInlineLayout(),
                fixturePreparationPolicy,
                fontFixturePolicy,
                fontResolver));
  }

  private static void assertTrialPreparationRejects(
      CpuWorkloadSpecifications.CaretSpec caret,
      CpuWorkloadSpecifications.InlineLayoutSpec inline) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CpuWorkloadSpecifications.TrialSetupSpec(
                CpuWorkloadSpecifications.TRIAL_SETUP.roundToPixel(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fontWarmups(),
                caret,
                inline,
                CpuWorkloadSpecifications.TRIAL_SETUP.fixturePreparationPolicy(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fontFixturePolicy(),
                CpuWorkloadSpecifications.TRIAL_SETUP.fontResolver()));
  }

  private static void assertIdentityChangesOrFailsClosed(
      WorkloadIdentity baseline, Supplier<WorkloadIdentity> changedIdentity) {
    try {
      assertNotEquals(baseline.semanticId(), changedIdentity.get().semanticId());
    } catch (IllegalArgumentException expectedFailClosed) {
      assertTrue(expectedFailClosed.getMessage() != null);
    }
  }

  private static Specification copySpecification(
      Specification source,
      ContainerSpecification container,
      TextStyleSpecification style,
      List<Font> prewarmFonts,
      List<String> sourceContent,
      String workloadContent,
      List<SceneSpecification> measurementOrder) {
    return new Specification(
        source.window(),
        container,
        style,
        prewarmFonts,
        source.prewarmText(),
        source.prewarmWorkloadContent(),
        sourceContent,
        workloadContent,
        source.contentTransform(),
        measurementOrder,
        source.inlineLayoutStartYPx(),
        source.warmupFrames(),
        source.measuredFrames(),
        source.roundToPixel(),
        source.clear(),
        source.synchronizeWithGlFinish(),
        source.structuralValidation());
  }

  private static Specification copySpecificationWithPrewarm(
      Specification source, String prewarmText, String prewarmWorkloadContent) {
    return new Specification(
        source.window(),
        source.container(),
        source.style(),
        source.prewarmFonts(),
        prewarmText,
        prewarmWorkloadContent,
        source.sourceContent(),
        source.workloadContent(),
        source.contentTransform(),
        source.measurementOrder(),
        source.inlineLayoutStartYPx(),
        source.warmupFrames(),
        source.measuredFrames(),
        source.roundToPixel(),
        source.clear(),
        source.synchronizeWithGlFinish(),
        source.structuralValidation());
  }

  private static Specification copySpecificationWithBehavior(
      Specification source,
      ClearSpecification clear,
      StructuralValidationSpecification validation) {
    return new Specification(
        source.window(),
        source.container(),
        source.style(),
        source.prewarmFonts(),
        source.prewarmText(),
        source.prewarmWorkloadContent(),
        source.sourceContent(),
        source.workloadContent(),
        source.contentTransform(),
        source.measurementOrder(),
        source.inlineLayoutStartYPx(),
        source.warmupFrames(),
        source.measuredFrames(),
        source.roundToPixel(),
        clear,
        source.synchronizeWithGlFinish(),
        validation);
  }

  private static JsonObject currentCpuCase(String operation) {
    for (JsonElement element : golden().getAsJsonArray("currentE4CpuCases")) {
      JsonObject currentCase = element.getAsJsonObject();
      if (currentCase.get("operation").getAsString().equals(operation)) {
        return currentCase;
      }
    }
    throw new IllegalArgumentException("Missing current CPU case: " + operation);
  }

  private static JsonObject currentRenderingCase(String name) {
    for (JsonElement element : golden().getAsJsonArray("currentE4RenderingCases")) {
      JsonObject currentCase = element.getAsJsonObject();
      if (currentCase.get("name").getAsString().equals(name)) {
        return currentCase;
      }
    }
    throw new IllegalArgumentException("Missing current rendering case: " + name);
  }

  private static List<String> javaExecTaskArguments(
      String source, String taskName, String invocationName) {
    List<KotlinToken> tokens = kotlinTokens(source);
    int bodyStart = -1;
    for (int index = 0; index + 10 < tokens.size(); index++) {
      if (tokens.get(index).isIdentifier("tasks")
          && tokens.get(index + 1).isSymbol(".")
          && tokens.get(index + 2).isIdentifier("register")
          && tokens.get(index + 3).isSymbol("<")
          && tokens.get(index + 4).isIdentifier("JavaExec")
          && tokens.get(index + 5).isSymbol(">")
          && tokens.get(index + 6).isSymbol("(")
          && tokens.get(index + 7).isString(taskName)
          && tokens.get(index + 8).isSymbol(")")
          && tokens.get(index + 9).isSymbol("{")) {
        bodyStart = index + 9;
        break;
      }
    }
    if (bodyStart < 0) {
      throw new IllegalArgumentException("Unable to parse JavaExec task: " + taskName);
    }

    List<String> arguments = new ArrayList<>();
    String[] invocationPath = invocationName.split("\\.", -1);
    if (invocationPath.length < 1 || invocationPath.length > 2) {
      throw new IllegalArgumentException("Unsupported Kotlin invocation path: " + invocationName);
    }
    int invocationCount = 0;
    int braceDepth = 1;
    for (int index = bodyStart + 1; index < tokens.size() && braceDepth > 0; index++) {
      KotlinToken token = tokens.get(index);
      if (token.isSymbol("{")) {
        braceDepth++;
        continue;
      }
      if (token.isSymbol("}")) {
        braceDepth--;
        continue;
      }
      int openingParenthesis = -1;
      if (braceDepth == 1 && invocationPath.length == 1
          && token.isIdentifier(invocationPath[0])
          && index + 1 < tokens.size()
          && tokens.get(index + 1).isSymbol("(")
          && (index == 0 || !tokens.get(index - 1).isSymbol("."))) {
        openingParenthesis = index + 1;
      } else if (braceDepth == 1 && invocationPath.length == 2
          && token.isIdentifier(invocationPath[0])
          && index + 3 < tokens.size()
          && tokens.get(index + 1).isSymbol(".")
          && tokens.get(index + 2).isIdentifier(invocationPath[1])
          && tokens.get(index + 3).isSymbol("(")) {
        openingParenthesis = index + 3;
      }
      if (openingParenthesis < 0) {
        continue;
      }
      invocationCount++;
      int parenthesisDepth = 1;
      for (index = openingParenthesis + 1;
          index < tokens.size() && parenthesisDepth > 0;
          index++) {
        KotlinToken argument = tokens.get(index);
        if (argument.isSymbol("(")) {
          parenthesisDepth++;
        } else if (argument.isSymbol(")")) {
          parenthesisDepth--;
        } else if (parenthesisDepth == 1 && argument.type() == KotlinTokenType.STRING) {
          arguments.add(argument.value());
        } else if (parenthesisDepth == 1 && !argument.isSymbol(",")) {
          throw new IllegalArgumentException(
              taskName + "." + invocationName + " must use literal string arguments");
        }
      }
      index--;
    }
    if (invocationCount != 1) {
      throw new IllegalArgumentException(
          taskName + " must declare exactly one direct " + invocationName + "(...) call");
    }
    return List.copyOf(arguments);
  }

  private static List<KotlinToken> kotlinTokens(String source) {
    List<KotlinToken> tokens = new ArrayList<>();
    for (int index = 0; index < source.length(); ) {
      char current = source.charAt(index);
      if (Character.isWhitespace(current)) {
        index++;
      } else if (current == '/' && index + 1 < source.length()
          && source.charAt(index + 1) == '/') {
        index += 2;
        while (index < source.length() && source.charAt(index) != '\n') {
          index++;
        }
      } else if (current == '/' && index + 1 < source.length()
          && source.charAt(index + 1) == '*') {
        int end = source.indexOf("*/", index + 2);
        if (end < 0) {
          throw new IllegalArgumentException("Unclosed Kotlin block comment");
        }
        index = end + 2;
      } else if (current == '"') {
        StringBuilder value = new StringBuilder();
        index++;
        boolean closed = false;
        while (index < source.length()) {
          char character = source.charAt(index++);
          if (character == '"') {
            closed = true;
            break;
          }
          if (character == '\\') {
            if (index >= source.length()) {
              throw new IllegalArgumentException("Unclosed Kotlin string escape");
            }
            char escaped = source.charAt(index++);
            value.append(
                switch (escaped) {
                  case 'n' -> '\n';
                  case 'r' -> '\r';
                  case 't' -> '\t';
                  case '"' -> '"';
                  case '\\' -> '\\';
                  default -> escaped;
                });
          } else {
            value.append(character);
          }
        }
        if (!closed) {
          throw new IllegalArgumentException("Unclosed Kotlin string");
        }
        tokens.add(new KotlinToken(KotlinTokenType.STRING, value.toString()));
      } else if (Character.isJavaIdentifierStart(current)) {
        int start = index++;
        while (index < source.length() && Character.isJavaIdentifierPart(source.charAt(index))) {
          index++;
        }
        tokens.add(new KotlinToken(KotlinTokenType.IDENTIFIER, source.substring(start, index)));
      } else {
        tokens.add(new KotlinToken(KotlinTokenType.SYMBOL, Character.toString(current)));
        index++;
      }
    }
    return List.copyOf(tokens);
  }

  private static String option(List<String> arguments, String name) {
    int index = arguments.indexOf(name);
    if (index < 0 || index + 1 >= arguments.size()) {
      throw new IllegalArgumentException("Missing JMH option value: " + name);
    }
    if (arguments.lastIndexOf(name) != index) {
      throw new IllegalArgumentException("Duplicate JMH option: " + name);
    }
    return arguments.get(index + 1);
  }

  private static void assertOptionDimension(
      List<String> arguments,
      String option,
      WorkloadIdentity identity,
      Dimension dimension) {
    assertDimension(identity, dimension, option(arguments, option));
  }

  private static Map<String, ?> equivalentRepresentations(JsonObject fixture) {
    Map<String, Object> equivalents = new java.util.LinkedHashMap<>();
    for (Map.Entry<String, JsonElement> entry : fixture.getAsJsonObject("dimensions").entrySet()) {
      Dimension dimension = Dimension.fromKey(entry.getKey());
      JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
      Object equivalent;
      if (primitive.isNumber()) {
        BigDecimal number = primitive.getAsBigDecimal();
        equivalent = number.toPlainString() + (number.scale() <= 0 ? ".0" : "0");
      } else if (primitive.isBoolean()) {
        equivalent = primitive.getAsBoolean() ? "TRUE" : "FALSE";
      } else {
        String current = primitive.getAsString();
        equivalent =
            switch (dimension) {
              case BENCHMARK_CLASS, OPERATION, PREWARM_WORKLOAD_CONTENT, WORKLOAD_CONTENT -> current;
              case CATEGORY ->
                  Category.valueOf(
                      current.toUpperCase(java.util.Locale.ROOT).replace('-', '_'));
              case FONT_CHAIN -> List.of(current.split(",", -1));
              case MEASUREMENT_TIME, WARMUP_TIME -> Duration.ofMillis(500);
              case OUTPUT_TIME_UNIT -> TimeUnit.valueOf(current.toUpperCase(java.util.Locale.ROOT));
              case STATE_SCOPE ->
                  switch (current) {
                    case "benchmark" -> Scope.Benchmark;
                    case "thread" -> Scope.Thread;
                    case "group" -> Scope.Group;
                    default -> throw new IllegalArgumentException("Unknown state scope: " + current);
                  };
              case SUBMISSION_STATE ->
                  WorkloadIdentity.SubmissionState.valueOf(
                      current.toUpperCase(java.util.Locale.ROOT).replace('-', '_'));
              case VISIBILITY ->
                  WorkloadIdentity.Visibility.valueOf(
                      current.toUpperCase(java.util.Locale.ROOT).replace('-', '_'));
              default -> current.toUpperCase(java.util.Locale.ROOT).replace('-', '_');
            };
      }
      equivalents.put(entry.getKey(), equivalent);
    }
    return equivalents;
  }

  private static Object sensitivityValue(Dimension dimension, JsonElement currentElement) {
    JsonPrimitive current = currentElement.getAsJsonPrimitive();
    if (current.isNumber()) {
      if (dimension == Dimension.MEASUREMENT_ORDER_INDEX
          || dimension == Dimension.SCENE_PAIR_COUNT) {
        return null;
      }
      BigDecimal number = current.getAsBigDecimal();
      if (dimension == Dimension.OFFSCREEN_RATIO) {
        return number.compareTo(BigDecimal.ONE) == 0 ? new BigDecimal("0.5") : BigDecimal.ONE;
      }
      return number.add(BigDecimal.ONE);
    }
    if (current.isBoolean()) {
      return !current.getAsBoolean();
    }
    String value = current.getAsString();
    return switch (dimension) {
      case API,
          BENCHMARK_CLASS,
          CATEGORY,
          COMPANION_SCENE_SHAPE,
          CONTROL_TYPE,
          DISPLAY,
          FIXTURE_PREPARATION_POLICY,
          FONT_FIXTURE_POLICY,
          FONT_RESOLVER,
          FONT_STRETCH,
          HARNESS,
          NATIVE_ACCESS,
          OPERATION,
          POSITION,
          RENDERER_PATH,
          SETUP_LEVEL,
          MEASUREMENT_ORDER,
          WRAP_WIDTH_POLICY -> null;
      case BENCHMARK_MODE -> "throughput";
      case CARET_OFFSET_POLICY ->
          value.equals("fixed") ? "measured-width-minus-inset" : "fixed";
      case CARET_STATE -> value.equals("visible") ? "hidden" : "visible";
      case CLEAR_POLICY -> value.equals("none") ? "color-stencil-before-sample" : "none";
      case CLIP_STATE -> value.equals("mixed") ? "inside" : "mixed";
      case COLOR -> value.equals("black") ? "white" : "black";
      case CONTENT_ALTERNATION -> value.equals("none") ? "latin-mixed-cjk" : "none";
      case CONTENT_TRANSFORM -> value.equals("none") ? "remove-ascii-spaces" : "none";
      case CONTEXT_VISIBILITY -> value.equals("hidden") ? "visible" : "hidden";
      case CONTROL_STATE -> value.equals("focused") ? "unfocused" : "focused";
      case FONT_CHAIN ->
          value
              + (value.contains("prewarm=")
                  ? ",layout=Additional Font|normal|normal|regular|fonts/Additional.ttf"
                  : ",Additional Font|normal|normal|regular|fonts/Additional.ttf");
      case FONT_STYLE -> value.equals("normal") ? "italic" : "normal";
      case FONT_WEIGHT -> value.equals("bold") ? "regular" : "bold";
      case MEASUREMENT_TIME, WARMUP_TIME -> value.equals("1s") ? "2s" : "1s";
      case OUTPUT_TIME_UNIT -> value.equals("milliseconds") ? "microseconds" : "milliseconds";
      case OVERFLOW_WRAP -> value.equals("normal") ? "break-word" : "normal";
      case PREMEASURE_SEQUENCE -> value.equals("none") ? "per-scene" : "none";
      case PROFILER -> value.equals("gc") ? "none" : "gc";
      case STATE_SCOPE -> value.equals("benchmark") ? "thread" : "benchmark";
      case SUBMISSION_STATE -> value.equals("changed") ? "unchanged" : "changed";
      case SYNCHRONIZATION -> value.equals("gl-finish") ? "none" : "gl-finish";
      case TEXT_ALIGN -> value.equals("left") ? "right" : "left";
      case VALIDATION_POLICY ->
          value.equals("none")
              ? "small-scene-production-command-recording-before-measurement"
              : "none";
      case VISIBILITY -> value.equals("mixed") ? "visible" : "mixed";
      case WHITE_SPACE -> value.equals("normal") ? "pre" : "normal";
      case WORD_BREAK -> value.equals("normal") ? "break-all" : "normal";
      case WARMUP_ORDER ->
          value.equals("per-scene") ? "alternating-small-large-starting-small" : "per-scene";
      case PREWARM_WORKLOAD_CONTENT, WORKLOAD_CONTENT -> value + "-changed";
      case WRAPPING_POLICY -> value.equals("normal") ? "soft-wrap" : "normal";
      default -> null;
    };
  }

  private static void assertDimension(
      WorkloadIdentity identity, Dimension dimension, Object sourceValue) {
    assertEquals(dimension.canonicalValue(sourceValue), identity.dimensions().get(dimension));
  }

  private static Path repositoryRoot() {
    Path candidate = Path.of("").toAbsolutePath();
    while (candidate != null) {
      if (Files.exists(candidate.resolve("spinygui.benchmark/build.gradle.kts"))) {
        return candidate;
      }
      candidate = candidate.getParent();
    }
    throw new IllegalStateException("Unable to locate repository root");
  }

  private static WorkloadIdentity identity(JsonObject fixture, String displayLabel) {
    return identity(fixture, displayLabel, Map.of(), Set.of());
  }

  private static WorkloadIdentity identity(
      JsonObject fixture,
      String displayLabel,
      Map<String, ?> overrides,
      Set<String> omitted) {
    return identity(
        fixture,
        displayLabel,
        overrides,
        omitted,
        fixture.get("workload").getAsString());
  }

  private static WorkloadIdentity identity(
      JsonObject fixture,
      String displayLabel,
      Map<String, ?> overrides,
      Set<String> omitted,
      String workload) {
    WorkloadIdentity.Builder builder = WorkloadIdentity.e5(workload);
    JsonObject fixtureDimensions = fixture.getAsJsonObject("dimensions");
    List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(fixtureDimensions.entrySet());
    Collections.reverse(entries);
    for (Map.Entry<String, JsonElement> entry : entries) {
      if (!omitted.contains(entry.getKey())) {
        Object value =
            overrides.containsKey(entry.getKey())
                ? overrides.get(entry.getKey())
                : value(entry.getValue());
        builder.dimension(Dimension.fromKey(entry.getKey()), value);
      }
    }
    for (Map.Entry<String, ?> override : overrides.entrySet()) {
      if (!fixtureDimensions.has(override.getKey())) {
        builder.dimension(Dimension.fromKey(override.getKey()), override.getValue());
      }
    }
    return builder.build(displayLabel);
  }

  private static Object value(JsonElement element) {
    JsonPrimitive value = element.getAsJsonPrimitive();
    if (value.isBoolean()) {
      return value.getAsBoolean();
    }
    if (value.isNumber()) {
      return value.getAsBigDecimal();
    }
    return value.getAsString();
  }

  private static String dimension(JsonObject fixture, String name) {
    return fixture.getAsJsonObject("dimensions").get(name).getAsString();
  }

  private static JsonObject fixture(String name) {
    for (JsonElement element : golden().getAsJsonArray("fixtures")) {
      JsonObject fixture = element.getAsJsonObject();
      if (fixture.get("name").getAsString().equals(name)) {
        return fixture;
      }
    }
    throw new IllegalArgumentException("Missing golden fixture: " + name);
  }

  private static JsonObject golden() {
    try (InputStream stream =
            WorkloadIdentityTest.class.getResourceAsStream("workload-identities-v1.json");
        InputStreamReader reader =
            new InputStreamReader(
                java.util.Objects.requireNonNull(stream, "golden fixture"),
                StandardCharsets.UTF_8)) {
      return JsonParser.parseReader(reader).getAsJsonObject();
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to read identity golden fixtures", exception);
    }
  }

  private static final class ParamDriftFixture {
    @Param({"latin", "mixed"})
    private String corpus;

    @Benchmark
    public void operation() {
    }
  }

  private static final class BenchmarkModeOverrideFixture {
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void operation() {
    }
  }

  private static final class OutputTimeUnitOverrideFixture {
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void operation() {
    }
  }

  private static final class ThreadsOverrideFixture {
    @Benchmark
    @Threads(2)
    public void operation() {
    }
  }

  private static final class ForkOverrideFixture {
    @Benchmark
    @Fork(3)
    public void operation() {
    }
  }

  private static final class WarmupOverrideFixture {
    @Benchmark
    @Warmup(iterations = 4)
    public void operation() {
    }
  }

  private static final class MeasurementOverrideFixture {
    @Benchmark
    @Measurement(iterations = 6)
    public void operation() {
    }
  }

  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @State(Scope.Thread)
  @Threads(1)
  @Fork(value = 2, warmups = 0)
  @Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  @Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  private static final class StateDriftFixture {
  }

  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @State(Scope.Benchmark)
  @Threads(1)
  @Fork(value = 2, warmups = 0, jvmArgsAppend = "-Xmx1g")
  @Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  @Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  private static final class ForkJvmArgsDriftFixture {
  }

  private static class InheritedBenchmarkBase {
    @Benchmark
    public void inheritedOperation() {
    }
  }

  private static final class InheritedBenchmarkFixture extends InheritedBenchmarkBase {
  }

  private static class InheritedSetupBase {
    @Setup(Level.Trial)
    public void inheritedSetup() {
    }
  }

  private static final class InheritedSetupFixture extends InheritedSetupBase {
  }

  @Warmup(iterations = 4, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  private static class InheritedWarmupClassOverrideBase {
  }

  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @State(Scope.Benchmark)
  @Threads(1)
  @Fork(value = 2, warmups = 0)
  @Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  private static final class InheritedWarmupClassOverrideFixture
      extends InheritedWarmupClassOverrideBase {
  }

  @OperationsPerInvocation(2)
  private static class InheritedUnsupportedClassAnnotationBase {
  }

  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @State(Scope.Benchmark)
  @Threads(1)
  @Fork(value = 2, warmups = 0)
  @Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  @Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  private static final class InheritedUnsupportedClassAnnotationFixture
      extends InheritedUnsupportedClassAnnotationBase {
  }

  private static class InheritedMethodOverrideBase {
    @Benchmark
    @Warmup(iterations = 4, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
    public void inheritedOperation() {
    }
  }

  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @State(Scope.Benchmark)
  @Threads(1)
  @Fork(value = 2, warmups = 0)
  @Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  @Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
  private static final class InheritedMethodOverrideFixture extends InheritedMethodOverrideBase {
  }

  private enum KotlinTokenType {
    IDENTIFIER,
    STRING,
    SYMBOL
  }

  private enum InlineStyleDrift {
    ORDERED_FONTS,
    FONT_STYLE,
    FONT_WEIGHT,
    EFFECTIVE_STRETCH,
    FONT_SIZE,
    LINE_HEIGHT,
    COLOR,
    DISPLAY,
    POSITION,
    WHITE_SPACE,
    TEXT_ALIGN,
    OVERFLOW_WRAP,
    WORD_BREAK,
    TAB_SIZE
  }

  private record KotlinToken(KotlinTokenType type, String value) {
    boolean isIdentifier(String expected) {
      return type == KotlinTokenType.IDENTIFIER && value.equals(expected);
    }

    boolean isString(String expected) {
      return type == KotlinTokenType.STRING && value.equals(expected);
    }

    boolean isSymbol(String expected) {
      return type == KotlinTokenType.SYMBOL && value.equals(expected);
    }
  }
}
