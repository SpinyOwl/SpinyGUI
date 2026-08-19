package com.spinyowl.spinygui.core.system.input;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_DISABLED;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import org.junit.jupiter.api.Test;

class NativeInputDisabledBehaviorTest {

  @Test
  void disabledCheckboxCannotBeActivatedByNativeBehavior() {
    InputElement input = disabledInput(TYPE_CHECKBOX);
    CheckboxBehavior behavior = new CheckboxBehavior();

    assertFalse(behavior.activate(input));
    assertFalse(behavior.handleKey(input, KeyCode.SPACE, KeyAction.PRESS));
    assertFalse(behavior.handleKey(input, KeyCode.SPACE, KeyAction.RELEASE));
    assertFalse(input.checked());
    assertFalse(input.pressed());
    assertFalse(InputBehaviorRegistry.activatable(input));
  }

  @Test
  void disabledRadioCannotChangeGroupSelection() {
    Frame frame = new Frame();
    InputElement selected = new InputElement();
    selected.type(TYPE_RADIO);
    selected.setAttribute("name", "mode");
    selected.checked(true);
    InputElement disabled = disabledInput(TYPE_RADIO);
    disabled.setAttribute("name", "mode");
    frame.addChild(selected);
    frame.addChild(disabled);

    assertFalse(new RadioBehavior().activate(disabled, frame));
    assertTrue(selected.checked());
    assertFalse(disabled.checked());
  }

  @Test
  void disabledRangeIgnoresUserKeyboardButAllowsProgrammaticValueChanges() {
    InputElement input = disabledInput(TYPE_RANGE);
    input.value("50");
    RangeBehavior behavior = new RangeBehavior();

    assertFalse(behavior.handleKey(input, KeyCode.RIGHT, KeyAction.PRESS));
    assertEquals("50", input.value());

    behavior.setValue(input, 75);
    assertEquals("75", input.value());
  }

  private InputElement disabledInput(String type) {
    InputElement input = new InputElement();
    input.type(type);
    input.setAttribute(ATTR_DISABLED, "");
    return input;
  }
}
