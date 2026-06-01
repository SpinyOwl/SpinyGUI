package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.util.TextUtil;

/** Backend-agnostic editing behavior for multiline textarea controls. */
public class TextareaBehavior {

  private final MultilineTextControlMetrics metrics;

  public TextareaBehavior() {
    this(null);
  }

  public TextareaBehavior(MultilineTextControlMetrics metrics) {
    this.metrics = metrics;
  }

  public boolean insertPrintable(TextareaElement textarea, int codepoint) {
    if (!isPrintable(codepoint)) {
      return false;
    }
    return replaceSelectionOrInsert(textarea, TextUtil.cpToStr(codepoint));
  }

  public boolean handleKey(TextareaElement textarea, KeyCode keyCode, KeyAction action) {
    return handleKey(textarea, keyCode, action, false);
  }

  public boolean handleKey(
      TextareaElement textarea, KeyCode keyCode, KeyAction action, boolean extendSelection) {
    if (keyCode == null || action == KeyAction.RELEASE) {
      return false;
    }

    return switch (keyCode) {
      case ENTER, NUMPAD_ENTER -> replaceSelectionOrInsert(textarea, "\n");
      case BACKSPACE -> backspace(textarea);
      case DELETE -> delete(textarea);
      case LEFT -> moveCaretLeft(textarea, extendSelection);
      case RIGHT -> moveCaretRight(textarea, extendSelection);
      case HOME -> moveCaret(textarea, lineStart(textarea), extendSelection);
      case END -> moveCaret(textarea, lineEnd(textarea), extendSelection);
      case UP ->
          metrics == null
              ? false
              : moveCaret(
                  textarea,
                  metrics.verticalCaretIndex(textarea, textarea.caretIndex(), -1),
                  extendSelection);
      case DOWN ->
          metrics == null
              ? false
              : moveCaret(
                  textarea,
                  metrics.verticalCaretIndex(textarea, textarea.caretIndex(), 1),
                  extendSelection);
      default -> false;
    };
  }

  private boolean replaceSelectionOrInsert(TextareaElement textarea, String text) {
    int start = textarea.selectionStart();
    int end = textarea.selectionEnd();
    textarea.value(textarea.value().substring(0, start) + text + textarea.value().substring(end));
    textarea.caretIndex(start + text.length());
    return true;
  }

  private boolean backspace(TextareaElement textarea) {
    if (deleteSelection(textarea)) {
      return true;
    }
    int caretIndex = textarea.caretIndex();
    if (caretIndex == 0) {
      return false;
    }
    String value = textarea.value();
    int previousIndex = value.offsetByCodePoints(caretIndex, -1);
    textarea.value(value.substring(0, previousIndex) + value.substring(caretIndex));
    textarea.caretIndex(previousIndex);
    return true;
  }

  private boolean delete(TextareaElement textarea) {
    if (deleteSelection(textarea)) {
      return true;
    }
    int caretIndex = textarea.caretIndex();
    String value = textarea.value();
    if (caretIndex == value.length()) {
      return false;
    }
    int nextIndex = value.offsetByCodePoints(caretIndex, 1);
    textarea.value(value.substring(0, caretIndex) + value.substring(nextIndex));
    textarea.caretIndex(caretIndex);
    return true;
  }

  private boolean moveCaretLeft(TextareaElement textarea, boolean extendSelection) {
    if (textarea.hasSelection() && !extendSelection) {
      return moveCaret(textarea, textarea.selectionStart(), false);
    }
    int caretIndex = textarea.caretIndex();
    if (caretIndex == 0) {
      return false;
    }
    return moveCaret(
        textarea, textarea.value().offsetByCodePoints(caretIndex, -1), extendSelection);
  }

  private boolean moveCaretRight(TextareaElement textarea, boolean extendSelection) {
    if (textarea.hasSelection() && !extendSelection) {
      return moveCaret(textarea, textarea.selectionEnd(), false);
    }
    int caretIndex = textarea.caretIndex();
    if (caretIndex == textarea.value().length()) {
      return false;
    }
    return moveCaret(textarea, textarea.value().offsetByCodePoints(caretIndex, 1), extendSelection);
  }

  private boolean moveCaret(TextareaElement textarea, int caretIndex, boolean extendSelection) {
    int previousCaretIndex = textarea.caretIndex();
    int previousSelectionAnchor = textarea.selectionAnchor();
    if (extendSelection) {
      textarea.select(textarea.selectionAnchor(), caretIndex);
    } else {
      textarea.caretIndex(caretIndex);
    }
    return previousCaretIndex != textarea.caretIndex()
        || previousSelectionAnchor != textarea.selectionAnchor();
  }

  private boolean deleteSelection(TextareaElement textarea) {
    if (!textarea.hasSelection()) {
      return false;
    }
    int start = textarea.selectionStart();
    int end = textarea.selectionEnd();
    textarea.value(textarea.value().substring(0, start) + textarea.value().substring(end));
    textarea.caretIndex(start);
    return true;
  }

  private int lineStart(TextareaElement textarea) {
    if (metrics != null) {
      return metrics.lineStart(textarea, textarea.caretIndex());
    }
    int index = textarea.caretIndex();
    return textarea.value().lastIndexOf('\n', Math.max(0, index - 1)) + 1;
  }

  private int lineEnd(TextareaElement textarea) {
    if (metrics != null) {
      return metrics.lineEnd(textarea, textarea.caretIndex());
    }
    int index = textarea.caretIndex();
    int newline = textarea.value().indexOf('\n', index);
    return newline < 0 ? textarea.value().length() : newline;
  }

  private boolean isPrintable(int codepoint) {
    return Character.isValidCodePoint(codepoint) && !Character.isISOControl(codepoint);
  }
}
