package com.spinyowl.spinygui.backend.glfwutil.callback.chain.impl;

import com.spinyowl.spinygui.backend.glfwutil.callback.chain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.callback.chain.IChainCursorEnterCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCursorEnterCallback SetCursorEnterCallback} method.
 */
public class ChainCursorEnterCallback extends AbstractChainCallback<GLFWCursorEnterCallbackI> implements IChainCursorEnterCallback {
    @Override
    public void invoke(long window, boolean entered) {
        callbackChain.forEach(c -> c.invoke(window, entered));
    }
}
