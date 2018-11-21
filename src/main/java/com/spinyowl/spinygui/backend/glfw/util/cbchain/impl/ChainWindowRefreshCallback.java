package com.spinyowl.spinygui.backend.glfw.util.cbchain.impl;

import com.spinyowl.spinygui.backend.glfw.util.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfw.util.cbchain.IChainWindowRefreshCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 * <p>
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowRefreshCallback SetWindowRefreshCallback} method.
 */
public class ChainWindowRefreshCallback extends AbstractChainCallback<GLFWWindowRefreshCallbackI> implements IChainWindowRefreshCallback {
    @Override
    public void invoke(long window) {
        callbackChain.forEach(c -> c.invoke(window));
    }
}
