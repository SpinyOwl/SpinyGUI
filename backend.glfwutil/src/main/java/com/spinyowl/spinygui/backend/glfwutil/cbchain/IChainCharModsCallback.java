package com.spinyowl.spinygui.backend.glfwutil.cbchain;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;

/**
 * Instances of this interface may be passed to the {@link GLFW#glfwSetCharModsCallback SetCharModsCallback} method.
 */
public interface IChainCharModsCallback extends IChainCallback<GLFWCharModsCallbackI>, GLFWCharModsCallbackI {
}