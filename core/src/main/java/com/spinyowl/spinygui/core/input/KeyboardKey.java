package com.spinyowl.spinygui.core.input;

import lombok.Data;
import lombok.NonNull;

@Data
public class KeyboardKey {

  /**
   * KeyCode associated with native key code. Could be different (in case of using some different
   * from QWERTY keyboard layout).
   */
  @NonNull
  private final KeyCode keyCode;

  /**
   * The code value of the physical key represented by the event.
   */
  private final int nativeKeyCode;

}
