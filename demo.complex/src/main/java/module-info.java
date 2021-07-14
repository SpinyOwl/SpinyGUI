open module com.spinyowl.spinygui.demo.complex {
  requires static lombok;
  requires transitive org.slf4j;
  requires transitive ch.qos.logback.core;
  requires transitive ch.qos.logback.classic;

  requires transitive com.spinyowl.cbchain;

  requires transitive com.spinyowl.spinygui.core;
  requires transitive com.spinyowl.spinygui.core.backend;
  requires transitive com.spinyowl.spinygui.core.backend.lwjgl.nanovg;

  requires org.lwjgl;
  requires org.lwjgl.natives;
  requires org.lwjgl.glfw;
  requires org.lwjgl.glfw.natives;
  requires org.lwjgl.nanovg;
  requires org.lwjgl.nanovg.natives;
  requires org.lwjgl.opengl;
  requires org.lwjgl.opengl.natives;
}
