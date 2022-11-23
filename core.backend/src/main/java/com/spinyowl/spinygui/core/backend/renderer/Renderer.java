package com.spinyowl.spinygui.core.backend.renderer;

import com.spinyowl.spinygui.core.layout.Viewport;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/** Common renderer interface. */
public interface Renderer {

  void initialize();

  void render(long window, Vector2fc windowSize, Vector2ic frameBufferSize, Viewport frame);

  void destroy();
}
