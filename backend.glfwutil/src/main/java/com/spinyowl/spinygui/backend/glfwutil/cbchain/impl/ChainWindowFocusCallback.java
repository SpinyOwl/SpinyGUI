package com.spinyowl.spinygui.backend.glfwutil.cbchain.impl;

import com.spinyowl.spinygui.backend.glfwutil.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.cbchain.IChainWindowFocusCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowFocusCallback SetWindowFocusCallback} method.
 */
public class ChainWindowFocusCallback extends AbstractChainCallback<GLFWWindowFocusCallbackI> implements IChainWindowFocusCallback {
    @Override
    public void invoke(long window, boolean focused) {
        callbackChain.forEach(c -> c.invoke(window, focused));
    }
}
