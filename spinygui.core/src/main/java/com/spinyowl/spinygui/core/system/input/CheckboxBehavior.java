package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.InputElement;

/** Backend-agnostic activation behavior for {@code input[type=checkbox]}. */
public final class CheckboxBehavior {

  public boolean activate(InputElement input) {
    if (input.disabled()
        || InputBehaviorRegistry.kind(input) != InputBehaviorRegistry.Kind.CHECKBOX) {
      return false;
    }
    input.checked(!input.checked());
    return true;
  }

  public boolean handleKey(InputElement input, KeyCode keyCode, KeyAction action) {
    if (input.disabled()) {
      input.pressed(false);
      return false;
    }
    if (InputBehaviorRegistry.kind(input) != InputBehaviorRegistry.Kind.CHECKBOX
        || keyCode != KeyCode.SPACE) {
      return false;
    }
    if (action == KeyAction.PRESS) {
      input.pressed(true);
      return false;
    }
    if (action == KeyAction.RELEASE) {
      input.pressed(false);
      return activate(input);
    }
    return false;
  }
}
