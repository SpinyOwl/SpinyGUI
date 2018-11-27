package com.spinyowl.spinygui.backend.glfwutil.callback;

import com.spinyowl.spinygui.backend.core.event.*;
import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.service.ServiceHolder;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;

public final class DefaultCallback {

    private DefaultCallback() {
    }

    /**
     * Creates default GLFWWindowSizeCallback.
     *
     * @return the GLFWWindowSizeCallback.
     */
    public static GLFWWindowSizeCallbackI createWindowSizeCallback() {
        return (window, width, height) -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowSizeEvent(window, width, height));
    }

    /**
     * Creates default GLFWWindowRefreshCallback.
     *
     * @return the GLFWWindowRefreshCallback.
     */
    public static GLFWWindowRefreshCallbackI createWindowRefreshCallback() {
        return window -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowRefreshEvent(window));
    }

    /**
     * Creates default GLFWWindowPosCallback.
     *
     * @return the GLFWWindowPosCallback.
     */
    public static GLFWWindowPosCallbackI createWindowPosCallback() {
        return (window, xpos, ypos) -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowPosEvent(window, xpos, ypos));
    }

    /**
     * Creates default GLFWWindowIconifyCallback.
     *
     * @return the GLFWWindowIconifyCallback.
     */
    public static GLFWWindowIconifyCallbackI createWindowIconifyCallback() {
        return (window, iconified) -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowIconifyEvent(window, iconified));
    }

    /**
     * Creates default GLFWWindowFocusCallback.
     *
     * @return the GLFWWindowFocusCallback.
     */
    public static GLFWWindowFocusCallbackI createWindowFocusCallback() {
        return (window, focused) -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowFocusEvent(window, focused));
    }

    /**
     * Creates default GLFWWindowCloseCallback.
     *
     * @return the GLFWWindowCloseCallback.
     */
    public static GLFWWindowCloseCallbackI createWindowCloseCallback() {
        return (window) -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowCloseEvent(window));
    }

    /**
     * Creates default GLFWCursorPosCallback.
     *
     * @return the GLFWCursorPosCallback.
     */
    public static GLFWCursorPosCallbackI createCursorPosCallback() {
        return (window, xpos, ypos) -> SystemEventProcessor.getInstance().pushEvent(new SystemCursorPosEvent(window, xpos, ypos));
    }

    /**
     * Creates default GLFWMouseButtonCallback.
     *
     * @return the GLFWMouseButtonCallback.
     */
    public static GLFWMouseButtonCallbackI createMouseButtonCallback() {
        return (window, button, action, mods) -> SystemEventProcessor.getInstance().pushEvent(new SystemMouseClickEvent(window, button, action, mods));
    }

    /**
     * Creates default GLFWFramebufferSizeCallback.
     *
     * @return the GLFWFramebufferSizeCallback.
     */
    public static GLFWFramebufferSizeCallbackI createFramebufferSizeCallback() {
        return (window, width, height) -> SystemEventProcessor.getInstance().pushEvent(new SystemFramebufferSizeEvent(window, width, height));
    }

    /**
     * Creates default GLFWCursorEnterCallback.
     *
     * @return the GLFWCursorEnterCallback.
     */
    public static GLFWCursorEnterCallbackI createCursorEnterCallback() {
        return (window, entered) -> SystemEventProcessor.getInstance().pushEvent(new SystemCursorEnterEvent(window, entered));
    }

    /**
     * Creates default GLFWCharModsCallback.
     *
     * @return the GLFWCharModsCallback.
     */
    public static GLFWCharModsCallbackI createCharModsCallback() {
        return (window, codepoint, mods) -> SystemEventProcessor.getInstance().pushEvent(new SystemCharModsEvent(window, codepoint, mods));
    }

    /**
     * Creates default GLFWScrollCallback.
     *
     * @return the GLFWScrollCallback.
     */
    public static GLFWScrollCallbackI createScrollCallback() {
        return (window, xoffset, yoffset) -> SystemEventProcessor.getInstance().pushEvent(new SystemScrollEvent(window, xoffset, yoffset));
    }

    /**
     * Creates default GLFWKeyCallback.
     *
     * @return the GLFWKeyCallback.
     */
    public static GLFWKeyCallbackI createKeyCallback() {
        return (window, key, scancode, action, mods) -> SystemEventProcessor.getInstance().pushEvent(new SystemKeyEvent(window, key, scancode, action, mods));
    }

    /**
     * Creates default GLFWDropCallback.
     *
     * @return the GLFWDropCallback.
     */
    public static GLFWDropCallbackI createDropCallback() {
        return (window, count, names) -> {
            PointerBuffer pb = PointerBuffer.create(names, count);
            String[] strings = new String[count];
            for (int i = 0; i < count; i++) {
                strings[i] = pb.getStringUTF8(i);
            }
            SystemEventProcessor.getInstance().pushEvent(new SystemDropEvent(window, strings));
        };
    }

    /**
     * Creates default GLFWCharCallback.
     *
     * @return the GLFWCharCallback.
     */
    public static GLFWCharCallbackI createCharCallback() {
        return (window, codepoint) -> SystemEventProcessor.getInstance().pushEvent(new SystemCharEvent(window, codepoint));
    }

    /**
     * Creates default GLFWWindowContentScaleCallbackI.
     *
     * @return the GLFWWindowContentScaleCallbackI.
     */
    public static GLFWWindowContentScaleCallbackI createWindowContentScaleCallback() {
        return (window, xscale, yscale) -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowContentScaleEvent(window, xscale, yscale));
    }

    /**
     * Creates default GLFWWindowMaximizeCallbackI.
     *
     * @return the GLFWWindowMaximizeCallbackI.
     */
    public static GLFWWindowMaximizeCallbackI createWindowMaximizeCallback() {
        return (window, maximized) -> SystemEventProcessor.getInstance().pushEvent(new SystemWindowMaximizeEvent(window, maximized));
    }
}
