package com.spinyowl.spinygui.demo.simple;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWaitEventsTimeout;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import com.spinyowl.cbchain.impl.ChainCharCallback;
import com.spinyowl.cbchain.impl.ChainCursorEnterCallback;
import com.spinyowl.cbchain.impl.ChainCursorPosCallback;
import com.spinyowl.cbchain.impl.ChainKeyCallback;
import com.spinyowl.cbchain.impl.ChainMouseButtonCallback;
import com.spinyowl.cbchain.impl.ChainScrollCallback;
import com.spinyowl.cbchain.impl.ChainWindowSizeCallback;
import com.spinyowl.spinygui.GlfwSystemEventBridge;
import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglCallbackInstaller;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;

/** Native smoke harness for representative key-chain coexistence and attach-only detachment. */
public final class EmbeddedCallbackCoexistenceSmoke {

  /** Window title before any coexistence probe has been pressed. */
  private static final String INITIAL_TITLE =
      "KEY ATTACHED | press C, then D, then C | Escape closes";

  private EmbeddedCallbackCoexistenceSmoke() {}

  /** Runs the real-window key-chain ownership smoke; no SpinyGUI rendering context is required. */
  public static void main(String[] args) {
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    long window = 0L;
    LwjglCallbackInstaller.Registration bridgeRegistration = null;
    try {
      window = glfwCreateWindow(760, 220, INITIAL_TITLE, 0L, 0L);
      if (window == 0L) {
        throw new IllegalStateException("Unable to create GLFW smoke window");
      }

      Protocol protocol = new Protocol();
      GlfwSystemEventBridge.Chains chains = chains();
      chains.key().add(
          (callbackWindow, key, scancode, action, mods) -> {
            protocol.onCallerKey(key, action);
            if (protocol.closeRequested()) {
              glfwSetWindowShouldClose(callbackWindow, true);
            }
          });
      installCallerOwnedChains(window, chains);

      SystemEventProcessorImpl systemEvents = SystemEventProcessorImpl.create();
      GlfwSystemEventBridge bridge =
          GlfwSystemEventBridge.attached(
              new FrameNavigator(new Frame(), 1),
              systemEvents,
              (callbackWindow, key, action) -> protocol.onBridgeKey(key, action),
              chains);
      bridgeRegistration = bridge.install(window);
      glfwSetWindowTitle(window, protocol.title());

      while (!glfwWindowShouldClose(window)) {
        glfwWaitEventsTimeout(0.05);
        systemEvents.processEvents();
        if (protocol.takeDetachRequest()) {
          bridgeRegistration.close();
          bridgeRegistration = null;
          protocol.markDetached();
        }
        glfwSetWindowTitle(window, protocol.title());
      }
    } finally {
      try {
        try {
          if (bridgeRegistration != null) {
            bridgeRegistration.close();
          }
        } finally {
          if (window != 0L) {
            try {
              glfwFreeCallbacks(window);
            } finally {
              glfwDestroyWindow(window);
            }
          }
        }
      } finally {
        glfwTerminate();
      }
    }
  }

  private static GlfwSystemEventBridge.Chains chains() {
    return new GlfwSystemEventBridge.Chains(
        new ChainCursorPosCallback(),
        new ChainCursorEnterCallback(),
        new ChainWindowSizeCallback(),
        new ChainScrollCallback(),
        new ChainMouseButtonCallback(),
        new ChainCharCallback(),
        new ChainKeyCallback());
  }

  private static void installCallerOwnedChains(
      long window, GlfwSystemEventBridge.Chains chains) {
    glfwSetCursorPosCallback(window, chains.cursorPos());
    glfwSetCursorEnterCallback(window, chains.cursorEnter());
    glfwSetWindowSizeCallback(window, chains.windowSize());
    glfwSetScrollCallback(window, chains.scroll());
    glfwSetMouseButtonCallback(window, chains.mouseButton());
    glfwSetCharCallback(window, chains.character());
    glfwSetKeyCallback(window, chains.key());
  }

  /** Headlessly testable key protocol shared by the native title and callback handlers. */
  static final class Protocol {
    /** Number of C presses observed by the caller-owned key handler. */
    private int callerCount;
    /** Number of C presses observed by the bridge key policy while attached. */
    private int bridgeCount;
    /** Deferred detach request consumed only after callback iteration finishes. */
    private boolean detachRequested;
    /** True until the bridge registration has been closed. */
    private boolean attached = true;
    /** True after Escape asks the caller-owned window to close. */
    private boolean closeRequested;

    /** Records caller-chain keys and defers bridge detachment to the event loop. */
    void onCallerKey(int key, int action) {
      if (action != GLFW_PRESS) return;
      if (key == GLFW_KEY_C) callerCount++;
      if (key == GLFW_KEY_D && attached) detachRequested = true;
      if (key == GLFW_KEY_ESCAPE) closeRequested = true;
    }

    /** Records the same C probe through the attached SpinyGUI bridge policy. */
    void onBridgeKey(int key, int action) {
      if (key == GLFW_KEY_C && action == GLFW_PRESS) bridgeCount++;
    }

    /** Consumes one deferred detach request outside callback iteration. */
    boolean takeDetachRequest() {
      boolean requested = detachRequested;
      detachRequested = false;
      return requested;
    }

    /** Marks the state visible only after the bridge registration closes successfully. */
    void markDetached() {
      attached = false;
    }

    /** Returns whether the caller-owned window should close. */
    boolean closeRequested() {
      return closeRequested;
    }

    /** Returns the caller callback's C-probe count. */
    int callerCount() {
      return callerCount;
    }

    /** Returns the attached bridge policy's C-probe count. */
    int bridgeCount() {
      return bridgeCount;
    }

    /** Formats the observable native-window status. */
    String title() {
      String state = attached ? "KEY ATTACHED" : "KEY DETACHED";
      return state
          + " | C: caller="
          + callerCount
          + " bridge="
          + bridgeCount
          + (attached ? " | D detaches" : " | C now reaches caller only")
          + " | Escape closes";
    }
  }
}
