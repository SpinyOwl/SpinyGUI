package com.spinyowl.spinygui.core.input;

import lombok.Data;
import lombok.NonNull;

@Data
public class KeyboardKey {

  /**
   * KeyCode associated with native key code. Could be different (in case of using some different
   * from QWERTY keyboard layout).
   */
  @NonNull private final KeyCode keyCode;

  /** The code value of the physical key represented by the event. */
  private final int nativeKeyCode;

  /**
   * The system-specific scancode of the key.
   *
   * <p>The scancode is unique for every key, regardless of whether it has a key token. Scancodes
   * are platform-specific but consistent over time, so keys will have different scancodes depending
   * on the platform, but they are safe to save to disk.
   */
  private final int scancode;
}
