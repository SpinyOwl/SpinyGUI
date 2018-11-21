package com.spinyowl.spinygui.backend.glfwutil.cbchain;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetScrollCallback SetScrollCallback} method.
 */
public interface IChainScrollCallback extends IChainCallback<GLFWScrollCallbackI>, GLFWScrollCallbackI {
}