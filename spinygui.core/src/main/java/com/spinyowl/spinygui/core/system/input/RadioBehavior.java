package com.spinyowl.spinygui.core.system.input;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_NAME;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import java.util.Objects;

/** Backend-agnostic grouping and activation behavior for {@code input[type=radio]}. */
public final class RadioBehavior {

  public boolean activate(InputElement input, Frame frame) {
    if (input.disabled() || InputBehaviorRegistry.kind(input) != InputBehaviorRegistry.Kind.RADIO) {
      return false;
    }
    if (input.checked()) {
      return false;
    }

    String name = input.getAttribute(ATTR_NAME);
    if (frame != null && name != null && !name.isBlank()) {
      clearGroup(frame, input, name);
    }
    input.checked(true);
    return true;
  }

  public boolean handleKey(
      InputElement input, Frame frame, KeyCode keyCode, KeyAction action) {
    if (input.disabled()) {
      input.pressed(false);
      return false;
    }
    if (InputBehaviorRegistry.kind(input) != InputBehaviorRegistry.Kind.RADIO
        || keyCode != KeyCode.SPACE) {
      return false;
    }
    if (action == KeyAction.PRESS) {
      input.pressed(true);
      return false;
    }
    if (action == KeyAction.RELEASE) {
      input.pressed(false);
      return activate(input, frame);
    }
    return false;
  }

  private void clearGroup(Element element, InputElement selected, String name) {
    if (element instanceof InputElement candidate
        && candidate != selected
        && InputBehaviorRegistry.kind(candidate) == InputBehaviorRegistry.Kind.RADIO
        && Objects.equals(name, candidate.getAttribute(ATTR_NAME))) {
      candidate.checked(false);
    }
    element.children().forEach(child -> clearGroup(child, selected, name));
  }
}
