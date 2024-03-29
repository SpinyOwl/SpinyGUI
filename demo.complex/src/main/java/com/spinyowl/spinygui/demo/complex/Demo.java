package com.spinyowl.spinygui.demo.complex;

import static org.lwjgl.glfw.GLFW.GLFW_DOUBLEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
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

import com.spinyowl.cbchain.impl.ChainCursorEnterCallback;
import com.spinyowl.cbchain.impl.ChainCursorPosCallback;
import com.spinyowl.cbchain.impl.ChainErrorCallback;
import com.spinyowl.cbchain.impl.ChainKeyCallback;
import com.spinyowl.cbchain.impl.ChainScrollCallback;
import com.spinyowl.cbchain.impl.ChainWindowCloseCallback;
import com.spinyowl.cbchain.impl.ChainWindowSizeCallback;
import com.spinyowl.spinygui.core.animation.Animator;
import com.spinyowl.spinygui.core.animation.AnimatorImpl;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
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
import com.spinyowl.spinygui.core.system.event.SystemCursorEnterEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemCursorEnterEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemCursorPosEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemScrollEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemWindowSizeEventListener;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.time.TimeService;
import java.time.Instant;
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

    initializeSystemEventListener();

    FontStorage fontStorage = new FontStorageImpl();
    FontService fontService = new FontServiceImpl(fontStorage, true);
    layoutService =
        LayoutServiceProvider.create(
            systemEventProcessor, eventProcessor, timeService, fontService);
  }

  private void initializeSystemEventListener() {
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

    var chainKeyCallback = new ChainKeyCallback();
    chainKeyCallback.add(
        (w1, key, code, action, mods) -> {
          if (key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE) stop();
        });
    chainKeyCallback.add(
        (w, key, code, action, mods) -> {
          if (key == GLFW_KEY_G && action == GLFW_RELEASE) Runtime.getRuntime().gc();
        });
    glfwSetKeyCallback(window, chainKeyCallback);
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
