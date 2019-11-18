package com.spinyowl.spinygui.backend.glfwutil.callback.chain.impl;

import com.spinyowl.spinygui.backend.glfwutil.callback.chain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.callback.chain.IChainWindowFocusCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 *
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowFocusCallback SetWindowFocusCallback} method.
 */
public class ChainWindowFocusCallback extends AbstractChainCallback<GLFWWindowFocusCallbackI> implements IChainWindowFocusCallback {
    @Override
    public void invoke(long window, boolean focused) {
        callbackChain.forEach(c -> c.invoke(window, focused));
    }
}
