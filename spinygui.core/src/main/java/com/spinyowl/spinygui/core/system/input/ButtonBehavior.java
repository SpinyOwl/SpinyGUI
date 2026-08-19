package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;

/** Backend-agnostic activation behavior for button-like controls. */
public class ButtonBehavior {

  public boolean handleKey(ButtonElement button, KeyCode keyCode, KeyAction action) {
    return handleKey(button, button.activatable(), keyCode, action);
  }

  public boolean handleKey(InputElement input, KeyCode keyCode, KeyAction action) {
    return handleKey(input, input.buttonInput() && !input.disabled(), keyCode, action);
  }

  private boolean handleKey(
      Element element, boolean activatable, KeyCode keyCode, KeyAction action) {
    if (!activatable || !activationKey(keyCode)) {
      return false;
    }

    if (KeyAction.PRESS.equals(action)) {
      element.pressed(true);
      return true;
    }
    if (KeyAction.RELEASE.equals(action)) {
      element.pressed(false);
    }
    return false;
  }

  private boolean activationKey(KeyCode keyCode) {
    return KeyCode.ENTER.equals(keyCode)
        || KeyCode.NUMPAD_ENTER.equals(keyCode)
        || KeyCode.SPACE.equals(keyCode);
  }
}
