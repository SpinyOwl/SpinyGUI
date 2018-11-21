package com.spinyowl.spinygui.backend.glfw.util.cbchain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetFramebufferSizeCallback SetFramebufferSizeCallback} method.
 */
public interface IChainFramebufferSizeCallback extends IChainCallback<GLFWFramebufferSizeCallbackI>, GLFWFramebufferSizeCallbackI {
}
