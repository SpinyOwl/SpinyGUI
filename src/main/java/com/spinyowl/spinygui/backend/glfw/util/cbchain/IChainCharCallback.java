package com.spinyowl.spinygui.backend.glfw.util.cbchain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCharCallback SetCharCallback} method.
 */
public interface IChainCharCallback extends IChainCallback<GLFWCharCallbackI>, GLFWCharCallbackI {
}
