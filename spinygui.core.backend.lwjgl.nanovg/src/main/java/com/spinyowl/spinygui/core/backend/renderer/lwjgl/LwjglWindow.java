package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import org.joml.Vector2f;
import org.joml.Vector2i;

/** Injected LWJGL window/context boundary used by the reusable application loop. */
public interface LwjglWindow extends AutoCloseable {
  void initialize();
  boolean shouldClose();
  void pollEvents();
  long handle();
  Vector2f windowSize();
  Vector2i framebufferSize();
  void beginRender(Vector2i framebufferSize);
  void swapBuffers();
  @Override void close();
}
