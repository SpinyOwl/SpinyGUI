package com.spinyowl.spinygui.demo.complex;

import static org.lwjgl.glfw.GLFW.GLFW_DOUBLEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.cbchain.impl.ChainCharCallback;
import com.spinyowl.cbchain.impl.ChainCursorEnterCallback;
import com.spinyowl.cbchain.impl.ChainCursorPosCallback;
import com.spinyowl.cbchain.impl.ChainErrorCallback;
import com.spinyowl.cbchain.impl.ChainKeyCallback;
import com.spinyowl.cbchain.impl.ChainMouseButtonCallback;
import com.spinyowl.cbchain.impl.ChainScrollCallback;
import com.spinyowl.cbchain.impl.ChainWindowCloseCallback;
import com.spinyowl.cbchain.impl.ChainWindowSizeCallback;
import com.spinyowl.spinygui.core.animation.Animator;
import com.spinyowl.spinygui.core.animation.AnimatorImpl;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
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
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStoreProvider;
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
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import com.spinyowl.spinygui.core.time.TimeService;
import java.time.Instant;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLCapabilities;

public abstract class Demo {

  private final int width;
  private final int height;
  private final String title;
  private final Renderer renderer;
  protected StyleSheetParser styleSheetParser;
  protected NodeParser nodeParser;
  private boolean running = false;
  // Need to initialize
  private Animator animator;
  private TimeService timeService;
  private EventProcessor eventProcessor;
  private SystemEventProcessor systemEventProcessor;
  private LayoutService layoutService;
  private StyleManager styleManager;

  private Frame frame;
  private long window;
  private MouseServiceImpl mouseService;

  protected Demo(int width, int height, String title, Renderer renderer) {
    this.width = width;
    this.height = height;
    this.title = title;
    this.renderer = renderer;
  }

  public void run() {
    System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
    System.setProperty("java.awt.headless", Boolean.TRUE.toString());
    initialize();
    work();
    destroy();
  }

  private void work() {
    glfwMakeContextCurrent(window);
    GLCapabilities glCapabilities = createCapabilities();

    renderer.initialize();

    glfwMakeContextCurrent(window);
    setCapabilities(glCapabilities);
    glfwSwapInterval(0); // disable vsync

    long millis = System.currentTimeMillis();
    int fps = 0;
    while (running) {
      tick();
      fps++;

      long now = System.currentTimeMillis();
      if (now >= millis + 1000) {
        GLFW.glfwSetWindowTitle(window, "FPS: " + fps);

        millis = now;
        fps = 0;
      }
    }

    renderer.destroy();
  }

