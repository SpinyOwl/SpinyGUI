package com.spinyowl.spinygui.benchmark.interaction;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.FramePipeline;
import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.animation.TransitionImpact;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
import com.spinyowl.spinygui.core.input.impl.ShortcutRegistryImpl;
import com.spinyowl.spinygui.core.layout.impl.BlockLayout;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.layout.impl.LayoutServiceImpl;
import com.spinyowl.spinygui.core.layout.impl.NoneLayout;
import com.spinyowl.spinygui.core.layout.impl.TextLayoutImpl;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemCursorPosEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemKeyEventListener;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;

/** Deterministic, headless text-heavy panel used by every interaction scenario. */
final class DiagnosticsPanelFixture implements AutoCloseable {
  static final int ROW_COUNT = 256;
  static final int TEXT_NODES_PER_ROW = 3;
  private static final int ROW_HEIGHT = 22;

  private final DiagnosticsInteractionScenario scenario;
  private final Frame frame = new Frame();
  private final Element panel = NodeBuilder.div();
  private final java.util.ArrayList<Element> rows = new java.util.ArrayList<>(ROW_COUNT);
  private final InputElement editor = NodeBuilder.input(NodeBuilder.TYPE_TEXT, "filter", "initial");
  private final MouseServiceImpl mouse = new MouseServiceImpl();
  private final DefaultEventProcessor guiEvents = new DefaultEventProcessor();
  private final SystemEventProcessorImpl systemEvents;
  private final StyleManagerImpl styleManager;
  private final LayoutServiceImpl layoutService;
  private final FontServiceImpl fontService;
  private final FramePipeline pipeline;
  private boolean alternate;

  DiagnosticsPanelFixture(DiagnosticsInteractionScenario scenario, DiagnosticSession diagnostics) {
    this.scenario = scenario;
    frame.diagnostics(diagnostics);
    frame.frameSize(1024, 768);
    frame.box().contentSize(1024, 768);
    panel.setAttribute("class", "diagnostics-panel");
    frame.addChild(panel);
    for (int rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
      Element row = NodeBuilder.div();
      row.setAttribute("id", "diagnostic-row-" + rowIndex);
      String scenarioClass = switch (scenario) {
        case PAINT_ONLY_HOVER -> " paint-hover";
        case DIMENSION_AFFECTING_HOVER -> " dimension-hover";
        default -> "";
      };
      row.setAttribute("class", "diagnostic-row" + scenarioClass);
      row.addChild(NodeBuilder.text("Subsystem " + rowIndex));
      row.addChild(NodeBuilder.text("  value=" + (rowIndex * 17)));
      row.addChild(NodeBuilder.text("  status=healthy"));
      row.box().contentPosition(8, 8 + rowIndex * ROW_HEIGHT);
      row.box().contentSize(880, 20);
      panel.addChild(row);
      rows.add(row);
    }
    editor.setAttribute("class", "diagnostic-editor");
    editor.box().contentPosition(8, 8);
    editor.box().contentSize(240, 20);
    panel.addChild(editor);
    if (scenario == DiagnosticsInteractionScenario.UNKNOWN_LISTENER_EFFECT) {
      rows.get(1).addListener(CursorEnterEvent.class, ignored -> {});
    }

    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    frame.styleSheets().add(parser.parse("""
        .diagnostics-panel { display: block; width: 900px; }
        .diagnostic-row { display: block; width: 880px; height: 20px; }
        .diagnostic-editor { display: block; width: 240px; height: 20px; }
        .paint-hover:hover { opacity: 0.65; }
        .dimension-hover:hover { width: 840px; height: 24px; }
        """));
    styleManager = new StyleManagerImpl(propertyStore, parser);
    fontService = new FontServiceImpl(new FontStorageImpl(), false, DiagnosticSession.disabled());
    fontService.installSemanticOwner();
    InlineFormattingContext inline = new InlineFormattingContext(fontService);
    Map<Display, com.spinyowl.spinygui.core.layout.ElementLayout> layouts = new HashMap<>();
    layoutService = new LayoutServiceImpl(new TextLayoutImpl(fontService, fontService), layouts);
    layouts.put(Display.NONE, new NoneLayout());
    layouts.put(Display.BLOCK, new BlockLayout(layoutService, inline, fontService));
    SystemEventListenerProviderImpl listeners = new SystemEventListenerProviderImpl();
    listeners.listener(
        SystemCursorPosEvent.class,
        SystemCursorPosEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(() -> 1D)
            .mouseService(mouse)
            .build());
    listeners.listener(
        SystemKeyEvent.class,
        SystemKeyEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(() -> 1D)
            .keyboard(
                new Keyboard(
                    new KeyboardLayoutImpl(Map.of(KeyCode.KEY_F12, 999)),
                    new ShortcutRegistryImpl()))
            .build());
    systemEvents = SystemEventProcessorImpl.builder().eventListenerProvider(listeners).build();
    pipeline =
        new FramePipeline(
            systemEvents, guiEvents, styleManager, () -> TransitionImpact.NO_CHANGE, layoutService);
    mouse.setCursorPositions(
        frame,
        new com.spinyowl.spinygui.core.input.MouseService.CursorPositions(
            new Vector2f(16, 19), new Vector2f(16, 19)));
    prepareAndRender();
  }

