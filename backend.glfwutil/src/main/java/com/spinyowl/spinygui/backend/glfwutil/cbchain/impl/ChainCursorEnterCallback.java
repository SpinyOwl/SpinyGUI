package com.spinyowl.spinygui.backend.glfwutil.cbchain.impl;

import com.spinyowl.spinygui.backend.glfwutil.cbchain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.cbchain.IChainCursorEnterCallback;
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
