open module com.spinyowl.spinygui.core.backend.lwjgl.skija {
  requires com.spinyowl.spinygui.core;
  requires com.spinyowl.spinygui.core.backend;
  requires lombok;
  requires org.lwjgl;
  requires org.lwjgl.natives;
  requires org.lwjgl.glfw;
  requires org.lwjgl.glfw.natives;
  requires org.lwjgl.opengl;
  requires org.lwjgl.opengl.natives;
  requires org.jetbrains.skija.shared;
  //  requires org.jetbrains.skija.linux;
  //  requires org.jetbrains.skija.windows;
  //  requires org.jetbrains.skija.macos.arm64;
  //  requires org.jetbrains.skija.macos.x64;

  exports com.spinyowl.spinygui.core.backend.renderer.lwjgl.skija;
}
