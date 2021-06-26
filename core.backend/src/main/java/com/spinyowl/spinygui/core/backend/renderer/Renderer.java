package com.spinyowl.spinygui.core.backend.renderer;

import com.spinyowl.spinygui.core.node.Frame;
import org.joml.Vector2f;

/** Common renderer interface. */
public interface Renderer {

  void initialize();

  void render(long window, Vector2f windowSize, Vector2f frameBufferSize, Frame frame);

  void destroy();
}
