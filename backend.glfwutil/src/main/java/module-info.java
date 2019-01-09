/**
 * Created by ShchAlexander on 09.08.2018.
 */
module com.spinyowl.spinygui.backend.glfwutil {

    requires transitive org.lwjgl.glfw;
    requires transitive org.lwjgl.glfw.natives;

    requires com.spinyowl.spinygui.backend.core;

    exports com.spinyowl.spinygui.backend.glfwutil.callback;
    exports com.spinyowl.spinygui.backend.glfwutil.callback.chain;
    exports com.spinyowl.spinygui.backend.glfwutil.callback.chain.impl;
}