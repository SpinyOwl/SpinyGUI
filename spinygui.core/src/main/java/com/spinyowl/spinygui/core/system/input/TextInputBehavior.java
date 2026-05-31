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
    int caretIndex = input.caretIndex();
    input.value(
        input.value().substring(0, caretIndex) + text + input.value().substring(caretIndex));
    input.caretIndex(caretIndex + text.length());
    return true;
  }

  public boolean handleKey(InputElement input, KeyCode keyCode, KeyAction action) {
    if (!input.textInput() || keyCode == null || action == KeyAction.RELEASE) {
      return false;
    }

    return switch (keyCode) {
      case BACKSPACE -> backspace(input);
      case DELETE -> delete(input);
      case LEFT -> moveCaretLeft(input);
      case RIGHT -> moveCaretRight(input);
      case HOME -> moveCaret(input, 0);
      case END -> moveCaret(input, input.value().length());
      default -> false;
    };
  }

  private boolean backspace(InputElement input) {
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

  private boolean moveCaretLeft(InputElement input) {
    int caretIndex = input.caretIndex();
    if (caretIndex == 0) {
      return false;
    }
    return moveCaret(input, input.value().offsetByCodePoints(caretIndex, -1));
  }

  private boolean moveCaretRight(InputElement input) {
    int caretIndex = input.caretIndex();
    if (caretIndex == input.value().length()) {
      return false;
    }
    return moveCaret(input, input.value().offsetByCodePoints(caretIndex, 1));
  }

  private boolean moveCaret(InputElement input, int caretIndex) {
    int previousCaretIndex = input.caretIndex();
    input.caretIndex(caretIndex);
    return previousCaretIndex != input.caretIndex();
  }

  private boolean isPrintable(int codepoint) {
    return Character.isValidCodePoint(codepoint) && !Character.isISOControl(codepoint);
  }
}
