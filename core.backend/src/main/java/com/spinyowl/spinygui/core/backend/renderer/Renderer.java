package com.spinyowl.spinygui.core.backend.renderer;

import com.spinyowl.spinygui.core.node.Frame;

/** Common renderer interface. */
public interface Renderer {

  void initialize();

  void render(Frame frame);

  void destroy();
}
