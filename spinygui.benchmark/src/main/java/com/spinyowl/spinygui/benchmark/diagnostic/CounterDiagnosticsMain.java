package com.spinyowl.spinygui.benchmark.diagnostic;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.spinyowl.spinygui.benchmark.RendererHostLifecycle;
import com.spinyowl.spinygui.benchmark.diagnostic.CounterDiagnosticArtifact.Entry;
import com.spinyowl.spinygui.benchmark.diagnostic.DiagnosticWorkloadSpecifications.CpuScenario;
import com.spinyowl.spinygui.benchmark.diagnostic.DiagnosticWorkloadSpecifications.ObservedShape;
import com.spinyowl.spinygui.benchmark.diagnostic.DiagnosticWorkloadSpecifications.RendererScenario;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInvocationMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Artifact;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Pairing;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRuntimeMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Category;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.joml.Vector2f;
import org.joml.Vector2i;

/** Executes one untimed, isolated recorded operation per identified scenario. */
public final class CounterDiagnosticsMain {
  private CounterDiagnosticsMain() {
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 3 || Pairing.fromJson(args[2]) != Pairing.UNPAIRED_INVESTIGATION) {
      throw new IllegalArgumentException(
          "Expected output path, run ID, and unpaired-investigation eligibility");
    }
    Path output = Path.of(args[0]);
    CounterDiagnosticArtifact artifact = run(args[1]);
    Files.createDirectories(output.getParent());
    Files.writeString(output, new GsonBuilder().setPrettyPrinting().create().toJson(artifact));
  }

  static CounterDiagnosticArtifact run(String runId) {
    List<Entry> entries = new ArrayList<>(runCpuScenarios());
    entries.addAll(runRendererScenarios());
    BenchmarkRunMetadata metadata = BenchmarkInvocationMetadata.diagnostics(runId);
    return new CounterDiagnosticArtifact(
        CounterDiagnosticArtifact.SCHEMA_VERSION,
        metadata.toJson(),
        TextDiagnosticCounter.VOCABULARY_VERSION,
        NvgDiagnosticCounter.VOCABULARY_VERSION,
        entries);
  }

  static List<Entry> runCpuScenarios() {
    ComparabilityMetadata.Environment environment = BenchmarkRuntimeMetadata.cpuEnvironment();
    ComparabilityMetadata.Implementation implementation = BenchmarkRuntimeMetadata.implementation();
    List<Entry> entries = new ArrayList<>();
    for (CpuScenario scenario : DiagnosticWorkloadSpecifications.CPU_SCENARIOS) {
      entries.add(runCpuScenario(scenario, environment, implementation));
    }
    return List.copyOf(entries);
  }

  static Entry runCpuScenario(
      CpuScenario scenario,
      ComparabilityMetadata.Environment environment,
      ComparabilityMetadata.Implementation implementation) {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(Arrays.asList(TextDiagnosticCounter.values()));
    FontServiceImpl fontService =
        new FontServiceImpl(new FontStorageImpl(), false, diagnostics);
    fontService.installSemanticOwner();

    scenario.execute(fontService);
    diagnostics.reset();
    TextMetrics metrics = scenario.execute(fontService);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();
    ObservedShape observed = observeCpu(scenario, metrics, snapshot);
    scenario.validateObserved(observed);
    return entry(
        scenario,
        environment,
        implementation,
        snapshot,
        observed,
        observedTextEvidence(Category.CPU, observed));
  }

  static List<Entry> runRendererScenarios() {
    ComparabilityMetadata.Implementation implementation = BenchmarkRuntimeMetadata.implementation();
    List<Entry> entries = new ArrayList<>();
    for (RendererScenario scenario : DiagnosticWorkloadSpecifications.RENDERER_SCENARIOS) {
      DiagnosticSession diagnostics = DiagnosticSession.enabled(combinedVocabulary());
      PreparedScene prepared = prepareScene(scenario, diagnostics);
      try (
          FontServiceImpl ignored = prepared.fontService();
          HiddenContext context = new HiddenContext(diagnostics)) {
        context.initialize();
        ComparabilityMetadata.Environment environment =
            BenchmarkRuntimeMetadata.renderingEnvironment(
                glGetString(GL_VENDOR),
                glGetString(GL_RENDERER),
                glGetString(GL_VERSION),
                glGetString(GL_VERSION));
        ObservedShape preparedObserved = observeRenderer(prepared);
        scenario.validateObserved(preparedObserved);
        if ("unchanged".equals(scenario.submissionState())) {
          context.renderPredecessor(prepared);
        }
        diagnostics.reset();
        context.render(prepared);
        DiagnosticSnapshot snapshot = diagnostics.snapshot();
        ObservedShape observed = observeRenderer(prepared);
        scenario.validateObserved(observed);
        validateRecordedRendererEvidence(scenario, observed, snapshot);
        entries.add(
            entry(
                scenario,
                environment,
                implementation,
                snapshot,
                observed,
                rendererEvidence(prepared, observed)));
      }
    }
    return List.copyOf(entries);
  }

  private static ObservedShape observeCpu(
      CpuScenario scenario, TextMetrics metrics, DiagnosticSnapshot snapshot) {
    long glyphs = glyphCount(metrics.lines());
    long runs = metrics.lines().stream().mapToLong(line -> line.runs().size()).sum();
    long sourceScans = snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED);
    long processedSource = sourceScans;
    int lineStarts = wrappedLineStarts(scenario.text(), metrics.lines());
    int deferred =
        Math.toIntExact(Math.max(0, processedSource - codePointCount(scenario.text())));
    return new ObservedShape(
        DiagnosticWorkloadSpecifications.sourceTextSha256(List.of(scenario.text())),
        codePointCount(scenario.text()),
        scenario.text().length(),
        sourceLineCount(scenario.text()),
        metrics.lines().size(),
        paragraphCount(scenario.text()),
        fallbackTransitions(scenario.text(), metrics.lines()),
        deferred,
        lineStarts,
        processedSource,
        glyphs,
        runs,
        0);
  }

  static PreparedScene prepareScene(
      RendererScenario scenario, DiagnosticSession diagnostics) {
    FontServiceImpl fontService =
        new FontServiceImpl(new FontStorageImpl(), true, diagnostics);
    fontService.installSemanticOwner();
    scenario.style().verifyResolution(fontService.fontChainResolver());
    fontService.measureText(
        scenario.prewarmText(),
        scenario.prewarmFonts(),
        scenario.style().fontSizePx(),
        scenario.style().lineHeight());

    Frame frame = new Frame();
    scenario.style().apply(frame);
    frame.frameSize(
        DiagnosticWorkloadSpecifications.FRAME_WIDTH_PX,
        DiagnosticWorkloadSpecifications.FRAME_HEIGHT_PX);
    frame.box().contentSize(
        DiagnosticWorkloadSpecifications.FRAME_WIDTH_PX,
        DiagnosticWorkloadSpecifications.FRAME_HEIGHT_PX);

    Element container = NodeBuilder.div();
    scenario.style().apply(container);
    container.box().contentPosition(scenario.container().xPx(), scenario.container().yPx());
    container.box().contentSize(scenario.container().widthPx(), scenario.container().heightPx());
    container.offsetParent(frame);
    frame.addChild(container);
    frame.layoutChildNodes(List.of(container));

    PreparedContent content =
        switch (scenario.category()) {
          case NORMAL_TEXT -> prepareNormalText(scenario, fontService, container);
          case INPUT -> prepareInputs(scenario, fontService, container);
          case TEXTAREA -> prepareTextareas(scenario, fontService, container);
          case CPU -> throw new IllegalStateException("CPU scenario cannot use the renderer");
        };
    container.layoutChildNodes(content.nodes());
    return new PreparedScene(scenario, frame, container, fontService, content.nodes());
  }

  private static PreparedContent prepareNormalText(
      RendererScenario scenario, FontServiceImpl fontService, Element container) {
    List<Node> textNodes = new ArrayList<>();
    for (int index = 0; index < scenario.itemCount(); index++) {
      Text text = NodeBuilder.text(scenario.sourceContent(index));
      text.offsetParent(container);
      container.addChild(text);
      textNodes.add(text);
    }
    new InlineFormattingContext(fontService).layout(container, textNodes, 0);

    return new PreparedContent(List.copyOf(textNodes));
  }

  private static PreparedContent prepareInputs(
      RendererScenario scenario, FontServiceImpl fontService, Element container) {
    List<Node> inputs = new ArrayList<>();
    for (int index = 0; index < scenario.itemCount(); index++) {
      InputElement input = new InputElement();
      scenario.style().apply(input);
      input.value(scenario.sourceContent(index));
      applySelection(input, scenario);
      input.focused(true);
      input.textScrollLeft(scenario.scrollXPx());
      input.box().contentPosition(0, 0);
      input.box().contentSize(scenario.controlWidthPx(), scenario.controlHeightPx());
      input.offsetParent(container);
      container.addChild(input);
      inputs.add(input);
    }
    return new PreparedContent(List.copyOf(inputs));
  }

  private static PreparedContent prepareTextareas(
      RendererScenario scenario, FontServiceImpl fontService, Element container) {
    List<Node> textareas = new ArrayList<>();
    for (int index = 0; index < scenario.itemCount(); index++) {
      TextareaElement textarea = new TextareaElement(scenario.sourceContent(index));
      scenario.style().apply(textarea);
      applySelection(textarea, scenario);
      textarea.focused(true);
      textarea.textScrollLeft(scenario.scrollXPx());
      textarea.textScrollTop(scenario.scrollYPx());
      textarea.box().contentPosition(0, 0);
      textarea.box().contentSize(scenario.controlWidthPx(), scenario.controlHeightPx());
      textarea.offsetParent(container);
      container.addChild(textarea);
      textareas.add(textarea);
    }
    return new PreparedContent(List.copyOf(textareas));
  }

  static void validatePreparedScene(RendererScenario scenario, PreparedScene prepared) {
    ObservedShape observed = observeRenderer(prepared);
    scenario.validateObserved(observed);
    Map<String, String> declared = declaredInputs(scenario);
    CounterDiagnosticArtifact.Entry.validateDeclaredObservedAgreement(
        scenario.name(), scenario.evidenceScope(), declared, rendererEvidence(prepared, observed));
  }

  static Map<String, JsonPrimitive> preparedEvidence(PreparedScene prepared) {
    ObservedShape observed = observeRenderer(prepared);
    return rendererEvidence(prepared, observed);
  }

  private static ObservedShape observeRenderer(PreparedScene prepared) {
    return switch (prepared.scenario().category()) {
      case NORMAL_TEXT -> observeNormalText(prepared);
      case INPUT -> observeInputs(prepared);
      case TEXTAREA -> observeTextareas(prepared);
      case CPU -> throw new IllegalStateException("CPU scenario cannot use the renderer");
    };
  }

  private static ObservedShape observeNormalText(PreparedScene prepared) {
    List<String> sources = new ArrayList<>();
    TreeSet<Integer> visualLineY = new TreeSet<>();
    long fragments = 0;
    long glyphs = 0;
    long runs = 0;
    int fallbackTransitions = 0;
    for (Node node : prepared.nodes()) {
      Text text = (Text) node;
      sources.add(text.content());
      fragments += text.inlineFragments().size();
      for (InlineFragment fragment : text.inlineFragments()) {
        visualLineY.add(Float.floatToIntBits(fragment.y()));
        glyphs += fragment.runs().stream().mapToLong(run -> run.glyphs().size()).sum();
        runs += fragment.runs().size();
      }
      fallbackTransitions += fallbackTransitions(text.content(), fragmentLines(text));
    }
    return observedShape(
        sources,
        visualLineY.size(),
        fallbackTransitions,
        0,
        0,
        codePointCount(sources),
        glyphs,
        runs,
        fragments);
  }

  private static ObservedShape observeInputs(PreparedScene prepared) {
    List<String> sources = new ArrayList<>();
    long glyphs = 0;
    long runs = 0;
    int visualLines = 0;
    int fallbackTransitions = 0;
    for (Node node : prepared.nodes()) {
      InputElement input = (InputElement) node;
      sources.add(input.value());
      TextLineMetrics line =
          prepared.fontService().getTextLineMetrics(
              input.value(),
              prepared.scenario().layoutFonts(),
              prepared.scenario().style().fontSizePx(),
              prepared.scenario().style().lineHeight());
      glyphs += line.runs().stream().mapToLong(run -> run.glyphs().size()).sum();
      runs += line.runs().size();
      visualLines++;
      fallbackTransitions += fallbackTransitions(input.value(), List.of(line));
    }
    return observedShape(
        sources,
        visualLines,
        fallbackTransitions,
        0,
        0,
        codePointCount(sources),
        glyphs,
        runs,
        0);
  }

  private static ObservedShape observeTextareas(PreparedScene prepared) {
    List<String> sources = new ArrayList<>();
    long glyphs = 0;
    long runs = 0;
    int visualLines = 0;
    int fallbackTransitions = 0;
    int deferredSuffixCodePoints = 0;
    int lineStarts = 0;
    for (Node node : prepared.nodes()) {
      TextareaElement textarea = (TextareaElement) node;
      sources.add(textarea.value());
      long scansBefore =
          prepared
              .fontService()
              .diagnostics()
              .snapshot()
              .value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED);
      List<MultilineTextControlMetrics.Line> lines =
          new MultilineTextControlMetrics(prepared.fontService()).lines(textarea);
      long scansAfter =
          prepared
              .fontService()
              .diagnostics()
              .snapshot()
              .value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED);
      visualLines += lines.size();
      List<TextLineMetrics> metricLines = controlMetricLines(lines);
      long currentGlyphs = glyphCount(metricLines);
      glyphs += currentGlyphs;
      runs += metricLines.stream().mapToLong(line -> line.runs().size()).sum();
      fallbackTransitions += fallbackTransitions(textarea.value(), metricLines);
      int currentLineStarts = wrappedLineStarts(textarea.value(), metricLines);
      lineStarts += currentLineStarts;
      int measuredSourceCodePoints =
          codePointCount(textarea.value()) - (sourceLineCount(textarea.value()) - 1);
      deferredSuffixCodePoints +=
          Math.toIntExact(
              Math.max(
                  0,
                  scansAfter
                      - scansBefore
                      - currentGlyphs
                      - measuredSourceCodePoints
                      - currentLineStarts));
    }
    return observedShape(
        sources,
        visualLines,
        fallbackTransitions,
        deferredSuffixCodePoints,
        lineStarts,
        codePointCount(sources),
        glyphs,
        runs,
        0);
  }

  private static ObservedShape observedShape(
      List<String> sources,
      int visualLines,
      int fallbackTransitions,
      int deferredSuffixCodePoints,
      int lineStartTransitions,
      long processedSourceCodePoints,
      long glyphs,
      long runs,
      long fragments) {
    return new ObservedShape(
        DiagnosticWorkloadSpecifications.sourceTextSha256(sources),
        codePointCount(sources),
        utf16Length(sources),
        sourceLineCount(sources),
        visualLines,
        paragraphCount(sources),
        fallbackTransitions,
        deferredSuffixCodePoints,
        lineStartTransitions,
        processedSourceCodePoints,
        glyphs,
        runs,
        fragments);
  }

  private static void validateRecordedRendererEvidence(
      RendererScenario scenario, ObservedShape observed, DiagnosticSnapshot snapshot) {
    if (scenario.category() == Category.TEXTAREA) {
      long recordedLines = snapshot.value(NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED);
      if (recordedLines != observed.visualLineCount()) {
        throw new IllegalStateException(
            "Recorded textarea line evidence disagrees with prepared visual lines for "
                + scenario.name());
      }
      if (snapshot.value(TextDiagnosticCounter.TEXTAREA_COMPLETE_LAYOUTS) <= scenario.itemCount()) {
        throw new IllegalStateException(
            "Textarea scenario does not expose repeated complete-layout work: " + scenario.name());
      }
    }
    if (scenario.category() == Category.INPUT
        && snapshot.value(TextDiagnosticCounter.INPUT_COMPLETE_LAYOUTS) != scenario.itemCount()) {
      throw new IllegalStateException(
          "Recorded input complete-layout count disagrees with aggregate item scope");
    }
  }

  private static Entry entry(
      DiagnosticWorkloadSpecifications.Scenario scenario,
      ComparabilityMetadata.Environment environment,
      ComparabilityMetadata.Implementation implementation,
      DiagnosticSnapshot snapshot,
      ObservedShape observed,
      Map<String, JsonPrimitive> outputs) {
    var identity = scenario.identity();
    var comparability =
        DiagnosticWorkloadSpecifications.comparability(scenario, environment, implementation);
    return new Entry(
        scenario.name(),
        scenario.evidenceScope(),
        identity.semanticId(),
        identity.seriesId(),
        declaredInputs(scenario),
        comparability.toJson(),
        snapshot.values(),
        snapshot.saturatedCounterIds(),
        outputs);
  }

  private static Map<String, JsonPrimitive> rendererEvidence(
      PreparedScene prepared, ObservedShape observed) {
    Category category = prepared.scenario().category();
    Map<String, JsonPrimitive> outputs = new LinkedHashMap<>(observedTextEvidence(category, observed));
    var frameSize = prepared.frame().frameSize();
    var containerRect = prepared.container().box().content();
    float contentX = containerRect.x();
    float contentY = containerRect.y();
    float contentWidth = containerRect.width();
    float contentHeight = containerRect.height();
    if (category != Category.NORMAL_TEXT) {
      var first = (Element) prepared.nodes().getFirst();
      var controlRect = first.box().content();
      contentX += controlRect.x();
      contentY += controlRect.y();
      contentWidth = controlRect.width();
      contentHeight = controlRect.height();
      validateEquivalentControls(prepared.nodes(), first);
      if (first instanceof InputElement input) {
        put(outputs, "observed-caret-index-utf16", input.caretIndex());
        put(outputs, "observed-control-focused", input.focused());
        put(outputs, "observed-control-height-px", controlRect.height());
        put(outputs, "observed-control-width-px", controlRect.width());
        put(outputs, "observed-scroll-x-px", input.textScrollLeft());
        put(outputs, "observed-selection-end-utf16", input.selectionEnd());
        put(outputs, "observed-selection-start-utf16", input.selectionStart());
      } else if (first instanceof TextareaElement textarea) {
        put(outputs, "observed-caret-index-utf16", textarea.caretIndex());
        put(outputs, "observed-control-focused", textarea.focused());
        put(outputs, "observed-control-height-px", controlRect.height());
        put(outputs, "observed-control-width-px", controlRect.width());
        put(outputs, "observed-scroll-x-px", textarea.textScrollLeft());
        put(outputs, "observed-scroll-y-px", textarea.textScrollTop());
        put(outputs, "observed-selection-end-utf16", textarea.selectionEnd());
        put(outputs, "observed-selection-start-utf16", textarea.selectionStart());
        put(outputs, "observed-wrap-width-px", controlRect.width());
      }
    }
    put(outputs, "observed-frame-width-px", frameSize.x());
    put(outputs, "observed-frame-height-px", frameSize.y());
    put(outputs, "observed-container-position-x-px", containerRect.x());
    put(outputs, "observed-container-position-y-px", containerRect.y());
    put(outputs, "observed-container-width-px", containerRect.width());
    put(outputs, "observed-container-height-px", containerRect.height());
    put(outputs, "observed-effective-content-position-x-px", contentX);
    put(outputs, "observed-effective-content-position-y-px", contentY);
    put(
        outputs,
        "observed-offscreen-extent-px",
        offscreenExtent(contentX, contentY, contentWidth, contentHeight, frameSize.x(), frameSize.y()));
    put(
        outputs,
        "observed-offscreen-ratio",
        offscreenRatio(contentX, contentY, contentWidth, contentHeight, frameSize.x(), frameSize.y()));
    put(
        outputs,
        "observed-predecessor-render-execution-count",
        prepared.predecessorRenderExecutions());
    return Map.copyOf(outputs);
  }

  private static Map<String, JsonPrimitive> observedTextEvidence(
      Category category, ObservedShape observed) {
    Map<String, JsonPrimitive> outputs = new LinkedHashMap<>();
    put(outputs, "observed-source-text-sha256", observed.sourceTextSha256());
    put(outputs, "observed-source-code-point-count", observed.sourceCodePointCount());
    put(outputs, "observed-source-utf16-length", observed.sourceUtf16Length());
    put(outputs, "observed-source-line-count", observed.sourceLineCount());
    put(outputs, "observed-paragraph-count", observed.paragraphCount());
    put(outputs, "observed-visual-line-count", observed.visualLineCount());
    put(outputs, "observed-resolved-glyph-count", observed.resolvedGlyphCount());
    put(outputs, "observed-resolved-run-count", observed.resolvedRunCount());
    put(outputs, "observed-fallback-transition-count", observed.fallbackTransitionCount());
    if (category == Category.CPU) {
      put(
          outputs,
          "observed-processed-source-code-point-count",
          observed.processedSourceCodePointCount());
    }
    if (category == Category.CPU || category == Category.TEXTAREA) {
      put(
          outputs,
          "observed-deferred-suffix-code-point-count",
          observed.deferredSuffixCodePointCount());
      put(
          outputs,
          "observed-line-start-kerning-transition-count",
          observed.lineStartKerningTransitionCount());
    }
    if (category == Category.NORMAL_TEXT) {
      put(outputs, "observed-text-fragment-count", observed.textFragmentCount());
    }
    return Map.copyOf(outputs);
  }

  private static int fallbackTransitions(String source, List<TextLineMetrics> lines) {
    List<ResolvedGlyph> glyphs =
        lines.stream()
            .flatMap(line -> line.runs().stream())
            .flatMap(run -> run.glyphs().stream())
            .collect(
                java.util.stream.Collectors.toMap(
                    ResolvedGlyph::sourceStart,
                    glyph -> glyph,
                    (first, ignored) -> first,
                    java.util.TreeMap::new))
            .values()
            .stream()
            .toList();
    int transitions = 0;
    ResolvedGlyph previous = null;
    for (ResolvedGlyph glyph : glyphs) {
      if (previous != null
          && source.substring(previous.sourceEnd(), glyph.sourceStart()).indexOf('\n') < 0
          && previous.font() != glyph.font()) {
        transitions++;
      }
      previous = glyph;
    }
    return transitions;
  }

  private static int wrappedLineStarts(String source, List<TextLineMetrics> lines) {
    int transitions = 0;
    for (TextLineMetrics line : lines) {
      int start = line.startIndex();
      if (start > 0 && source.charAt(start - 1) != '\n') transitions++;
    }
    return transitions;
  }

  private static long glyphCount(List<TextLineMetrics> lines) {
    return lines.stream()
        .flatMap(line -> line.runs().stream())
        .mapToLong(run -> run.glyphs().size())
        .sum();
  }

  private static List<TextLineMetrics> fragmentLines(Text text) {
    List<TextLineMetrics> lines = new ArrayList<>();
    for (InlineFragment fragment : text.inlineFragments()) {
      lines.add(
          TextLineMetrics.builder()
              .characters(fragment.text())
              .startIndex(
                  fragment.runs().isEmpty() ? 0 : fragment.runs().getFirst().sourceStart())
              .endIndex(
                  fragment.runs().isEmpty() ? 0 : fragment.runs().getLast().sourceEnd())
              .runs(fragment.runs())
              .build());
    }
    return lines;
  }

  private static List<TextLineMetrics> controlMetricLines(
      List<MultilineTextControlMetrics.Line> lines) {
    return lines.stream()
        .map(
            line ->
                TextLineMetrics.builder()
                    .characters(line.text())
                    .startIndex(line.startIndex())
                    .endIndex(line.endIndex())
                    .runs(line.runs())
                    .build())
        .toList();
  }

  private static void applySelection(InputElement input, RendererScenario scenario) {
    input.caretIndex(scenario.caretIndexUtf16());
    input.selectionAnchor(selectionAnchor(scenario));
  }

  private static void applySelection(TextareaElement textarea, RendererScenario scenario) {
    textarea.caretIndex(scenario.caretIndexUtf16());
    textarea.selectionAnchor(selectionAnchor(scenario));
  }

  private static int selectionAnchor(RendererScenario scenario) {
    return scenario.caretIndexUtf16() == scenario.selectionStartUtf16()
        ? scenario.selectionEndUtf16()
        : scenario.selectionStartUtf16();
  }

  private static int sourceLineCount(String text) {
    return text.split("\n", -1).length;
  }

  private static int paragraphCount(String text) {
    return sourceLineCount(text);
  }

  private static int codePointCount(String text) {
    return text.codePointCount(0, text.length());
  }

  private static int codePointCount(List<String> sources) {
    return sources.stream().mapToInt(CounterDiagnosticsMain::codePointCount).sum();
  }

  private static int utf16Length(List<String> sources) {
    return sources.stream().mapToInt(String::length).sum();
  }

  private static int sourceLineCount(List<String> sources) {
    return sources.stream().mapToInt(CounterDiagnosticsMain::sourceLineCount).sum();
  }

  private static int paragraphCount(List<String> sources) {
    return sources.stream().mapToInt(CounterDiagnosticsMain::paragraphCount).sum();
  }

  private static Map<String, String> declaredInputs(
      DiagnosticWorkloadSpecifications.Scenario scenario) {
    Map<String, String> declared = new LinkedHashMap<>();
    scenario
        .identity()
        .dimensions()
        .forEach((dimension, value) -> declared.put(dimension.key(), value));
    return Map.copyOf(declared);
  }

  private static void validateEquivalentControls(List<Node> nodes, Element first) {
    for (Node node : nodes) {
      Element control = (Element) node;
      if (Float.compare(control.box().content().x(), first.box().content().x()) != 0
          || Float.compare(control.box().content().y(), first.box().content().y()) != 0
          || Float.compare(control.box().content().width(), first.box().content().width()) != 0
          || Float.compare(control.box().content().height(), first.box().content().height()) != 0
          || control.focused() != first.focused()) {
        throw new IllegalStateException("Prepared controls disagree within aggregate evidence scope");
      }
      if (first instanceof InputElement expected && control instanceof InputElement actual) {
        if (actual.caretIndex() != expected.caretIndex()
            || actual.selectionStart() != expected.selectionStart()
            || actual.selectionEnd() != expected.selectionEnd()
            || Float.compare(actual.textScrollLeft(), expected.textScrollLeft()) != 0) {
          throw new IllegalStateException("Prepared inputs disagree within aggregate evidence scope");
        }
      } else if (first instanceof TextareaElement expected
          && control instanceof TextareaElement actual) {
        if (actual.caretIndex() != expected.caretIndex()
            || actual.selectionStart() != expected.selectionStart()
            || actual.selectionEnd() != expected.selectionEnd()
            || Float.compare(actual.textScrollLeft(), expected.textScrollLeft()) != 0
            || Float.compare(actual.textScrollTop(), expected.textScrollTop()) != 0) {
          throw new IllegalStateException(
              "Prepared textareas disagree within aggregate evidence scope");
        }
      } else {
        throw new IllegalStateException("Prepared control category changed after setup");
      }
    }
  }

  private static float offscreenRatio(
      float x, float y, float width, float height, float frameWidth, float frameHeight) {
    float intersectionWidth = Math.max(0, Math.min(x + width, frameWidth) - Math.max(x, 0));
    float intersectionHeight = Math.max(0, Math.min(y + height, frameHeight) - Math.max(y, 0));
    return 1 - (intersectionWidth * intersectionHeight) / (width * height);
  }

  private static float offscreenExtent(
      float x, float y, float width, float height, float frameWidth, float frameHeight) {
    return Math.max(
        Math.max(Math.max(0, -x), Math.max(0, x + width - frameWidth)),
        Math.max(Math.max(0, -y), Math.max(0, y + height - frameHeight)));
  }

  private static void put(
      Map<String, JsonPrimitive> outputs, String key, Number value) {
    BigDecimal decimal = new BigDecimal(value.toString());
    BigDecimal normalized =
        decimal.compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : decimal.stripTrailingZeros();
    if (normalized.scale() < 0) normalized = normalized.setScale(0);
    outputs.put(key, new JsonPrimitive(normalized));
  }

  private static void put(
      Map<String, JsonPrimitive> outputs, String key, String value) {
    outputs.put(key, new JsonPrimitive(value));
  }

  private static void put(
      Map<String, JsonPrimitive> outputs, String key, boolean value) {
    outputs.put(key, new JsonPrimitive(value));
  }

  private static Set<DiagnosticCounter> combinedVocabulary() {
    return Stream.concat(
            Arrays.stream(TextDiagnosticCounter.values()),
            Arrays.stream(NvgDiagnosticCounter.values()))
        .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
  }

  private record PreparedContent(List<Node> nodes) {
  }

  static final class PreparedScene {
    private final RendererScenario scenario;
    private final Frame frame;
    private final Element container;
    private final FontServiceImpl fontService;
    private final List<Node> nodes;
    private long predecessorRenderExecutions;

    PreparedScene(
        RendererScenario scenario,
        Frame frame,
        Element container,
        FontServiceImpl fontService,
        List<Node> nodes) {
      this.scenario = scenario;
      this.frame = frame;
      this.container = container;
      this.fontService = fontService;
      this.nodes = List.copyOf(nodes);
    }

    RendererScenario scenario() {
      return scenario;
    }

    Frame frame() {
      return frame;
    }

    Element container() {
      return container;
    }

    FontServiceImpl fontService() {
      return fontService;
    }

    List<Node> nodes() {
      return nodes;
    }

    long predecessorRenderExecutions() {
      return predecessorRenderExecutions;
    }

    void recordPredecessorRenderExecution() {
      predecessorRenderExecutions = Math.addExact(predecessorRenderExecutions, 1);
    }
  }

  private static final class HiddenContext implements AutoCloseable {
    private final DiagnosticSession diagnostics;
    private long window;
    private NvgRenderer renderer;
    private RendererHostLifecycle<NvgRenderer> rendererLifecycle;
    private boolean glfwInitialized;

    private HiddenContext(DiagnosticSession diagnostics) {
      this.diagnostics = diagnostics;
    }

    private void initialize() {
      if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
      glfwInitialized = true;
      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
      glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
      window =
          glfwCreateWindow(
              DiagnosticWorkloadSpecifications.FRAME_WIDTH_PX,
              DiagnosticWorkloadSpecifications.FRAME_HEIGHT_PX,
              "SpinyGUI counter diagnostics",
              NULL,
              NULL);
      if (window == NULL) throw new IllegalStateException("Unable to create hidden GLFW window");
      glfwMakeContextCurrent(window);
      createCapabilities();
      glfwSwapInterval(0);
      glViewport(
          0,
          0,
          DiagnosticWorkloadSpecifications.FRAME_WIDTH_PX,
          DiagnosticWorkloadSpecifications.FRAME_HEIGHT_PX);
      rendererLifecycle =
          new RendererHostLifecycle<>(
              () -> new NvgRenderer(true, diagnostics),
              NvgRenderer::initialize,
              NvgRenderer::destroy,
              this::closeHost);
      renderer = rendererLifecycle.initialize();
    }

    private void render(PreparedScene scene) {
      glClearColor(0, 0, 0, 1);
      glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
      renderer.textMeasurer(scene.fontService());
      renderer.render(
          window,
          new Vector2f(
              DiagnosticWorkloadSpecifications.FRAME_WIDTH_PX,
              DiagnosticWorkloadSpecifications.FRAME_HEIGHT_PX),
          new Vector2i(
              DiagnosticWorkloadSpecifications.FRAME_WIDTH_PX,
              DiagnosticWorkloadSpecifications.FRAME_HEIGHT_PX),
          scene.frame());
    }

    private void renderPredecessor(PreparedScene scene) {
      render(scene);
      scene.recordPredecessorRenderExecution();
    }

    @Override
    public void close() {
      if (rendererLifecycle != null) {
        rendererLifecycle.close();
      } else {
        closeHost();
      }
    }

    private void closeHost() {
      setCapabilities(null);
      if (window != NULL) {
        glfwMakeContextCurrent(NULL);
        glfwDestroyWindow(window);
      }
      if (glfwInitialized) glfwTerminate();
    }
  }
}
