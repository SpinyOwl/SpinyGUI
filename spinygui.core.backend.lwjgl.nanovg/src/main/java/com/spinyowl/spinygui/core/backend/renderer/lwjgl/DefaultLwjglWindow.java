package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_DOUBLEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM;
import static org.lwjgl.glfw.GLFW.GLFW_PLATFORM_X11;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwInitHint;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;

/** Single-window GLFW/OpenGL adapter for the default application wrapper. */
public final class DefaultLwjglWindow implements LwjglWindow {
  /** Production adapter for the native resources exclusively owned by a standalone window. */
  private static final NativeCleanup SYSTEM_CLEANUP = new NativeCleanup() {
    @Override public void freeCallbacks(long window) { Callbacks.glfwFreeCallbacks(window); }
    @Override public long currentContext() { return glfwGetCurrentContext(); }
    @Override public void clearCapabilities() { setCapabilities(null); }
    @Override public void clearCurrentContext() { glfwMakeContextCurrent(NULL); }
    @Override public void destroyWindow(long window) { glfwDestroyWindow(window); }
    @Override public void terminateGlfw() { glfwTerminate(); }
    @Override public void clearErrorCallback() { DefaultLwjglWindow.clearErrorCallback(); }
  };
  private final LwjglApplicationConfiguration configuration;
  /** Installer retained for this window's lifetime and invoked once after context creation. */
  private final LwjglCallbackInstaller callbackInstaller;
  /** Native cleanup adapter retained for failure-safe teardown and deterministic testing. */
  private final NativeCleanup nativeCleanup;
  private long window;
  private boolean glfwInitialized;
  private boolean closed;
  private GLFWErrorCallback errorCallback;
  /** Installed callback ownership handle, closed before this window destroys native resources. */
  private LwjglCallbackInstaller.Registration callbackRegistration;

  public DefaultLwjglWindow(
      LwjglApplicationConfiguration configuration,
      Frame frame,
      SystemEventProcessor systemEvents) {
    this(
        configuration,
        directCallbacks(
            new GlfwSystemEventMapper(
                new FrameNavigator(Objects.requireNonNull(frame, "frame"), 1),
                Objects.requireNonNull(systemEvents, "systemEvents"),
                (window, key, action) -> {
                  if (key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                  }
                })));
  }

  /** Creates a standalone window that delegates callback installation to {@code callbackInstaller}. */
  public DefaultLwjglWindow(
      LwjglApplicationConfiguration configuration, LwjglCallbackInstaller callbackInstaller) {
    this(configuration, callbackInstaller, SYSTEM_CLEANUP);
  }

  DefaultLwjglWindow(
      LwjglApplicationConfiguration configuration,
      LwjglCallbackInstaller callbackInstaller,
      NativeCleanup nativeCleanup) {
    this.configuration = Objects.requireNonNull(configuration, "configuration");
    this.callbackInstaller = Objects.requireNonNull(callbackInstaller, "callbackInstaller");
    this.nativeCleanup = Objects.requireNonNull(nativeCleanup, "nativeCleanup");
  }

