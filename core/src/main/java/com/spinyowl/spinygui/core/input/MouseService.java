package com.spinyowl.spinygui.core.input;

import com.spinyowl.spinygui.core.node.Frame;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/** Provides ability to get and set cursor positions. */
public interface MouseService {

  CursorPositions getCursorPositions(Frame frame);

  void setCursorPositions(Frame frame, CursorPositions positions);

  boolean pressed(MouseButton button);

  void pressed(MouseButton button, boolean pressed);

  record CursorPositions(Vector2fc current, Vector2fc previous) {}

  class DefaultMouseService implements MouseService {
    private final Map<Frame, CursorPositions> cursorPositionsMap = new ConcurrentHashMap<>();
    private final Map<MouseButton, Boolean> buttons = new ConcurrentHashMap<>();

    @Override
    public CursorPositions getCursorPositions(Frame frame) {
      return cursorPositionsMap.getOrDefault(
          frame, new CursorPositions(new Vector2f(), new Vector2f()));
    }

    @Override
    public void setCursorPositions(Frame frame, CursorPositions positions) {
      cursorPositionsMap.put(frame, positions);
    }

    @Override
    public boolean pressed(MouseButton button) {
      return buttons.getOrDefault(button, false);
    }

    @Override
    public void pressed(MouseButton button, boolean pressed) {
      buttons.put(button, pressed);
    }
  }
}
