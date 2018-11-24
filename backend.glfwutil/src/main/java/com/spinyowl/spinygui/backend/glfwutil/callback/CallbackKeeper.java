package com.spinyowl.spinygui.backend.glfwutil.callback;

import com.spinyowl.spinygui.backend.glfwutil.callback.chain.*;

import static org.lwjgl.glfw.GLFW.*;

public interface CallbackKeeper {

    static void registerCallbacks(long window, CallbackKeeper keeper) {
        glfwSetCharCallback(window, keeper.getChainCharCallback());
        glfwSetCharModsCallback(window, keeper.getChainCharModsCallback());
        glfwSetCursorEnterCallback(window, keeper.getChainCursorEnterCallback());
        glfwSetCursorPosCallback(window, keeper.getChainCursorPosCallback());
        glfwSetDropCallback(window, keeper.getChainDropCallback());
        glfwSetFramebufferSizeCallback(window, keeper.getChainFramebufferSizeCallback());
        glfwSetKeyCallback(window, keeper.getChainKeyCallback());
        glfwSetMouseButtonCallback(window, keeper.getChainMouseButtonCallback());
        glfwSetScrollCallback(window, keeper.getChainScrollCallback());
        glfwSetWindowCloseCallback(window, keeper.getChainWindowCloseCallback());
        glfwSetWindowContentScaleCallback(window, keeper.getChainWindowContentScaleCallback());
        glfwSetWindowFocusCallback(window, keeper.getChainWindowFocusCallback());
        glfwSetWindowIconifyCallback(window, keeper.getChainWindowIconifyCallback());
        glfwSetWindowMaximizeCallback(window, keeper.getChainWindowMaximizeCallback());
        glfwSetWindowPosCallback(window, keeper.getChainWindowPosCallback());
        glfwSetWindowRefreshCallback(window, keeper.getChainWindowRefreshCallback());
        glfwSetWindowSizeCallback(window, keeper.getChainWindowSizeCallback());
    }

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWCharCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainCharCallback getChainCharCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWDropCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainDropCallback getChainDropCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWKeyCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainKeyCallback getChainKeyCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWScrollCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainScrollCallback getChainScrollCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWCharModsCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainCharModsCallback getChainCharModsCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWCursorEnterCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainCursorEnterCallback getChainCursorEnterCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWFramebufferSizeCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainFramebufferSizeCallback getChainFramebufferSizeCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWMouseButtonCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainMouseButtonCallback getChainMouseButtonCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWCursorPosCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainCursorPosCallback getChainCursorPosCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowCloseCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowCloseCallback getChainWindowCloseCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowFocusCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowFocusCallback getChainWindowFocusCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowIconifyCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowIconifyCallback getChainWindowIconifyCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowPosCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowPosCallback getChainWindowPosCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowRefreshCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowRefreshCallback getChainWindowRefreshCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowSizeCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowSizeCallback getChainWindowSizeCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowContentScaleCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowContentScaleCallback getChainWindowContentScaleCallback();

    /**
     * Returns chain callback used to keep {@link org.lwjgl.glfw.GLFWWindowMaximizeCallbackI} instances.
     *
     * @return chain char callback.
     */
    IChainWindowMaximizeCallback getChainWindowMaximizeCallback();


}
