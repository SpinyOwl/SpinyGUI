package com.spinyowl.spinygui.backend.glfwutil.callback.chain.impl;

import com.spinyowl.spinygui.backend.glfwutil.callback.chain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.callback.chain.IChainMouseButtonCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 *
 * Instances of this interface may be passed to the {@link GLFW#glfwSetMouseButtonCallback SetMouseButtonCallback} method.
 */
public class ChainMouseButtonCallback extends AbstractChainCallback<GLFWMouseButtonCallbackI> implements IChainMouseButtonCallback {
    @Override
    public void invoke(long window, int button, int action, int mods) {
        callbackChain.forEach(c -> c.invoke(window, button, action, mods));
    }
}
