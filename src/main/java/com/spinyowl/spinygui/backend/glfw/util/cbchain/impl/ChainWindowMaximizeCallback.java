package com.spinyowl.spinygui.backend.glfw.util.cbchain.impl;

import com.spinyowl.spinygui.backend.glfw.util.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfw.util.cbchain.IChainWindowMaximizeCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowMaximizeCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowMaximizeCallback SetWindowMaximizeCallback} method.
 */
public class ChainWindowMaximizeCallback extends AbstractChainCallback<GLFWWindowMaximizeCallbackI> implements IChainWindowMaximizeCallback {
    /**
     * Will be called when the specified window is maximized or restored.
     *
     * @param window    the window that was maximized or restored.
     * @param maximized {@link GLFW#GLFW_TRUE TRUE} if the window was maximized, or {@link GLFW#GLFW_FALSE FALSE} if it was restored
     */
    @Override
    public void invoke(long window, boolean maximized) {
        callbackChain.forEach(c -> c.invoke(window, maximized));
    }
}
