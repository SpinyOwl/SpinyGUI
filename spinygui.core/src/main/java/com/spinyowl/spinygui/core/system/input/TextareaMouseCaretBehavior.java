package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.node.TextareaElement;
import lombok.NonNull;
import org.joml.Vector2fc;

/** Places the caret in a textarea from a mouse position. */
public class TextareaMouseCaretBehavior {

  @NonNull private final MultilineTextControlMetrics metrics;

  public TextareaMouseCaretBehavior(@NonNull MultilineTextControlMetrics metrics) {
    this.metrics = metrics;
  }

  public boolean placeCaret(
      TextareaElement textarea, Vector2fc cursorPosition, boolean extendSelection) {
    int previousCaretIndex = textarea.caretIndex();
    int previousSelectionAnchor = textarea.selectionAnchor();
    int nextCaretIndex = metrics.indexAt(textarea, cursorPosition);
    if (extendSelection) {
      textarea.select(textarea.selectionAnchor(), nextCaretIndex);
    } else {
      textarea.caretIndex(nextCaretIndex);
    }
    return previousCaretIndex != textarea.caretIndex()
        || previousSelectionAnchor != textarea.selectionAnchor();
  }
}
