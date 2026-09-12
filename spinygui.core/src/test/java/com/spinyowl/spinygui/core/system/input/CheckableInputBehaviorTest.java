package com.spinyowl.spinygui.core.system.input;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import org.junit.jupiter.api.Test;

class CheckableInputBehaviorTest {

  @Test
  void activate_togglesCheckboxAndReturnsTheChangedInput() {
    InputElement checkbox = NodeBuilder.input(TYPE_CHECKBOX, "news", "yes");
    Frame frame = frame(checkbox);
    assertFalse(checkbox.checked());
    assertEquals(java.util.List.of(checkbox), CheckableInputBehavior.activate(checkbox, frame));
    assertTrue(checkbox.checked());
    CheckableInputBehavior.activate(checkbox, frame);
    assertFalse(checkbox.checked());
  }

  @Test
  void selectRadio_clearsOnlyMatchingNamedPeersInSameFrame() {
    InputElement red = NodeBuilder.radioButton("color", "red");
    InputElement blue = NodeBuilder.radioButton("color", "blue");
    InputElement otherGroup = NodeBuilder.radioButton("size", "large");
    InputElement otherFrame = NodeBuilder.radioButton("color", "green");
    red.checked(true);
    otherGroup.checked(true);
    otherFrame.checked(true);
    Frame frame = frame(red, blue, otherGroup);
    assertEquals(java.util.List.of(red, blue), CheckableInputBehavior.activate(blue, frame));
    assertFalse(red.checked());
    assertTrue(blue.checked());
    assertTrue(otherGroup.checked());
    assertTrue(otherFrame.checked());
  }

  @Test
  void selectRelativeRadio_wrapsWithinEnabledNamedGroup() {
    InputElement first = NodeBuilder.radioButton("choice", "one");
    InputElement second = NodeBuilder.radioButton("choice", "two");
    InputElement third = NodeBuilder.radioButton("choice", "three");
    second.setAttribute("disabled", "");
    first.checked(true);
    Frame frame = frame(first, second, third);
    CheckableInputBehavior.selectRelativeRadio(first, frame, -1);
    assertTrue(third.checked());
    assertFalse(first.checked());
  }

  @Test
  void activate_doesNotChangeDisabledControls() {
    InputElement checkbox = NodeBuilder.input(TYPE_CHECKBOX, "news", "yes");
    checkbox.setAttribute("disabled", "");
    assertTrue(CheckableInputBehavior.activate(checkbox, frame(checkbox)).isEmpty());
    assertFalse(checkbox.checked());
  }

  private static Frame frame(InputElement... inputs) {
    Frame frame = new Frame();
    frame.addChildren(inputs);
    return frame;
  }
}
