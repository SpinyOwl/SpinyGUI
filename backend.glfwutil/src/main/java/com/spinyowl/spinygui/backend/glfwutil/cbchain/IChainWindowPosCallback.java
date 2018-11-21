package com.spinyowl.spinygui.backend.glfwutil.cbchain;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetWindowPosCallback SetWindowPosCallback} method.
 */
public interface IChainWindowPosCallback extends IChainCallback<GLFWWindowPosCallbackI>, GLFWWindowPosCallbackI {
}
