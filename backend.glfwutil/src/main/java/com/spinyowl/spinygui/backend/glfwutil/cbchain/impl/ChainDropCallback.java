package com.spinyowl.spinygui.backend.glfwutil.cbchain.impl;

import com.spinyowl.spinygui.backend.glfwutil.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.cbchain.IChainDropCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetDropCallback SetDropCallback} method.
 */
public class ChainDropCallback extends AbstractChainCallback<GLFWDropCallbackI> implements IChainDropCallback {
    @Override
    public void invoke(long window, int count, long names) {
        callbackChain.forEach(c -> c.invoke(window, count, names));
    }
}