  @Override
  public void initialize() {
    if (window != NULL || glfwInitialized) {
      throw new IllegalStateException("Window is already initialized");
    }
    if (closed) throw new IllegalStateException("Window is already closed");
    System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
    System.setProperty("java.awt.headless", Boolean.TRUE.toString());
    try {
      errorCallback = GLFWErrorCallback.createPrint(System.err);
      glfwSetErrorCallback(errorCallback);
      if (System.getProperty("os.name", "").toLowerCase().contains("linux")) {
        glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11);
      }
      if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
      glfwInitialized = true;

      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
      glfwWindowHint(GLFW_RESIZABLE, configuration.resizable() ? GLFW_TRUE : GLFW_FALSE);
      glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
      glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
      window =
          glfwCreateWindow(
              configuration.width(), configuration.height(), configuration.title(), NULL, NULL);
      if (window == NULL) throw new IllegalStateException("Unable to create GLFW window");

      glfwMakeContextCurrent(window);
      createCapabilities();
      glfwSwapInterval(configuration.vSync() ? 1 : 0);
      installCallbacks();
      glfwShowWindow(window);
    } catch (RuntimeException failure) {
      try {
        close();
      } catch (RuntimeException cleanupFailure) {
        failure.addSuppressed(cleanupFailure);
      }
      throw failure;
    }
  }

  @Override
  public boolean shouldClose() {
    requireInitialized();
    return glfwWindowShouldClose(window);
  }

  @Override
  public void pollEvents() {
    requireInitialized();
    glfwPollEvents();
  }

  @Override
  public long handle() {
    requireInitialized();
    return window;
  }

  @Override
  public Vector2f windowSize() {
    requireInitialized();
    int[] width = {0};
    int[] height = {0};
    glfwGetWindowSize(window, width, height);
    return new Vector2f(width[0], height[0]);
  }

  @Override
  public Vector2i framebufferSize() {
    requireInitialized();
    int[] width = {0};
    int[] height = {0};
    glfwGetFramebufferSize(window, width, height);
    return new Vector2i(width[0], height[0]);
  }

  @Override
  public void beginRender(Vector2i framebufferSize) {
    requireInitialized();
    glClearColor(1f, 1f, 1f, 1f);
    glViewport(0, 0, framebufferSize.x, framebufferSize.y);
    glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
  }

  @Override
  public void swapBuffers() {
    requireInitialized();
    glfwSwapBuffers(window);
  }

  @Override
  public void close() {
    if (closed) return;
    closed = true;
    RuntimeException failure = null;
    if (window != NULL) {
      if (callbackRegistration != null) {
        LwjglCallbackInstaller.Registration registration = callbackRegistration;
        callbackRegistration = null;
        failure = cleanup(failure, registration::close);
      }
      long ownedWindow = window;
      failure = cleanup(failure, () -> nativeCleanup.freeCallbacks(ownedWindow));
      boolean ownsCurrentContext = false;
      try {
        ownsCurrentContext = nativeCleanup.currentContext() == ownedWindow;
      } catch (RuntimeException cleanupFailure) {
        failure = record(failure, cleanupFailure);
      }
      if (ownsCurrentContext) {
        failure = cleanup(failure, nativeCleanup::clearCapabilities);
        failure = cleanup(failure, nativeCleanup::clearCurrentContext);
      }
      failure = cleanup(failure, () -> nativeCleanup.destroyWindow(ownedWindow));
      window = NULL;
    }
    if (glfwInitialized) {
      glfwInitialized = false;
      failure = cleanup(failure, nativeCleanup::terminateGlfw);
    }
    if (errorCallback != null) {
      errorCallback = null;
      failure = cleanup(failure, nativeCleanup::clearErrorCallback);
    }
    if (failure != null) throw failure;
  }

  private static RuntimeException cleanup(RuntimeException failure, Runnable cleanup) {
    try {
      cleanup.run();
      return failure;
    } catch (RuntimeException cleanupFailure) {
      return record(failure, cleanupFailure);
    }
  }

  private static void clearErrorCallback() {
    GLFWErrorCallback installed = glfwSetErrorCallback(null);
    if (installed != null) installed.free();
  }

  private static RuntimeException record(
      RuntimeException failure, RuntimeException cleanupFailure) {
    if (failure == null) return cleanupFailure;
    failure.addSuppressed(cleanupFailure);
    return failure;
  }

  private void installCallbacks() {
    callbackRegistration =
        Objects.requireNonNull(callbackInstaller.install(window), "callback registration");
  }

  private void requireInitialized() {
    if (window == NULL || closed) throw new IllegalStateException("Window is not initialized");
  }

  private static LwjglCallbackInstaller directCallbacks(GlfwSystemEventMapper mapper) {
    return window -> {
      GlfwSystemEventMapper.Callbacks callbacks = mapper.callbacks();
      try {
        glfwSetCursorPosCallback(window, callbacks.cursorPos());
        glfwSetCursorEnterCallback(window, callbacks.cursorEnter());
        glfwSetWindowSizeCallback(window, callbacks.windowSize());
        glfwSetScrollCallback(window, callbacks.scroll());
        glfwSetMouseButtonCallback(window, callbacks.mouseButton());
        glfwSetCharCallback(window, callbacks.character());
        glfwSetKeyCallback(window, callbacks.key());
        return () -> unsetDirectCallbacks(window);
      } catch (RuntimeException failure) {
        unsetDirectCallbacks(window);
        throw failure;
      }
    };
  }

  private static void unsetDirectCallbacks(long window) {
    free(glfwSetKeyCallback(window, null));
    free(glfwSetCharCallback(window, null));
    free(glfwSetMouseButtonCallback(window, null));
    free(glfwSetScrollCallback(window, null));
    free(glfwSetWindowSizeCallback(window, null));
    free(glfwSetCursorEnterCallback(window, null));
    free(glfwSetCursorPosCallback(window, null));
  }

  private static void free(org.lwjgl.system.Callback callback) {
    if (callback != null) callback.free();
  }

  /** Failure-injectable native teardown boundary used only after ownership has been established. */
  interface NativeCleanup {
    void freeCallbacks(long window);
    long currentContext();
    void clearCapabilities();
    void clearCurrentContext();
    void destroyWindow(long window);
    void terminateGlfw();
    void clearErrorCallback();
  }
}
