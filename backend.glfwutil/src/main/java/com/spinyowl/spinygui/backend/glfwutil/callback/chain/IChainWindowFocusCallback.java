package com.spinyowl.spinygui.backend.glfwutil.callback.chain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowFocusCallback SetWindowFocusCallback} method.
 */
public interface IChainWindowFocusCallback extends IChainCallback<GLFWWindowFocusCallbackI>, GLFWWindowFocusCallbackI {
}
