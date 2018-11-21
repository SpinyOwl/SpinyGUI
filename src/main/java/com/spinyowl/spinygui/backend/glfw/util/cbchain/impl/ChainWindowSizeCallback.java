package com.spinyowl.spinygui.backend.glfw.util.cbchain.impl;

import com.spinyowl.spinygui.backend.glfw.util.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfw.util.cbchain.IChainWindowSizeCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowSizeCallback SetWindowSizeCallback} method.
 */
public class ChainWindowSizeCallback extends AbstractChainCallback<GLFWWindowSizeCallbackI> implements IChainWindowSizeCallback {
    @Override
    public void invoke(long window, int width, int height) {
        callbackChain.forEach(c -> c.invoke(window, width, height));
    }
}
