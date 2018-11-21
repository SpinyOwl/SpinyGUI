/**
 * Created by ShchAlexander on 09.08.2018.
 */
module com.spinyowl.spinygui.backend.glfwutil {

    requires transitive org.lwjgl.glfw;
    requires transitive org.lwjgl.glfw.natives;

    exports com.spinyowl.spinygui.backend.glfwutil;

    exports com.spinyowl.spinygui.backend.glfwutil.cbchain;
    exports com.spinyowl.spinygui.backend.glfwutil.cbchain.impl;
}