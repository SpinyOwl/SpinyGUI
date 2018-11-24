package com.spinyowl.spinygui.backend.glfwutil.callback.chain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCursorEnterCallback SetCursorEnterCallback} method.
 */
public interface IChainCursorEnterCallback extends IChainCallback<GLFWCursorEnterCallbackI>, GLFWCursorEnterCallbackI {
}