package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CAPS_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_NUM_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.FrameNavigator;
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
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

/** Authoritative GLFW-value to queued {@code SystemEvent} mapping. */
public final class GlfwSystemEventMapper {
  /** Navigator consulted once at each callback capture. */
  private final FrameNavigator navigator;
  /** Queue receiving fully targeted events; events are never processed inline. */
  private final SystemEventProcessor systemEvents;
  /** Explicit host policy invoked for every raw key callback before event filtering. */
  private final KeyPolicy keyPolicy;
  /** Stable callback identities used for registration and exact removal. */
  private final Callbacks callbacks;

  /** Creates a mapper for one navigator, queue, and explicit key policy. */
  public GlfwSystemEventMapper(
      FrameNavigator navigator, SystemEventProcessor systemEvents, KeyPolicy keyPolicy) {
    this.navigator = Objects.requireNonNull(navigator, "navigator");
    this.systemEvents = Objects.requireNonNull(systemEvents, "systemEvents");
    this.keyPolicy = Objects.requireNonNull(keyPolicy, "keyPolicy");
    callbacks = new Callbacks(this::cursorPos, this::cursorEnter, this::windowSize,
        this::scroll, this::mouseButton, this::character, this::key);
  }

  /** Returns the stable callback set for installation or chain attachment. */
  public Callbacks callbacks() {
    return callbacks;
  }

  private void cursorPos(long window, double x, double y) {
    systemEvents.push(SystemCursorPosEvent.builder().frame(navigator.currentFrame())
        .posX((float) x).posY((float) y).build());
  }

  private void cursorEnter(long window, boolean entered) {
    systemEvents.push(SystemCursorEnterEvent.builder().frame(navigator.currentFrame())
        .entered(entered).build());
  }

  private void windowSize(long window, int width, int height) {
    systemEvents.push(SystemWindowSizeEvent.builder().frame(navigator.currentFrame())
        .width(width).height(height).build());
  }

  private void scroll(long window, double x, double y) {
    systemEvents.push(SystemScrollEvent.builder().frame(navigator.currentFrame())
        .offsetX((float) x).offsetY((float) y).build());
  }

  private void mouseButton(long window, int button, int action, int mods) {
    SystemMouseButton mappedButton = mapMouseButton(button);
    SystemKeyAction mappedAction = mapAction(action);
    if (mappedButton != null && mappedAction != null) {
      systemEvents.push(SystemMouseClickEvent.builder().frame(navigator.currentFrame())
          .button(mappedButton).action(mappedAction).mods(mapMods(mods)).build());
    }
  }

  private void character(long window, int codepoint) {
    systemEvents.push(SystemCharEvent.builder().frame(navigator.currentFrame())
        .codepoint(codepoint).build());
  }

  private void key(long window, int key, int scancode, int action, int mods) {
    var frame = navigator.currentFrame();
    keyPolicy.onKey(window, key, action);
    SystemKeyAction mappedAction = mapAction(action);
    if (mappedAction != null) {
      systemEvents.push(SystemKeyEvent.builder().frame(frame).keyCode(key)
          .scancode(scancode).action(mappedAction).mods(mapMods(mods)).build());
    }
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

  /** Explicit host action for raw key callbacks, separate from event mapping. */
  @FunctionalInterface
  public interface KeyPolicy {
    /** Applies host behavior without changing whether a supported key event is queued. */
    void onKey(long window, int key, int action);

    /** Returns a policy with no host-side key behavior. */
    static KeyPolicy none() {
      return (window, key, action) -> { };
    }
  }

  /** Stable typed GLFW callbacks representing the complete supported window event set. */
  public record Callbacks(
      GLFWCursorPosCallbackI cursorPos,
      GLFWCursorEnterCallbackI cursorEnter,
      GLFWWindowSizeCallbackI windowSize,
      GLFWScrollCallbackI scroll,
      GLFWMouseButtonCallbackI mouseButton,
      GLFWCharCallbackI character,
      GLFWKeyCallbackI key) {
    /** Rejects null callback identities because teardown depends on exact identity. */
    public Callbacks {
      Objects.requireNonNull(cursorPos, "cursorPos");
      Objects.requireNonNull(cursorEnter, "cursorEnter");
      Objects.requireNonNull(windowSize, "windowSize");
      Objects.requireNonNull(scroll, "scroll");
      Objects.requireNonNull(mouseButton, "mouseButton");
      Objects.requireNonNull(character, "character");
      Objects.requireNonNull(key, "key");
    }
  }
}
