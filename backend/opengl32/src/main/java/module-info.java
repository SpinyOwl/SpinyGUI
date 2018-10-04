/**
 * Created by ShchAlexander on 09.08.2018.
 */
open module org.liquidengine.legui.backend.opengl32 {
    requires org.liquidengine.legui.backend;
    requires org.liquidengine.legui.core.system;

    requires org.lwjgl;
    requires org.lwjgl.natives;
    requires org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;

    exports org.liquidengine.legui.backend.opengl32;
}