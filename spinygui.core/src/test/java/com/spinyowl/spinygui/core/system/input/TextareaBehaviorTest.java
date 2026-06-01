package com.spinyowl.spinygui.core.system.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.TextareaElement;
import org.junit.jupiter.api.Test;

class TextareaBehaviorTest {

  private final TextareaBehavior behavior = new TextareaBehavior();

  @Test
  void insertPrintable_replacesSelection() {
    TextareaElement textarea = new TextareaElement("abcd");
    textarea.select(1, 3);

    assertTrue(behavior.insertPrintable(textarea, 'x'));

    assertEquals("axd", textarea.value());
    assertEquals(2, textarea.caretIndex());
    assertFalse(textarea.hasSelection());
  }

  @Test
  void handleKey_whenEnterPressed_insertsNewline() {
    TextareaElement textarea = new TextareaElement("ab");
    textarea.caretIndex(1);

    assertTrue(behavior.handleKey(textarea, KeyCode.ENTER, KeyAction.PRESS));

    assertEquals("a\nb", textarea.value());
    assertEquals(2, textarea.caretIndex());
  }

  @Test
  void handleKey_whenBackspaceAfterNewline_removesLineBreak() {
    TextareaElement textarea = new TextareaElement("a\nb");
    textarea.caretIndex(2);

    assertTrue(behavior.handleKey(textarea, KeyCode.BACKSPACE, KeyAction.PRESS));

    assertEquals("ab", textarea.value());
    assertEquals(1, textarea.caretIndex());
  }

  @Test
  void handleKey_whenHomeAndEndPressed_movesWithinCurrentLine() {
    TextareaElement textarea = new TextareaElement("ab\ncde");
    textarea.caretIndex(5);

    assertTrue(behavior.handleKey(textarea, KeyCode.HOME, KeyAction.PRESS));
    assertEquals(3, textarea.caretIndex());

    assertTrue(behavior.handleKey(textarea, KeyCode.END, KeyAction.PRESS));
    assertEquals(6, textarea.caretIndex());
  }
}
