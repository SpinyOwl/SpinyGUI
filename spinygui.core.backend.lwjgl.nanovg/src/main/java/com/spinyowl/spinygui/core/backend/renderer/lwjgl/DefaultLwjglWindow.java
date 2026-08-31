package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_DOUBLEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CAPS_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_NUM_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;
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

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorEnterEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/** Single-window GLFW/OpenGL adapter for the default application wrapper. */
public final class DefaultLwjglWindow implements LwjglWindow {
  private final LwjglApplicationConfiguration configuration;
  private final Frame frame;
  private final SystemEventProcessor systemEvents;
  private long window;
  private boolean glfwInitialized;
  private boolean closed;
  private GLFWErrorCallback errorCallback;

  public DefaultLwjglWindow(
      LwjglApplicationConfiguration configuration,
      Frame frame,
      SystemEventProcessor systemEvents) {
    this.configuration = Objects.requireNonNull(configuration, "configuration");
    this.frame = Objects.requireNonNull(frame, "frame");
    this.systemEvents = Objects.requireNonNull(systemEvents, "systemEvents");
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
      close();
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
    if (window != NULL) {
      Callbacks.glfwFreeCallbacks(window);
      if (glfwGetCurrentContext() == window) {
        setCapabilities(null);
        glfwMakeContextCurrent(NULL);
      }
      glfwDestroyWindow(window);
      window = NULL;
    }
    if (glfwInitialized) {
      glfwTerminate();
      glfwInitialized = false;
    }
    if (errorCallback != null) {
      GLFWErrorCallback installed = glfwSetErrorCallback(null);
      if (installed != null) installed.free();
      errorCallback = null;
    }
  }

  private void installCallbacks() {
    glfwSetCursorPosCallback(
        window,
        (ignored, x, y) ->
            systemEvents.push(
                SystemCursorPosEvent.builder()
                    .frame(frame)
                    .posX((float) x)
                    .posY((float) y)
                    .build()));
    glfwSetCursorEnterCallback(
        window,
        (ignored, entered) ->
            systemEvents.push(
                SystemCursorEnterEvent.builder().frame(frame).entered(entered).build()));
    glfwSetWindowSizeCallback(
        window,
        (ignored, width, height) ->
            systemEvents.push(
                SystemWindowSizeEvent.builder()
                    .frame(frame)
                    .width(width)
                    .height(height)
                    .build()));
    glfwSetScrollCallback(
        window,
        (ignored, x, y) ->
            systemEvents.push(
                SystemScrollEvent.builder()
                    .frame(frame)
                    .offsetX((float) x)
                    .offsetY((float) y)
                    .build()));
    glfwSetMouseButtonCallback(
        window,
        (ignored, button, action, mods) -> {
          SystemMouseButton mappedButton = mapMouseButton(button);
          SystemKeyAction mappedAction = mapAction(action);
          if (mappedButton != null && mappedAction != null) {
            systemEvents.push(
                SystemMouseClickEvent.builder()
                    .frame(frame)
                    .button(mappedButton)
                    .action(mappedAction)
                    .mods(mapMods(mods))
                    .build());
          }
        });
    glfwSetCharCallback(
        window,
        (ignored, codepoint) ->
            systemEvents.push(
                SystemCharEvent.builder().frame(frame).codepoint(codepoint).build()));
    glfwSetKeyCallback(
        window,
        (ignored, key, scancode, action, mods) -> {
          if (key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
          }
          SystemKeyAction mappedAction = mapAction(action);
          if (mappedAction != null) {
            systemEvents.push(
                SystemKeyEvent.builder()
                    .frame(frame)
                    .keyCode(key)
                    .scancode(scancode)
                    .action(mappedAction)
                    .mods(mapMods(mods))
                    .build());
          }
        });
  }

  private void requireInitialized() {
    if (window == NULL || closed) throw new IllegalStateException("Window is not initialized");
  }

  private static SystemKeyAction mapAction(int action) {
    return switch (action) {
      case GLFW.GLFW_PRESS -> SystemKeyAction.PRESS;
      case GLFW.GLFW_RELEASE -> SystemKeyAction.RELEASE;
      case GLFW.GLFW_REPEAT -> SystemKeyAction.REPEAT;
      default -> null;
    };
  }

  private static SystemMouseButton mapMouseButton(int button) {
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

  private static ImmutableSet<SystemKeyMod> mapMods(int mods) {
    ImmutableSet.Builder<SystemKeyMod> mapped = ImmutableSet.builder();
    if ((mods & GLFW_MOD_SHIFT) != 0) mapped.add(SystemKeyMod.SHIFT);
    if ((mods & GLFW_MOD_CONTROL) != 0) mapped.add(SystemKeyMod.CONTROL);
    if ((mods & GLFW_MOD_ALT) != 0) mapped.add(SystemKeyMod.ALT);
    if ((mods & GLFW_MOD_SUPER) != 0) mapped.add(SystemKeyMod.SUPER);
    if ((mods & GLFW_MOD_CAPS_LOCK) != 0) mapped.add(SystemKeyMod.CAPS_LOCK);
    if ((mods & GLFW_MOD_NUM_LOCK) != 0) mapped.add(SystemKeyMod.NUM_LOCK);
    return mapped.build();
  }
}
