package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_END;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;

import com.spinyowl.spinygui.core.FramePipeline;
import com.spinyowl.spinygui.core.animation.TransitionCoordinator;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.clipboard.Clipboard;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
import com.spinyowl.spinygui.core.input.impl.ShortcutRegistryImpl;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.impl.LayoutServiceProvider;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorEnterEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemCharEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemCursorEnterEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemCursorPosEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemKeyEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemMouseClickEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemScrollEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemWindowSizeEventListener;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.Map;
import java.util.Objects;

/**
 * Default owner of the core services used by a single LWJGL frame pipeline.
 *
 * <p>The bundle is a convenience composition boundary. The individual services remain injectable
 * through the lower-level {@link AbstractLwjglApplication} API.
 */
public final class LwjglFrameServices implements AutoCloseable {
  private final Frame frame;
  private final TimeService timeService;
  private final StyleSheetParser styleSheetParser;
  private final NodeParser nodeParser;
  private final MouseServiceImpl mouseService;
  private final DefaultEventProcessor guiEvents;
  private final SystemEventProcessor systemEvents;
  private final FontService fontService;
  private final FramePipeline pipeline;
  private boolean closed;

  public LwjglFrameServices(Renderer renderer) {
    this(
        new Frame(),
        renderer,
        () -> System.nanoTime() / 1_000_000_000d,
        glfwClipboard(),
        defaultKeyboardLayout());
  }

  public LwjglFrameServices(
      Frame frame,
      Renderer renderer,
      TimeService timeService,
      Clipboard clipboard,
      KeyboardLayout keyboardLayout) {
    this.frame = Objects.requireNonNull(frame, "frame");
    this.timeService = Objects.requireNonNull(timeService, "timeService");
    Objects.requireNonNull(renderer, "renderer");
    Objects.requireNonNull(clipboard, "clipboard");
    Objects.requireNonNull(keyboardLayout, "keyboardLayout");

    TransitionCoordinator transitions = new TransitionCoordinator(timeService);
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    styleSheetParser = StyleSheetParserFactory.createParser(propertyStore);
    nodeParser = new DefaultNodeParser();
    StyleManager styles = new StyleManagerImpl(propertyStore, styleSheetParser, transitions);
    mouseService = new MouseServiceImpl();
    guiEvents = new DefaultEventProcessor();

    FontServiceImpl createdFontService = new FontServiceImpl(new FontStorageImpl(), true);
    createdFontService.installSemanticOwner();
    fontService = createdFontService;
    try {
      TextMeasurer textMeasurer = createdFontService;
      if (renderer instanceof NvgRenderer nvgRenderer) {
        nvgRenderer.textMeasurer(textMeasurer);
      }

      SystemEventListenerProviderImpl listeners =
          listeners(timeService, clipboard, keyboardLayout, textMeasurer);
      systemEvents =
          SystemEventProcessorImpl.builder().eventListenerProvider(listeners).build();
      LayoutService layout =
          LayoutServiceProvider.create(systemEvents, guiEvents, timeService, fontService);
      pipeline = new FramePipeline(systemEvents, guiEvents, styles, transitions, layout);
    } catch (RuntimeException failure) {
      createdFontService.close();
      throw failure;
    }
  }

  public Frame frame() {
    return frame;
  }

  public FramePipeline pipeline() {
    return pipeline;
  }

  public SystemEventProcessor systemEvents() {
    return systemEvents;
  }

  public MouseServiceImpl mouseService() {
    return mouseService;
  }

  public StyleSheetParser styleSheetParser() {
    return styleSheetParser;
  }

  public NodeParser nodeParser() {
    return nodeParser;
  }

  public TimeService timeService() {
    return timeService;
  }

  public void addStyleSheet(String css) {
    frame.styleSheets().add(styleSheetParser.parse(Objects.requireNonNull(css, "css")));
  }

  @Override
  public void close() {
    if (closed) return;
    closed = true;
    fontService.close();
  }

  private SystemEventListenerProviderImpl listeners(
      TimeService timeService,
      Clipboard clipboard,
      KeyboardLayout keyboardLayout,
      TextMeasurer textMeasurer) {
    SystemEventListenerProviderImpl listeners = new SystemEventListenerProviderImpl();
    ScrollbarInteraction scrollbarInteraction = new ScrollbarInteraction();
    listeners.listener(
        SystemCursorPosEvent.class,
        SystemCursorPosEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .scrollbarInteraction(scrollbarInteraction)
            .textMeasurer(textMeasurer)
            .build());
    listeners.listener(
        SystemCursorEnterEvent.class,
        SystemCursorEnterEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .build());
    listeners.listener(
        SystemWindowSizeEvent.class,
        SystemWindowSizeEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .build());
    listeners.listener(
        SystemScrollEvent.class,
        SystemScrollEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .textMeasurer(textMeasurer)
            .build());
    listeners.listener(
        SystemMouseClickEvent.class,
        SystemMouseClickEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .textMeasurer(textMeasurer)
            .scrollbarInteraction(scrollbarInteraction)
            .build());
    listeners.listener(
        SystemCharEvent.class,
        SystemCharEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .textMeasurer(textMeasurer)
            .build());
    listeners.listener(
        SystemKeyEvent.class,
        SystemKeyEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .keyboard(new Keyboard(keyboardLayout, new ShortcutRegistryImpl()))
            .clipboard(clipboard)
            .textMeasurer(textMeasurer)
            .build());
    return listeners;
  }

  private static KeyboardLayout defaultKeyboardLayout() {
    return new KeyboardLayoutImpl(
        Map.ofEntries(
            Map.entry(KeyCode.BACKSPACE, GLFW_KEY_BACKSPACE),
            Map.entry(KeyCode.DELETE, GLFW_KEY_DELETE),
            Map.entry(KeyCode.LEFT, GLFW_KEY_LEFT),
            Map.entry(KeyCode.RIGHT, GLFW_KEY_RIGHT),
            Map.entry(KeyCode.UP, GLFW_KEY_UP),
            Map.entry(KeyCode.DOWN, GLFW_KEY_DOWN),
            Map.entry(KeyCode.PAGE_UP, GLFW_KEY_PAGE_UP),
            Map.entry(KeyCode.PAGE_DOWN, GLFW_KEY_PAGE_DOWN),
            Map.entry(KeyCode.SPACE, GLFW_KEY_SPACE),
            Map.entry(KeyCode.ENTER, GLFW_KEY_ENTER),
            Map.entry(KeyCode.NUMPAD_ENTER, GLFW_KEY_KP_ENTER),
            Map.entry(KeyCode.TAB, GLFW_KEY_TAB),
            Map.entry(KeyCode.HOME, GLFW_KEY_HOME),
            Map.entry(KeyCode.END, GLFW_KEY_END),
            Map.entry(KeyCode.KEY_A, GLFW_KEY_A),
            Map.entry(KeyCode.KEY_C, GLFW_KEY_C),
            Map.entry(KeyCode.KEY_V, GLFW_KEY_V),
            Map.entry(KeyCode.KEY_X, GLFW_KEY_X),
            Map.entry(KeyCode.ESCAPE, GLFW_KEY_ESCAPE)));
  }

  private static Clipboard glfwClipboard() {
    return new Clipboard() {
      @Override
      public String getClipboardString() {
        long window = glfwGetCurrentContext();
        return window == 0 ? null : glfwGetClipboardString(window);
      }

      @Override
      public void setClipboardString(String string) {
        long window = glfwGetCurrentContext();
        if (window != 0) glfwSetClipboardString(window, string == null ? "" : string);
      }
    };
  }
}
