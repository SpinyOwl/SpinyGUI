package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.MouseButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SystemMouseButton {
  MOUSE_BUTTON_1(MouseButton.MOUSE_BUTTON_1), // LEFT
  MOUSE_BUTTON_2(MouseButton.MOUSE_BUTTON_2), // RIGHT
  MOUSE_BUTTON_3(MouseButton.MOUSE_BUTTON_3), // MIDDLE
  MOUSE_BUTTON_4(MouseButton.MOUSE_BUTTON_4), // SIDE LEFT TOP
  MOUSE_BUTTON_5(MouseButton.MOUSE_BUTTON_5), // SIDE LEFT BOTTOM
  MOUSE_BUTTON_6(MouseButton.MOUSE_BUTTON_6),
  MOUSE_BUTTON_7(MouseButton.MOUSE_BUTTON_7),
  MOUSE_BUTTON_8(MouseButton.MOUSE_BUTTON_8);

  public static final SystemMouseButton LEFT = MOUSE_BUTTON_1;
  public static final SystemMouseButton RIGHT = MOUSE_BUTTON_2;
  public static final SystemMouseButton MIDDLE = MOUSE_BUTTON_3;

  @NonNull private final MouseButton mouseButton;
}
