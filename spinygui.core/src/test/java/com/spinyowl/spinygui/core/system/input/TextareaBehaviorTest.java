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
  void handleKey_whenBackspaceAfterSupplementaryCodePoint_removesWholeCodePoint() {
    TextareaElement textarea = new TextareaElement("a\uD83D\uDE00b");
    textarea.caretIndex(3);

    assertTrue(behavior.handleKey(textarea, KeyCode.BACKSPACE, KeyAction.PRESS));

    assertEquals("ab", textarea.value());
    assertEquals(1, textarea.caretIndex());
  }

  @Test
  void handleKey_whenDeleteBeforeCjkCodePoint_removesWholeCodePoint() {
    TextareaElement textarea = new TextareaElement("a\u96EAb");
    textarea.caretIndex(1);

    assertTrue(behavior.handleKey(textarea, KeyCode.DELETE, KeyAction.PRESS));

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

  @Test
  void externallyAssignedIndices_clampAsUtf16OffsetsAndSnapFromSurrogateInterior() {
    TextareaElement textarea = new TextareaElement("a\uD83D\uDE00b");

    textarea.caretIndex(-10);
    assertEquals(0, textarea.caretIndex());
    textarea.caretIndex(99);
    assertEquals(4, textarea.caretIndex());
    textarea.caretIndex(2);
    assertEquals(1, textarea.caretIndex());
    assertEquals(1, textarea.selectionAnchor());

    textarea.select(2, 99);
    assertEquals(1, textarea.selectionAnchor());
    assertEquals(4, textarea.caretIndex());
    assertEquals(1, textarea.selectionStart());
    assertEquals(4, textarea.selectionEnd());
  }

  @Test
  void externallyAssignedIndices_snapBackwardFromSurrogateInterior() {
    TextareaElement textarea = new TextareaElement("a\uD83D\uDE00b");

    textarea.caretIndex(2);
    assertEquals(1, textarea.caretIndex());
    assertEquals(1, textarea.selectionAnchor());

    textarea.select(2, 3);
    assertEquals(1, textarea.selectionAnchor());
    assertEquals(3, textarea.caretIndex());

    textarea.selectionAnchor(2);
    assertEquals(1, textarea.selectionAnchor());
    assertEquals(3, textarea.caretIndex());

    textarea.value("ab");
    textarea.caretIndex(1);
    textarea.value("\uD83D\uDE00");
    assertEquals(0, textarea.caretIndex());
    assertEquals(0, textarea.selectionAnchor());

    textarea.value("a\uD83Db");
    textarea.caretIndex(2);
    assertEquals(2, textarea.caretIndex());
  }
}
