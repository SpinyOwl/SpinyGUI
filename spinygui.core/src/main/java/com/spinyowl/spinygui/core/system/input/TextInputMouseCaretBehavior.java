package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import lombok.NonNull;
import org.joml.Vector2fc;

/** Places the caret in a single-line text input from a mouse position. */
public class TextInputMouseCaretBehavior {
  @NonNull private final ControlTextLayoutService layoutService;

  public TextInputMouseCaretBehavior(@NonNull TextMeasurer textMeasurer) {
    this(new ControlTextLayoutService(textMeasurer));
  }

  public TextInputMouseCaretBehavior(@NonNull ControlTextLayoutService layoutService) {
    this.layoutService = layoutService;
  }

  public boolean placeCaret(InputElement input, Vector2fc cursorPosition) {
    return placeCaret(input, cursorPosition, false);
  }

  public boolean placeCaret(InputElement input, Vector2fc cursorPosition, boolean extendSelection) {
    if (!input.textInput()) return false;
    float contentX = input.absolutePosition().x() + input.box().border().left()
        + input.box().padding().left();
    float localX = Math.max(0, Math.min(cursorPosition.x() - contentX,
        input.box().contentSize().x())) + input.textScrollLeft();
    int nextCaretIndex = layoutService.query(input)
        .indexAt(localX, 0, layoutService.diagnostics());
    int previousCaretIndex = input.caretIndex();
    int previousSelectionAnchor = input.selectionAnchor();
    if (extendSelection) input.select(input.selectionAnchor(), nextCaretIndex);
    else input.caretIndex(nextCaretIndex);
    return previousCaretIndex != input.caretIndex()
        || previousSelectionAnchor != input.selectionAnchor();
  }
}
