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
    if (!input.textInput() || keyCode == null || action == KeyAction.RELEASE) {
      return false;
    }

    return switch (keyCode) {
      case BACKSPACE -> backspace(input);
      case DELETE -> delete(input);
      case LEFT -> moveCaretLeft(input, extendSelection);
      case RIGHT -> moveCaretRight(input, extendSelection);
      case HOME -> moveCaret(input, 0, extendSelection);
      case END -> moveCaret(input, input.value().length(), extendSelection);
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
}
