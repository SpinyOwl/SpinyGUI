package com.spinyowl.spinygui.core.system.input;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NativeInputBehaviorTest {

  @Test
  void checkbox_activateAndSpace_toggleCheckedState() {
    CheckboxBehavior behavior = new CheckboxBehavior();
    InputElement input = input(TYPE_CHECKBOX);

    assertTrue(behavior.activate(input));
    assertTrue(input.checked());
    assertTrue(behavior.activate(input));
    assertFalse(input.checked());

    assertFalse(behavior.handleKey(input, KeyCode.SPACE, KeyAction.PRESS));
    assertTrue(input.pressed());
    assertTrue(behavior.handleKey(input, KeyCode.SPACE, KeyAction.RELEASE));
    assertFalse(input.pressed());
    assertTrue(input.checked());
  }

  @Test
  void radio_activate_enforcesNamedGroupExclusivity() {
    RadioBehavior behavior = new RadioBehavior();
    Frame frame = new Frame();
    InputElement first = input(TYPE_RADIO);
    first.setAttribute("name", "mode");
    first.checked(true);
    InputElement second = input(TYPE_RADIO);
    second.setAttribute("name", "mode");
    InputElement otherGroup = input(TYPE_RADIO);
    otherGroup.setAttribute("name", "other");
    otherGroup.checked(true);
    frame.addChild(first);
    frame.addChild(second);
    frame.addChild(otherGroup);

    assertTrue(behavior.activate(second, frame));

    assertFalse(first.checked());
    assertTrue(second.checked());
    assertTrue(otherGroup.checked());
    assertFalse(behavior.activate(second, frame));
  }

  @Test
  void range_keyboardAndNormalization_respectMinMaxAndStep() {
    RangeBehavior behavior = new RangeBehavior();
    InputElement input = input(TYPE_RANGE);
    input.setAttribute("min", "10");
    input.setAttribute("max", "20");
    input.setAttribute("step", "2");
    input.value("14");

    assertTrue(behavior.handleKey(input, KeyCode.RIGHT, KeyAction.PRESS));
    assertEquals("16", input.value());
    assertTrue(behavior.handleKey(input, KeyCode.END, KeyAction.PRESS));
    assertEquals("20", input.value());
    assertFalse(behavior.handleKey(input, KeyCode.RIGHT, KeyAction.PRESS));
    assertEquals("20", input.value());

    assertTrue(behavior.setValue(input, 11));
    assertEquals("12", input.value());
    assertEquals(0.2, behavior.fraction(input), 0.0001);
  }

  @Test
  void range_stepAny_clampsWithoutSnapping() {
    RangeBehavior behavior = new RangeBehavior();
    InputElement input = input(TYPE_RANGE);
    input.setAttribute("min", "0");
    input.setAttribute("max", "1");
    input.setAttribute("step", "any");

    assertTrue(behavior.setValue(input, 0.375));
    assertEquals("0.375", input.value());
    assertTrue(behavior.setValue(input, 5));
    assertEquals("1", input.value());
  }

  @Test
  void range_pointerUsesScrolledAbsoluteCoordinates() {
    RangeBehavior behavior = new RangeBehavior();
    Element scrollParent = new Element("div");
    scrollParent.box().contentPosition(100, 0);
    scrollParent.scrollLeft(20);

    InputElement input = input(TYPE_RANGE);
    input.box().contentPosition(10, 0);
    input.box().contentSize(100, 18);
    input.offsetParent(scrollParent);

    assertTrue(behavior.setFromPointer(input, new Vector2f(115, 9)));
    assertEquals("25", input.value());
  }

  private InputElement input(String type) {
    InputElement input = new InputElement();
    input.type(type);
    return input;
  }
}
