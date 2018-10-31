/**
 * Created by ShchAlexander on 09.08.2018.
 */
open module org.spinyowl.spinygui.backend.opengl32 {
    requires org.spinyowl.spinygui.core;
    requires org.spinyowl.spinygui.backend;

    requires org.lwjgl;
    requires org.lwjgl.natives;
    requires org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;

    requires org.apache.logging.log4j;

    exports org.spinyowl.spinygui.backend.opengl32;
}