  Frame frame() {
    return frame;
  }

  InputImpact execute() {
    alternate = !alternate;
    switch (scenario) {
      case STATIONARY_POINTER -> pointer(16, 19);
      case POINTER_MOVE_WITHIN_TEXT_NODE -> pointer(alternate ? 18 : 32, 19);
      case POINTER_CROSS_TEXT_BOUNDARY, PAINT_ONLY_HOVER, DIMENSION_AFFECTING_HOVER ->
          pointer(16, alternate ? 19 : 41);
      case KEYBOARD_ONLY_INPUT ->
          systemEvents.push(
              SystemKeyEvent.builder()
                  .frame(frame)
                  .keyCode(999)
                  .scancode(999)
                  .action(SystemKeyAction.PRESS)
                  .mods(ImmutableSet.of())
                  .build());
      case SCROLL -> {
        frame.scrollTop(alternate ? 20 : 40);
      }
      case CLICK_FOCUS -> {
        rows.get(0).focused(alternate);
        rows.get(0).pressed(alternate);
      }
      case TEXT_EDITING -> {
        editor.value(alternate ? "initialx" : "initial");
      }
      case RESIZE -> {
        frame.frameSize(alternate ? 1024 : 1000, 768);
      }
      case UNKNOWN_LISTENER_EFFECT -> pointer(16, alternate ? 19 : 41);
    }
    InputImpact impact = pipeline.processInput();
    prepareAndRender();
    return impact;
  }

  private void pointer(float x, float y) {
    systemEvents.push(SystemCursorPosEvent.builder().frame(frame).posX(x).posY(y).build());
  }

  private void prepareAndRender() {
    FramePreparation preparation = pipeline.prepareFrame(frame);
    if (!preparation.renderable()) {
      throw new IllegalStateException("Benchmark frame preparation failed: " + preparation);
    }
    if (preparation.renderRequired()) {
      renderAndStabilizeHitBoxes(frame);
      if (!pipeline.publishRendered(frame, preparation)) {
        throw new IllegalStateException("Benchmark render was superseded");
      }
    }
  }

  private void renderAndStabilizeHitBoxes(Frame ignored) {
    countHeadlessRenderTraversal(frame);
    // Keep hit-test coordinates deterministic even if font availability changes row metrics.
    panel.box().contentPosition(0, 0);
    panel.box().contentSize(900, ROW_COUNT * ROW_HEIGHT + 32);
    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
      rows.get(rowIndex).box().contentPosition(8, 8 + rowIndex * ROW_HEIGHT);
      rows.get(rowIndex).box().contentSize(880, 20);
    }
  }

  private void countHeadlessRenderTraversal(Node node) {
    frame.diagnostics().increment(NvgDiagnosticCounter.RENDER_NODE_VISITS);
    for (Node child : node.childNodes()) countHeadlessRenderTraversal(child);
  }

  @Override
  public void close() {
    fontService.close();
  }
}
