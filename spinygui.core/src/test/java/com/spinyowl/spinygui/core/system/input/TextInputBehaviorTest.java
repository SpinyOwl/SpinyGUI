package com.spinyowl.spinygui.core.system.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.InputElement;
import org.junit.jupiter.api.Test;

class TextInputBehaviorTest {

  private final TextInputBehavior behavior = new TextInputBehavior();

  @Test
  void paste_retainsUnicodeTextAndCopiesItBackExactly() {
    String text = "R\u00f8gue \u96ea Seed";
    InputElement input = new InputElement();
    Clipboard clipboard = new Clipboard(text);

    assertTrue(behavior.handleShortcut(input, KeyCode.KEY_V, KeyAction.PRESS, clipboard));
    assertEquals(text, input.value());
    assertEquals(text.length(), input.caretIndex());

    input.select(0, input.value().length());
    behavior.handleShortcut(input, KeyCode.KEY_C, KeyAction.PRESS, clipboard);
    assertEquals(text, clipboard.getText());
  }

  @Test
  void editing_supplementaryCodePointDoesNotSplitSurrogatePair() {
    String emoji = new String(Character.toChars(0x1F600));
    InputElement input = new InputElement();

    assertTrue(behavior.insertPrintable(input, 0x1F600));
    assertEquals(emoji, input.value());
    assertEquals(2, input.caretIndex());

    assertTrue(behavior.handleKey(input, KeyCode.LEFT, KeyAction.PRESS));
    assertEquals(0, input.caretIndex());
    assertTrue(behavior.handleKey(input, KeyCode.DELETE, KeyAction.PRESS));
    assertEquals("", input.value());
  }

  @Test
  void externallyAssignedIndices_clampAsUtf16OffsetsAndSnapFromSurrogateInterior() {
    InputElement input = new InputElement();
    input.value("a\uD83D\uDE00b");

    input.caretIndex(-10);
    assertEquals(0, input.caretIndex());
    input.caretIndex(99);
    assertEquals(4, input.caretIndex());
    input.caretIndex(2);
    assertEquals(1, input.caretIndex());
    assertEquals(1, input.selectionAnchor());

    input.select(2, 99);
    assertEquals(1, input.selectionAnchor());
    assertEquals(4, input.caretIndex());
    assertEquals(1, input.selectionStart());
    assertEquals(4, input.selectionEnd());
  }

  @Test
  void externallyAssignedIndices_snapBackwardFromSurrogateInterior() {
    InputElement input = new InputElement();
    input.value("a\uD83D\uDE00b");

    input.caretIndex(2);
    assertEquals(1, input.caretIndex());
    assertEquals(1, input.selectionAnchor());

    input.select(2, 3);
    assertEquals(1, input.selectionAnchor());
    assertEquals(3, input.caretIndex());

    input.selectionAnchor(2);
    assertEquals(1, input.selectionAnchor());
    assertEquals(3, input.caretIndex());

    input.value("ab");
    input.caretIndex(1);
    input.value("\uD83D\uDE00");
    assertEquals(0, input.caretIndex());
    assertEquals(0, input.selectionAnchor());

    input.value("a\uD83Db");
    input.caretIndex(2);
    assertEquals(2, input.caretIndex());
  }

  private static final class Clipboard implements TextInputBehavior.TextClipboard {
    private String text;

    private Clipboard(String text) {
      this.text = text;
    }

    @Override
    public String getText() {
      return text;
    }

    @Override
    public void setText(String text) {
      this.text = text;
    }
  }
}
