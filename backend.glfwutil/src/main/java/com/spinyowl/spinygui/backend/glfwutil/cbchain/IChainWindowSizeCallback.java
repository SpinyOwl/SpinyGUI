package com.spinyowl.spinygui.backend.glfwutil.cbchain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowSizeCallback SetWindowSizeCallback} method.
 */
public interface IChainWindowSizeCallback extends IChainCallback<GLFWWindowSizeCallbackI>, GLFWWindowSizeCallbackI {
}