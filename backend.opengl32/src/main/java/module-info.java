/**
 * Created by ShchAlexander on 09.08.2018.
 */
open module com.spinyowl.spinygui.backend.opengl32 {
    requires com.spinyowl.spinygui.core;
    requires com.spinyowl.spinygui.backend.core;
    requires com.spinyowl.spinygui.backend.glfwutil;

    requires org.lwjgl;
    requires org.lwjgl.natives;
    requires org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;

    requires guava.base.r03;

    exports com.spinyowl.spinygui.backend.opengl32.api;
    exports com.spinyowl.spinygui.backend.opengl32.service;
    exports com.spinyowl.spinygui.backend.opengl32.service.internal;
}