package com.spinyowl.spinygui.core.parser.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.InputElement;
import org.junit.jupiter.api.Test;

class InputCheckedRoundTripTest {

  private final DefaultNodeParser parser = new DefaultNodeParser();

  @Test
  void checkedAttribute_initializesRuntimeStateAndSerializationTracksRuntimeChanges() {
    InputElement input =
        assertInstanceOf(
            InputElement.class, parser.fromHtml("<input type=\"checkbox\" checked>"));

    assertTrue(input.checked());

    input.checked(false);
    String unchecked = parser.toHtml(input, false);
    assertFalse(unchecked.contains("checked="));

    input.checked(true);
    String checked = parser.toHtml(input, false);
    assertTrue(checked.contains("checked=\"\""));
  }
}
