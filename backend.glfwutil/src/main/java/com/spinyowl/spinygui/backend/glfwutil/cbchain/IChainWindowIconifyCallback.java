package com.spinyowl.spinygui.backend.glfwutil.cbchain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowIconifyCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowIconifyCallback SetWindowIconifyCallback} method.
 */
public interface IChainWindowIconifyCallback extends IChainCallback<GLFWWindowIconifyCallbackI>, GLFWWindowIconifyCallbackI {
}
