package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.util.TextUtil;

/** Backend-agnostic editing behavior for single-line text inputs. */
public class TextInputBehavior {

  public boolean insertPrintable(InputElement input, int codepoint) {
    if (!input.textInput() || !isPrintable(codepoint)) {
      return false;
    }

    String text = TextUtil.cpToStr(codepoint);
    int start = input.selectionStart();
    int end = input.selectionEnd();
    input.value(input.value().substring(0, start) + text + input.value().substring(end));
    input.caretIndex(start + text.length());
    return true;
  }

  public boolean handleKey(InputElement input, KeyCode keyCode, KeyAction action) {
    return handleKey(input, keyCode, action, false);
  }

  public boolean handleKey(
      InputElement input, KeyCode keyCode, KeyAction action, boolean extendSelection) {
    return handleKey(input, keyCode, action, extendSelection, false);
  }

  public boolean handleKey(
      InputElement input,
      KeyCode keyCode,
      KeyAction action,
      boolean extendSelection,
      boolean wordNavigation) {
    if (!input.textInput() || keyCode == null || action == KeyAction.RELEASE) {
      return false;
    }

    return switch (keyCode) {
      case BACKSPACE -> backspace(input);
      case DELETE -> delete(input);
      case LEFT ->
          wordNavigation
              ? moveCaretToPreviousWord(input, extendSelection)
              : moveCaretLeft(input, extendSelection);
      case RIGHT ->
          wordNavigation
              ? moveCaretToNextWord(input, extendSelection)
              : moveCaretRight(input, extendSelection);
      case HOME -> moveCaret(input, 0, extendSelection);
      case END -> moveCaret(input, input.value().length(), extendSelection);
      default -> false;
    };
  }

  public boolean handleShortcut(
      InputElement input,
      KeyCode keyCode,
      KeyAction action,
      TextClipboard clipboard) {
    if (!input.textInput() || keyCode == null || action != KeyAction.PRESS) {
      return false;
    }

    return switch (keyCode) {
      case KEY_A -> selectAll(input);
      case KEY_C -> copy(input, clipboard);
      case KEY_V -> paste(input, clipboard);
      case KEY_X -> cut(input, clipboard);
      default -> false;
    };
  }

  private boolean backspace(InputElement input) {
    if (deleteSelection(input)) {
      return true;
    }
    int caretIndex = input.caretIndex();
    if (caretIndex == 0) {
      return false;
    }

    String value = input.value();
    int previousIndex = value.offsetByCodePoints(caretIndex, -1);
    input.value(value.substring(0, previousIndex) + value.substring(caretIndex));
    input.caretIndex(previousIndex);
    return true;
  }

  private boolean delete(InputElement input) {
    if (deleteSelection(input)) {
      return true;
    }
    int caretIndex = input.caretIndex();
    String value = input.value();
    if (caretIndex == value.length()) {
      return false;
    }

    int nextIndex = value.offsetByCodePoints(caretIndex, 1);
    input.value(value.substring(0, caretIndex) + value.substring(nextIndex));
    input.caretIndex(caretIndex);
    return true;
  }

  private boolean moveCaretLeft(InputElement input, boolean extendSelection) {
    if (input.hasSelection() && !extendSelection) {
      return moveCaret(input, input.selectionStart(), false);
    }
    int caretIndex = input.caretIndex();
    if (caretIndex == 0) {
      return false;
    }
    return moveCaret(input, input.value().offsetByCodePoints(caretIndex, -1), extendSelection);
  }

  private boolean moveCaretRight(InputElement input, boolean extendSelection) {
    if (input.hasSelection() && !extendSelection) {
      return moveCaret(input, input.selectionEnd(), false);
    }
    int caretIndex = input.caretIndex();
    if (caretIndex == input.value().length()) {
      return false;
    }
    return moveCaret(input, input.value().offsetByCodePoints(caretIndex, 1), extendSelection);
  }

  private boolean moveCaretToPreviousWord(InputElement input, boolean extendSelection) {
    if (input.hasSelection() && !extendSelection) {
      return moveCaret(input, input.selectionStart(), false);
    }
    return moveCaret(input, previousWordBoundary(input.value(), input.caretIndex()), extendSelection);
  }

  private boolean moveCaretToNextWord(InputElement input, boolean extendSelection) {
    if (input.hasSelection() && !extendSelection) {
      return moveCaret(input, input.selectionEnd(), false);
    }
    return moveCaret(input, nextWordBoundary(input.value(), input.caretIndex()), extendSelection);
  }