  private void tick() {
    try {
      glClearColor(1, 1, 1, 1);

      int[] ww = {0};
      int[] wh = {0};
      int[] bw = {0};
      int[] bh = {0};
      int[] wpx = {0};
      int[] wpy = {0};
      glfwGetWindowSize(window, ww, wh);
      var windowSize = new Vector2f(ww[0], wh[0]);

      glfwGetFramebufferSize(window, bw, bh);
      var framebufferSize = new Vector2i(bw[0], bh[0]);

      glfwGetWindowPos(window, wpx, wpy);
      glViewport(0, 0, framebufferSize.x, framebufferSize.y);

      // frame size should be directly specified as it is not updated by layout service.
      updateFrameDimensions(windowSize);
      // We need to recalculate styles first.
      styleManager.recalculate(frame);

      // We need to relayout components after styles changed.
      layoutService.layout(frame);

      // After that we can render.
      glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
      if (renderer instanceof NvgRenderer nvg) {
        nvg.debugMousePosition(mouseService.getCursorPositions(frame).current());
      }
      renderer.render(window, windowSize, framebufferSize, frame);
      glfwSwapBuffers(window);

      // update system. could be moved for example to game loop.
      update();

      // also we need to run animations
      animator.runAnimations();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // poll system events
    glfwPollEvents();

    // process system events and generated gui events
    systemEventProcessor.processEvents();
    eventProcessor.processEvents();
  }

  private void updateFrameDimensions(Vector2f windowSize) {
    frame.frameSize(windowSize.x, windowSize.y);
  }

  @SuppressWarnings("squid:S112")
  private void initialize() {
    if (!GLFW.glfwInit()) {
      throw new RuntimeException("Can't initialize GLFW");
    }

    initializeServices();

    frame = createGuiElements(width, height);

    window = glfwCreateWindow(width, height, title, NULL, NULL);
    glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
    glfwSetWindowPos(window, 50, 50);

    initializeCallbacks(window);
    glfwShowWindow(window);

    running = true;
  }

  private void initializeServices() {
    timeService =
        () -> {
          var now = Instant.now();
          return now.getEpochSecond() + (now.getNano() / 1_000_000_000D);
        };

    animator = new AnimatorImpl(timeService);

    PropertyStoreProvider provider = new DefaultPropertyStoreProvider();
    PropertyStore propertyStore = provider.createPropertyStore();
    styleSheetParser = StyleSheetParserFactory.createParser(propertyStore);
    nodeParser = new DefaultNodeParser();
    styleManager = new StyleManagerImpl(propertyStore, styleSheetParser);
    mouseService = new MouseServiceImpl();
    eventProcessor = new DefaultEventProcessor();

    FontStorage fontStorage = new FontStorageImpl();
    FontService fontService = new FontServiceImpl(fontStorage, true);
    if (renderer instanceof NvgRenderer nvg && fontService instanceof TextMeasurer textMeasurer) {
      nvg.textMeasurer(textMeasurer);
    }

    initializeSystemEventListener(
        fontService instanceof TextMeasurer textMeasurer ? textMeasurer : null);

    layoutService =
        LayoutServiceProvider.create(
            systemEventProcessor, eventProcessor, timeService, fontService);
  }

  private void initializeSystemEventListener(TextMeasurer textMeasurer) {
    SystemEventListenerProviderImpl systemEventListenerProvider =
        new SystemEventListenerProviderImpl();
    systemEventListenerProvider.listener(
        SystemCursorPosEvent.class,
        SystemCursorPosEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build());
    systemEventListenerProvider.listener(
        SystemCursorEnterEvent.class,
        SystemCursorEnterEventListener.builder()
            .eventProcessor(eventProcessor)
            .mouseService(mouseService)
            .timeService(timeService)
            .build());
    systemEventListenerProvider.listener(
        SystemWindowSizeEvent.class,
        SystemWindowSizeEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build());
    systemEventListenerProvider.listener(
        SystemScrollEvent.class,
        SystemScrollEventListener.builder()
            .mouseService(mouseService)
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build());
    systemEventListenerProvider.listener(
        SystemMouseClickEvent.class,
        SystemMouseClickEventListener.builder()
            .mouseService(mouseService)
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .textMeasurer(textMeasurer)
            .build());
    systemEventListenerProvider.listener(
        SystemCharEvent.class,
        SystemCharEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build());
    systemEventListenerProvider.listener(
        SystemKeyEvent.class,
        SystemKeyEventListener.builder()
            .keyboard(new Keyboard(defaultKeyboardLayout(), new ShortcutRegistryImpl()))
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .build());

    systemEventProcessor =
        SystemEventProcessorImpl.builder()
            .eventListenerProvider(systemEventListenerProvider)
            .build();
  }

  @SuppressWarnings({"squid:S1215", "squid:S106"})
  private void initializeCallbacks(long window) {
    var errorCallback = new ChainErrorCallback();
    errorCallback.add(GLFWErrorCallback.createPrint(System.err));
    errorCallback.add(GLFWErrorCallback.createThrow());
    glfwSetErrorCallback(errorCallback);

    var chainWindowCloseCallback = new ChainWindowCloseCallback();
    chainWindowCloseCallback.add(w -> running = false);
    glfwSetWindowCloseCallback(window, chainWindowCloseCallback);

    var chainCursorPosCallback = new ChainCursorPosCallback();
    chainCursorPosCallback.add(
        (w, x, y) ->
            systemEventProcessor.push(
                SystemCursorPosEvent.builder()
                    .frame(frame)
                    .posX((float) x)
                    .posY((float) y)
                    .build()));
    glfwSetCursorPosCallback(window, chainCursorPosCallback);

    var chainCursorEnterCallback = new ChainCursorEnterCallback();
    chainCursorEnterCallback.add(
        (w, entered) ->
            systemEventProcessor.push(
                SystemCursorEnterEvent.builder().frame(frame).entered(entered).build()));
    glfwSetCursorEnterCallback(window, chainCursorEnterCallback);

    var chainWindowSizeCallback = new ChainWindowSizeCallback();
    chainWindowSizeCallback.add(
        (w, wid, hei) ->
            systemEventProcessor.push(
                SystemWindowSizeEvent.builder().width(wid).height(hei).frame(frame).build()));
    glfwSetWindowSizeCallback(window, chainWindowSizeCallback);

    var chainScrollCallback = new ChainScrollCallback();
    chainScrollCallback.add(
        (w, x, y) ->
            systemEventProcessor.push(
                SystemScrollEvent.builder()
                    .frame(frame)
                    .offsetX((float) x)
                    .offsetY((float) y)
                    .build()));
    glfwSetScrollCallback(window, chainScrollCallback);

    var chainMouseButtonCallback = new ChainMouseButtonCallback();
    chainMouseButtonCallback.add(
        (w, button, action, mods) -> {
          SystemMouseButton mouseButton = mapMouseButton(button);
          SystemKeyAction keyAction = mapAction(action);
          if (mouseButton != null && keyAction != null) {
            systemEventProcessor.push(
                SystemMouseClickEvent.builder()
                    .frame(frame)
                    .button(mouseButton)
                    .action(keyAction)
                    .mods(mapMods(mods))
                    .build());
          }
        });
    glfwSetMouseButtonCallback(window, chainMouseButtonCallback);

    var chainCharCallback = new ChainCharCallback();
    chainCharCallback.add(
        (w, codepoint) ->
            systemEventProcessor.push(
                SystemCharEvent.builder().frame(frame).codepoint(codepoint).build()));
    glfwSetCharCallback(window, chainCharCallback);

    var chainKeyCallback = new ChainKeyCallback();
    chainKeyCallback.add(
        (w1, key, code, action, mods) -> {
          if (key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE) stop();
        });
    chainKeyCallback.add(
        (w, key, code, action, mods) -> {
          if (key == GLFW_KEY_F3 && action == GLFW_RELEASE && renderer instanceof NvgRenderer nvg) {
            nvg.toggleDebugMode();
          }
        });
    chainKeyCallback.add(
        (w, key, code, action, mods) -> {
          if (key == GLFW_KEY_G && action == GLFW_RELEASE) Runtime.getRuntime().gc();
        });
    chainKeyCallback.add(
        (w, key, code, action, mods) -> {
          SystemKeyAction keyAction = mapAction(action);
          if (keyAction != null) {
            systemEventProcessor.push(
                SystemKeyEvent.builder()
                    .frame(frame)
                    .keyCode(key)
                    .scancode(code)
                    .action(keyAction)
                    .mods(mapMods(mods))
                    .build());
          }
        });
    glfwSetKeyCallback(window, chainKeyCallback);
  }

  private KeyboardLayout defaultKeyboardLayout() {
    return new KeyboardLayoutImpl(
        Map.ofEntries(
            Map.entry(KeyCode.BACKSPACE, GLFW.GLFW_KEY_BACKSPACE),
            Map.entry(KeyCode.DELETE, GLFW.GLFW_KEY_DELETE),
            Map.entry(KeyCode.LEFT, GLFW.GLFW_KEY_LEFT),
            Map.entry(KeyCode.RIGHT, GLFW.GLFW_KEY_RIGHT),
            Map.entry(KeyCode.HOME, GLFW.GLFW_KEY_HOME),
            Map.entry(KeyCode.END, GLFW.GLFW_KEY_END),
            Map.entry(KeyCode.KEY_F3, GLFW.GLFW_KEY_F3),
            Map.entry(KeyCode.KEY_G, GLFW.GLFW_KEY_G),
            Map.entry(KeyCode.ESCAPE, GLFW.GLFW_KEY_ESCAPE)));
  }

  private SystemKeyAction mapAction(int action) {
    return switch (action) {
      case GLFW_PRESS -> SystemKeyAction.PRESS;
      case GLFW_RELEASE -> SystemKeyAction.RELEASE;
      case GLFW_REPEAT -> SystemKeyAction.REPEAT;
      default -> null;
    };
  }

  private SystemMouseButton mapMouseButton(int button) {
    return switch (button) {
      case GLFW.GLFW_MOUSE_BUTTON_1 -> SystemMouseButton.MOUSE_BUTTON_1;
      case GLFW.GLFW_MOUSE_BUTTON_2 -> SystemMouseButton.MOUSE_BUTTON_2;
      case GLFW.GLFW_MOUSE_BUTTON_3 -> SystemMouseButton.MOUSE_BUTTON_3;
      case GLFW.GLFW_MOUSE_BUTTON_4 -> SystemMouseButton.MOUSE_BUTTON_4;
      case GLFW.GLFW_MOUSE_BUTTON_5 -> SystemMouseButton.MOUSE_BUTTON_5;
      case GLFW.GLFW_MOUSE_BUTTON_6 -> SystemMouseButton.MOUSE_BUTTON_6;
      case GLFW.GLFW_MOUSE_BUTTON_7 -> SystemMouseButton.MOUSE_BUTTON_7;
      case GLFW.GLFW_MOUSE_BUTTON_8 -> SystemMouseButton.MOUSE_BUTTON_8;
      default -> null;
    };
  }

  private ImmutableSet<SystemKeyMod> mapMods(int mods) {
    ImmutableSet.Builder<SystemKeyMod> builder = ImmutableSet.builder();
    if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) builder.add(SystemKeyMod.SHIFT);
    if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) builder.add(SystemKeyMod.CONTROL);
    if ((mods & GLFW.GLFW_MOD_ALT) != 0) builder.add(SystemKeyMod.ALT);
    if ((mods & GLFW.GLFW_MOD_SUPER) != 0) builder.add(SystemKeyMod.SUPER);
    if ((mods & GLFW.GLFW_MOD_CAPS_LOCK) != 0) builder.add(SystemKeyMod.CAPS_LOCK);
    if ((mods & GLFW.GLFW_MOD_NUM_LOCK) != 0) builder.add(SystemKeyMod.NUM_LOCK);
    return builder.build();
  }

  private void destroy() {
    glfwDestroyWindow(window);
    glfwTerminate();
  }

  protected void stop() {
    running = false;
  }

  protected void update() {
    // could be implemented for further update logic.
  }

  protected abstract Frame createGuiElements(int width, int height);
}
