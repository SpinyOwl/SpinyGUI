package com.spinyowl.spinygui.backend.glfw.util.cbchain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetDropCallback SetDropCallback} method.
 */
public interface IChainDropCallback extends IChainCallback<GLFWDropCallbackI>, GLFWDropCallbackI {
}
