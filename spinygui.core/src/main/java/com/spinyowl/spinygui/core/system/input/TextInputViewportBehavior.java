package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import lombok.NonNull;

/** Keeps the single-line text input caret inside the visible content box. */
public class TextInputViewportBehavior {
  private static final float CARET_VISIBILITY_PADDING = 2f;
  @NonNull private final ControlTextLayoutService layoutService;

  public TextInputViewportBehavior(@NonNull TextMeasurer textMeasurer) {
    this(new ControlTextLayoutService(textMeasurer));
  }

  public TextInputViewportBehavior(@NonNull ControlTextLayoutService layoutService) {
    this.layoutService = layoutService;
  }

  public boolean ensureCaretVisible(InputElement input) {
    if (!input.textInput()) return false;
    float caretX = layoutService.query(input).caret(input.caretIndex(), layoutService.diagnostics()).x();
    float previousScrollLeft = input.textScrollLeft();
    float contentWidth = input.box().contentSize().x();
    if (caretX < previousScrollLeft + CARET_VISIBILITY_PADDING) {
      input.textScrollLeft(caretX - CARET_VISIBILITY_PADDING);
    } else if (caretX > previousScrollLeft + contentWidth - CARET_VISIBILITY_PADDING) {
      input.textScrollLeft(caretX - contentWidth + CARET_VISIBILITY_PADDING);
    }
    return previousScrollLeft != input.textScrollLeft();
  }
}
