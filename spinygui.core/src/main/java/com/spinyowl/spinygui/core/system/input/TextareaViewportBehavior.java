package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.node.TextareaElement;
import lombok.NonNull;

/** Keeps the textarea caret inside the visible content box. */
public class TextareaViewportBehavior {
  private static final float CARET_VISIBILITY_PADDING = 2f;

  @NonNull private final MultilineTextControlMetrics metrics;

  public TextareaViewportBehavior(@NonNull MultilineTextControlMetrics metrics) {
    this.metrics = metrics;
  }

  public boolean ensureCaretVisible(TextareaElement textarea) {
    MultilineTextControlMetrics.Caret caret = metrics.caret(textarea, textarea.caretIndex());
    float previousScrollLeft = textarea.textScrollLeft();
    float previousScrollTop = textarea.textScrollTop();
    float contentWidth = textarea.box().contentSize().x();
    float contentHeight = textarea.box().contentSize().y();

    if (caret.x() < previousScrollLeft + CARET_VISIBILITY_PADDING) {
      textarea.textScrollLeft(caret.x() - CARET_VISIBILITY_PADDING);
    } else if (caret.x() > previousScrollLeft + contentWidth - CARET_VISIBILITY_PADDING) {
      textarea.textScrollLeft(caret.x() - contentWidth + CARET_VISIBILITY_PADDING);
    }

    if (caret.y() < previousScrollTop + CARET_VISIBILITY_PADDING) {
      textarea.textScrollTop(caret.y() - CARET_VISIBILITY_PADDING);
    } else if (caret.y() + caret.height()
        > previousScrollTop + contentHeight - CARET_VISIBILITY_PADDING) {
      textarea.textScrollTop(
          caret.y() + caret.height() - contentHeight + CARET_VISIBILITY_PADDING);
    }

    return previousScrollLeft != textarea.textScrollLeft()
        || previousScrollTop != textarea.textScrollTop();
  }
}