  private boolean moveCaret(InputElement input, int caretIndex, boolean extendSelection) {
    int previousCaretIndex = input.caretIndex();
    int previousSelectionAnchor = input.selectionAnchor();
    if (extendSelection) {
      input.select(input.selectionAnchor(), caretIndex);
    } else {
      input.caretIndex(caretIndex);
    }
    return previousCaretIndex != input.caretIndex()
        || previousSelectionAnchor != input.selectionAnchor();
  }

  private boolean selectAll(InputElement input) {
    int previousCaretIndex = input.caretIndex();
    int previousSelectionAnchor = input.selectionAnchor();
    input.select(0, input.value().length());
    return previousCaretIndex != input.caretIndex()
        || previousSelectionAnchor != input.selectionAnchor();
  }

  private boolean copy(InputElement input, TextClipboard clipboard) {
    if (clipboard == null || !input.hasSelection()) {
      return false;
    }
    clipboard.setText(input.value().substring(input.selectionStart(), input.selectionEnd()));
    return false;
  }

  private boolean cut(InputElement input, TextClipboard clipboard) {
    if (clipboard == null || !input.hasSelection()) {
      return false;
    }
    clipboard.setText(input.value().substring(input.selectionStart(), input.selectionEnd()));
    return deleteSelection(input);
  }

  private boolean paste(InputElement input, TextClipboard clipboard) {
    if (clipboard == null) {
      return false;
    }
    String text = printableText(clipboard.getText());
    if (text.isEmpty()) {
      return false;
    }

    int start = input.selectionStart();
    int end = input.selectionEnd();
    input.value(input.value().substring(0, start) + text + input.value().substring(end));
    input.caretIndex(start + text.length());
    return true;
  }

  private boolean deleteSelection(InputElement input) {
    if (!input.hasSelection()) {
      return false;
    }
    int start = input.selectionStart();
    int end = input.selectionEnd();
    input.value(input.value().substring(0, start) + input.value().substring(end));
    input.caretIndex(start);
    return true;
  }

  private boolean isPrintable(int codepoint) {
    return Character.isValidCodePoint(codepoint) && !Character.isISOControl(codepoint);
  }

  private String printableText(String text) {
    if (text == null || text.isEmpty()) {
      return "";
    }
    StringBuilder builder = new StringBuilder(text.length());
    text.codePoints().filter(this::isPrintable).forEach(builder::appendCodePoint);
    return builder.toString();
  }

  private int previousWordBoundary(String value, int index) {
    int cursor = index;
    while (cursor > 0 && isWhitespaceBefore(value, cursor)) {
      cursor = value.offsetByCodePoints(cursor, -1);
    }
    if (cursor == 0) {
      return 0;
    }

    int previousIndex = value.offsetByCodePoints(cursor, -1);
    CharacterKind kind = kindAt(value, previousIndex);
    cursor = previousIndex;
    while (cursor > 0) {
      int before = value.offsetByCodePoints(cursor, -1);
      if (kindAt(value, before) != kind) {
        break;
      }
      cursor = before;
    }
    return cursor;
  }

  private int nextWordBoundary(String value, int index) {
    int cursor = index;
    int length = value.length();
    if (cursor >= length) {
      return length;
    }

    if (!isWhitespaceAt(value, cursor)) {
      CharacterKind kind = kindAt(value, cursor);
      cursor = value.offsetByCodePoints(cursor, 1);
      while (cursor < length && kindAt(value, cursor) == kind) {
        cursor = value.offsetByCodePoints(cursor, 1);
      }
    }
    while (cursor < length && isWhitespaceAt(value, cursor)) {
      cursor = value.offsetByCodePoints(cursor, 1);
    }
    return cursor;
  }

  private boolean isWhitespaceBefore(String value, int index) {
    return Character.isWhitespace(value.codePointBefore(index));
  }

  private boolean isWhitespaceAt(String value, int index) {
    return Character.isWhitespace(value.codePointAt(index));
  }

  private CharacterKind kindAt(String value, int index) {
    int codepoint = value.codePointAt(index);
    if (Character.isWhitespace(codepoint)) {
      return CharacterKind.WHITESPACE;
    }
    if (Character.isLetterOrDigit(codepoint) || codepoint == '_') {
      return CharacterKind.WORD;
    }
    return CharacterKind.PUNCTUATION;
  }

  private enum CharacterKind {
    WORD,
    WHITESPACE,
    PUNCTUATION
  }

  public interface TextClipboard {

    String getText();

    void setText(String text);
  }
}
