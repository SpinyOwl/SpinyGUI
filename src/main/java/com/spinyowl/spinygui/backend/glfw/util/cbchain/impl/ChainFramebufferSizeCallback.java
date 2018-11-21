package com.spinyowl.spinygui.backend.glfw.util.cbchain.impl;

import com.spinyowl.spinygui.backend.glfw.util.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfw.util.cbchain.IChainFramebufferSizeCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetFramebufferSizeCallback SetFramebufferSizeCallback} method.
 */
public class ChainFramebufferSizeCallback extends AbstractChainCallback<GLFWFramebufferSizeCallbackI> implements IChainFramebufferSizeCallback {
    @Override
    public void invoke(long window, int width, int height) {
        callbackChain.forEach(c -> c.invoke(window, width, height));
    }
}
