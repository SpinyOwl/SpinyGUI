package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.ButtonElement;

/** Backend-agnostic activation behavior for {@code button} elements. */
public class ButtonBehavior {

  public boolean handleKey(ButtonElement button, KeyCode keyCode, KeyAction action) {
    if (!button.activatable() || !activationKey(keyCode)) {
      return false;
    }

    if (KeyAction.PRESS.equals(action)) {
      button.pressed(true);
      return true;
    }
    if (KeyAction.RELEASE.equals(action)) {
      button.pressed(false);
    }
    return false;
  }

  private boolean activationKey(KeyCode keyCode) {
    return KeyCode.ENTER.equals(keyCode)
        || KeyCode.NUMPAD_ENTER.equals(keyCode)
        || KeyCode.SPACE.equals(keyCode);
  }
}
