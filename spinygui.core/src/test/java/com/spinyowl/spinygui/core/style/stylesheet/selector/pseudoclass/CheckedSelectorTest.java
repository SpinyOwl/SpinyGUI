package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import org.junit.jupiter.api.Test;

class CheckedSelectorTest {

  @Test
  void test_tracksRuntimeCheckedState() {
    InputElement input = NodeBuilder.checkbox("news", "yes");
    CheckedSelector selector = new CheckedSelector();
    assertFalse(selector.test(input));
    input.checked(true);
    assertTrue(selector.test(input));
  }
}
