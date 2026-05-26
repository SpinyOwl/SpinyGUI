package com.spinyowl.spinygui.core.input;

import lombok.Data;
import lombok.NonNull;

@Data
public class Keyboard {

  @NonNull private KeyboardLayout layout;
  @NonNull private ShortcutRegistry shortcuts;
}
