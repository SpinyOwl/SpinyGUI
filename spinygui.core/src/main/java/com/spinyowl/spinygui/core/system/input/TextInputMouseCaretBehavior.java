package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.joml.Vector2fc;

/** Places the caret in a single-line text input from a mouse position. */
public class TextInputMouseCaretBehavior {

  @NonNull private final TextMeasurer textMeasurer;

  public TextInputMouseCaretBehavior(@NonNull TextMeasurer textMeasurer) {
    this.textMeasurer = textMeasurer;
  }

  public boolean placeCaret(InputElement input, Vector2fc cursorPosition) {
    return placeCaret(input, cursorPosition, false);
  }

  public boolean placeCaret(InputElement input, Vector2fc cursorPosition, boolean extendSelection) {
    if (!input.textInput()) {
      return false;
    }

    float offsetX = textOffset(input, cursorPosition);
    int previousCaretIndex = input.caretIndex();
    int previousSelectionAnchor = input.selectionAnchor();
    int nextCaretIndex =
        textMeasurer
            .getTextCaretMetrics(
                input.value(), findFont(input.resolvedStyle()), fontSize(input), offsetX)
            .charIndex();
    if (extendSelection) {
      input.select(input.selectionAnchor(), nextCaretIndex);
    } else {
      input.caretIndex(nextCaretIndex);
    }
    return previousCaretIndex != input.caretIndex()
        || previousSelectionAnchor != input.selectionAnchor();
  }

  private float textOffset(InputElement input, Vector2fc cursorPosition) {
    float contentX =
        input.absolutePosition().x() + input.box().border().left() + input.box().padding().left();
    float localContentX = cursorPosition.x() - contentX;
    localContentX = Math.max(0, Math.min(localContentX, input.box().contentSize().x()));
    return localContentX + input.textScrollLeft();
  }

  private Font findFont(ResolvedStyle style) {
    Set<String> fontFamilies = style.fontFamilies();
    if (fontFamilies == null) {
      return Font.DEFAULT;
    }
    Set<Font> fonts =
        fontFamilies.stream()
            .map(f -> Font.find(f, style.fontStyle(), style.fontWeight()))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    return fonts.stream().findFirst().orElse(Font.DEFAULT);
  }

  private float fontSize(InputElement input) {
    Length<?> fontSize = input.resolvedStyle().fontSize();
    return fontSize == null ? 16f : StyleUtils.getFontSize(input);
  }
}
