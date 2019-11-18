package com.spinyowl.spinygui.backend.glfwutil.callback.chain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetMouseButtonCallback SetMouseButtonCallback} method.
 */
public interface IChainMouseButtonCallback extends IChainCallback<GLFWMouseButtonCallbackI>, GLFWMouseButtonCallbackI {
}
