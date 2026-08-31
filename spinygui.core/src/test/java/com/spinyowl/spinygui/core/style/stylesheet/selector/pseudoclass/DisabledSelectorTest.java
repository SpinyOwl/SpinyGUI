package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass;

import static com.spinyowl.spinygui.core.node.NodeBuilder.attrs;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.disabled;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.input;
import static com.spinyowl.spinygui.core.node.NodeBuilder.textarea;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DisabledSelectorTest {

  private final DisabledSelector selector = new DisabledSelector();

  @Test
  void matchesDisabledSupportedControls() {
    assertTrue(selector.test(button(attrs(disabled()))));
    assertTrue(selector.test(input(attrs(disabled()))));
    assertTrue(selector.test(textarea(attrs(disabled()), "value")));
  }

  @Test
  void doesNotMatchEnabledOrUnsupportedElements() {
    assertFalse(selector.test(button()));
    assertFalse(selector.test(input()));
    assertFalse(selector.test(textarea()));
    assertFalse(selector.test(div(attrs(disabled()))));
  }
}
