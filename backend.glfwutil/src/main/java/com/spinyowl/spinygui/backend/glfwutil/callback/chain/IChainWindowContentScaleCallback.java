package com.spinyowl.spinygui.backend.glfwutil.callback.chain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowContentScaleCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowContentScaleCallback SetWindowContentScaleCallback} method.
 */
public interface IChainWindowContentScaleCallback extends IChainCallback<GLFWWindowContentScaleCallbackI>, GLFWWindowContentScaleCallbackI {
}
