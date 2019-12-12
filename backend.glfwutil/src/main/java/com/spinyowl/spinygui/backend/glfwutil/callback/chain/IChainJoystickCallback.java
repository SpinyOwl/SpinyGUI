package com.spinyowl.spinygui.backend.glfwutil.callback.chain;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetJoystickCallback SetJoystickCallback} method.
 */
public interface IChainJoystickCallback extends IChainCallback<GLFWJoystickCallbackI>, GLFWJoystickCallbackI {
}