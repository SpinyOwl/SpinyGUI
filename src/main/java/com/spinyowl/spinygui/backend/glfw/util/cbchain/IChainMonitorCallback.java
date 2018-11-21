package com.spinyowl.spinygui.backend.glfw.util.cbchain;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetMonitorCallback SetMonitorCallback} method.
 */
public interface IChainMonitorCallback extends IChainCallback<GLFWMonitorCallbackI>, GLFWMonitorCallbackI {
}