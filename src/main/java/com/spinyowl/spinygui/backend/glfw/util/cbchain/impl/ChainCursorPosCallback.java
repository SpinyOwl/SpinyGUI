package com.spinyowl.spinygui.backend.glfw.util.cbchain.impl;

import com.spinyowl.spinygui.backend.glfw.util.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfw.util.cbchain.IChainCursorPosCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCursorPosCallback SetCursorPosCallback} method.
 */
public class ChainCursorPosCallback extends AbstractChainCallback<GLFWCursorPosCallbackI> implements IChainCursorPosCallback {
    public void invoke(long window, double xpos, double ypos) {
        callbackChain.forEach(c -> c.invoke(window, xpos, ypos));
    }
}