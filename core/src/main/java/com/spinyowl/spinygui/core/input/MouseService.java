package com.spinyowl.spinygui.core.input;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.Data;
import org.joml.Vector2fc;

/** Provides ability to get and set cursor positions. */
public interface MouseService {

  CursorPositions getCursorPositions(Frame frame);

  void setCursorPositions(Frame frame, CursorPositions positions);

  boolean pressed(MouseButton button);

  void pressed(MouseButton button, boolean pressed);

  @Data
  final class CursorPositions {
    private final Vector2fc current;
    private final Vector2fc previous;
  }
}
