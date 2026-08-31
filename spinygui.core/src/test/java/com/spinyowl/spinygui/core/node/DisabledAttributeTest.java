package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_DISABLED;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.attrs;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.disabled;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.input;
import static com.spinyowl.spinygui.core.node.NodeBuilder.textarea;
import static com.spinyowl.spinygui.core.node.NodeBuilder.type;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DisabledAttributeTest {

  @Test
  void disabledAttributeMarksSupportedControlsDisabled() {
    ButtonElement button = button(attrs(disabled()));
    InputElement input = input(attrs(disabled()));
    TextareaElement textarea = textarea(attrs(disabled()), "value");

    assertTrue(button.disabled());
    assertTrue(input.disabled());
    assertTrue(textarea.disabled());
  }

  @Test
  void disabledAttributeUsesBooleanPresenceSemantics() {
    InputElement input = input();

    input.setAttribute(ATTR_DISABLED, "false");
    assertTrue(input.disabled());

    input.removeAttribute(ATTR_DISABLED);
    assertFalse(input.disabled());
  }

  @Test
  void unsupportedElementDoesNotBecomeDisabled() {
    Element element = div(attrs(disabled()));

    assertFalse(element.disabled());
  }

  @Test
  void disabledButtonIsNotActivatable() {
    ButtonElement button = button(attrs(disabled(), type(TYPE_BUTTON)));

    assertFalse(button.activatable());

    button.removeAttribute(ATTR_DISABLED);
    assertTrue(button.activatable());
  }
}
