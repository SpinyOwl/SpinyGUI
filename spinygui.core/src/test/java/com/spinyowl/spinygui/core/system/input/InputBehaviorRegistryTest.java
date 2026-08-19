package com.spinyowl.spinygui.core.system.input;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_COLOR;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_EMAIL;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RESET;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SEARCH;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SUBMIT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEL;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.InputElement;
import org.junit.jupiter.api.Test;

class InputBehaviorRegistryTest {

  @Test
  void kind_classifiesNativeInputFamiliesWithoutTextFallback() {
    assertKind(TYPE_TEXT, InputBehaviorRegistry.Kind.TEXT);
    assertKind(TYPE_EMAIL, InputBehaviorRegistry.Kind.TEXT);
    assertKind(TYPE_SEARCH, InputBehaviorRegistry.Kind.TEXT);
    assertKind(TYPE_TEL, InputBehaviorRegistry.Kind.TEXT);
    assertKind(TYPE_URL, InputBehaviorRegistry.Kind.TEXT);
    assertKind(TYPE_PASSWORD, InputBehaviorRegistry.Kind.TEXT);

    assertKind(TYPE_BUTTON, InputBehaviorRegistry.Kind.BUTTON);
    assertKind(TYPE_SUBMIT, InputBehaviorRegistry.Kind.BUTTON);
    assertKind(TYPE_RESET, InputBehaviorRegistry.Kind.BUTTON);

    assertKind(TYPE_CHECKBOX, InputBehaviorRegistry.Kind.CHECKBOX);
    assertKind(TYPE_RADIO, InputBehaviorRegistry.Kind.RADIO);
    assertKind(TYPE_RANGE, InputBehaviorRegistry.Kind.RANGE);
    assertKind(TYPE_COLOR, InputBehaviorRegistry.Kind.UNSUPPORTED);
  }

  @Test
  void predicates_exposeEditableAndActivatableFamilies() {
    InputElement password = input(TYPE_PASSWORD);
    InputElement checkbox = input(TYPE_CHECKBOX);
    InputElement color = input(TYPE_COLOR);

    assertTrue(InputBehaviorRegistry.textEditable(password));
    assertFalse(InputBehaviorRegistry.activatable(password));
    assertFalse(InputBehaviorRegistry.textEditable(checkbox));
    assertTrue(InputBehaviorRegistry.activatable(checkbox));
    assertFalse(InputBehaviorRegistry.textEditable(color));
    assertFalse(InputBehaviorRegistry.activatable(color));
  }

  private void assertKind(String type, InputBehaviorRegistry.Kind expected) {
    assertEquals(expected, InputBehaviorRegistry.kind(input(type)));
  }

  private InputElement input(String type) {
    InputElement input = new InputElement();
    input.type(type);
    return input;
  }
}
