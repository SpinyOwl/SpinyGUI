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
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
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
import com.spinyowl.cbchain.impl.ChainCursorPosCallback;
import com.spinyowl.cbchain.impl.ChainErrorCallback;
import com.spinyowl.cbchain.impl.ChainKeyCallback;
import com.spinyowl.cbchain.impl.ChainWindowCloseCallback;
import com.spinyowl.spinygui.core.animation.Animator;
import com.spinyowl.spinygui.core.animation.AnimatorImpl;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.LayoutTree;
import com.spinyowl.spinygui.core.layout.impl.LayoutProviderImpl;
import com.spinyowl.spinygui.core.layout.impl.LayoutServiceImpl;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.AbstractStyleManager;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.PropertiesScanner;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStore;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemCursorPosEventListener;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.time.TimeService;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
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
  private AbstractStyleManager styleManager;
  //  private CallbackKeeper keeper;
  private GLCapabilities glCapabilities;

  private long[] monitors;

  private Frame frame;
  private long window;
  private SystemEventListenerProviderImpl systemEventListenerProvider;
  private MouseServiceImpl mouseService;
  private LayoutProviderImpl layoutProvider;

  public Demo(int width, int height, String title, Renderer renderer) {
    this.width = width;
    this.height = height;
    this.title = title;
    this.renderer = renderer;
  }

  private static void sleep(long sleepTime) {
    try {
      TimeUnit.NANOSECONDS.sleep(sleepTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
    glCapabilities = createCapabilities();

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
      var windowPos = new Vector2i(wpx[0], wpy[0]);
      glViewport(0, 0, framebufferSize.x, framebufferSize.y);

      // frame size should be directly specified as it is not updated by layout service.
      updateFrameDimensions(windowSize);
      // We need to recalculate styles first.
      styleManager.recalculate(frame);

      // We need to relayout components after styles changed.
      LayoutTree layoutTree = layoutService.layout(frame);

      // After that we can render.
      glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
      renderer.render(window, windowSize, framebufferSize, layoutTree);
      glfwSwapBuffers(window);

      // update system. could be moved for example to game loop.
      update();

      // also we need to run animations
      animator.runAnimations();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    // poll system events
    glfwPollEvents();

    // process system events and generated gui events
    systemEventProcessor.processEvents();
    eventProcessor.processEvents();
    //    System.gc();
  }

  private void updateFrameDimensions(Vector2f windowSize) {
    frame.frameSize(windowSize.x, windowSize.y);
  }

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

    PropertyStore propertyStore = new DefaultPropertyStore();
    PropertiesScanner.fillPropertyStore(propertyStore);
    styleSheetParser = StyleSheetParserFactory.createParser(propertyStore);
    nodeParser = new DefaultNodeParser();
    styleManager = new StyleManagerImpl(propertyStore, styleSheetParser);
    mouseService = new MouseServiceImpl();
    eventProcessor = new DefaultEventProcessor();

    initializeSystemEventListener();

    layoutProvider = new LayoutProviderImpl(systemEventProcessor, eventProcessor, timeService);
    layoutService = new LayoutServiceImpl(layoutProvider);
  }

  private void initializeSystemEventListener() {
    systemEventListenerProvider = new SystemEventListenerProviderImpl();
    systemEventListenerProvider.listener(
        SystemCursorPosEvent.class,
        SystemCursorPosEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .mouseService(mouseService)
            .build());
    systemEventProcessor =
        SystemEventProcessorImpl.builder()
            .eventListenerProvider(systemEventListenerProvider)
            .build();
  }

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

    var chainKeyCallback = new ChainKeyCallback();
    chainKeyCallback.add(
        (w1, key, code, action, mods) ->
            running = !(key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE));
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
