package com.spinyowl.spinygui.core.input;

import lombok.Data;
import lombok.NonNull;

@Data
public class KeyboardKey {
  @NonNull
  private final KeyCode keyCode;
  private final int nativeKeyCode;

}
