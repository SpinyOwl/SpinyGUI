package com.spinyowl.spinygui.backend.glfwutil.callback.chain.impl;

import com.spinyowl.spinygui.backend.glfwutil.callback.chain.AbstractChainCallback;
import com.spinyowl.spinygui.backend.glfwutil.callback.chain.IChainJoystickCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallbackI;

/**
 * Chain callback implementation based on {@link AbstractChainCallback}.
 *
 * Instances of this interface may be passed to the {@link GLFW#glfwSetJoystickCallback SetJoystickCallback} method.
 */
public class ChainJoystickCallback extends AbstractChainCallback<GLFWJoystickCallbackI> implements IChainJoystickCallback {
    @Override
    public void invoke(int joy, int event) {
        callbackChain.forEach(c -> c.invoke(joy, event));
    }
}
