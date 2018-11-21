package com.spinyowl.spinygui.backend.glfwutil.cbchain.impl;

import com.spinyowl.spinygui.backend.glfwutil.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.cbchain.IChainCharCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCharCallback SetCharCallback} method.
 */
public class ChainCharCallback extends AbstractChainCallback<GLFWCharCallbackI> implements IChainCharCallback {
    @Override
    public void invoke(long window, int codepoint) {
        callbackChain.forEach(c -> c.invoke(window, codepoint));
    }
}
