package com.spinyowl.spinygui.core.input.impl;

import com.spinyowl.spinygui.core.input.MouseButton;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.node.Frame;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.joml.Vector2f;

/** Default implementation of MouseService. */
public class MouseServiceImpl implements MouseService {
  private final Map<Frame, CursorPositions> cursorPositionsMap = new IdentityHashMap<>();
  private final Map<MouseButton, Boolean> buttons = new HashMap<>();

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
