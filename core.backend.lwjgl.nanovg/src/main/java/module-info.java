open module com.spinyowl.spinygui.core.backend.lwjgl.nanovg {
  requires com.spinyowl.spinygui.core;
  requires com.spinyowl.spinygui.core.backend;

  requires lombok;

  requires org.lwjgl;
  requires org.lwjgl.natives;
  requires org.lwjgl.glfw;
  requires org.lwjgl.glfw.natives;
  requires org.lwjgl.nanovg;
  requires org.lwjgl.nanovg.natives;
  requires org.lwjgl.opengl;
  requires org.lwjgl.opengl.natives;
  requires com.google.common;

  exports com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;
  exports com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util;
}
