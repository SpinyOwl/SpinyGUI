package com.spinyowl.spinygui.backend.glfwutil.callback.chain;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetErrorCallback SetErrorCallback} method.
 */
public interface IChainErrorCallback extends IChainCallback<GLFWErrorCallbackI>, GLFWErrorCallbackI {
}