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

/** Keeps the single-line text input caret inside the visible content box. */
public class TextInputViewportBehavior {
  private static final float CARET_VISIBILITY_PADDING = 2f;

  @NonNull private final TextMeasurer textMeasurer;

  public TextInputViewportBehavior(@NonNull TextMeasurer textMeasurer) {
    this.textMeasurer = textMeasurer;
  }

  public boolean ensureCaretVisible(InputElement input) {
    if (!input.textInput()) {
      return false;
    }
    float caretX = caretX(input);
    float previousScrollLeft = input.textScrollLeft();
    float contentWidth = input.box().contentSize().x();
    if (caretX < previousScrollLeft + CARET_VISIBILITY_PADDING) {
      input.textScrollLeft(caretX - CARET_VISIBILITY_PADDING);
    } else if (caretX > previousScrollLeft + contentWidth - CARET_VISIBILITY_PADDING) {
      input.textScrollLeft(caretX - contentWidth + CARET_VISIBILITY_PADDING);
    }
    return previousScrollLeft != input.textScrollLeft();
  }

  private float caretX(InputElement input) {
    int caretIndex = Math.max(0, Math.min(input.caretIndex(), input.value().length()));
    ResolvedStyle style = input.resolvedStyle();
    return textMeasurer
        .getTextLineMetrics(
            input.value().substring(0, caretIndex),
            findFont(style),
            fontSize(input),
            lineHeight(style))
        .width();
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

  private float lineHeight(ResolvedStyle style) {
    Float lineHeight = style.lineHeight();
    return lineHeight == null ? 1f : lineHeight;
  }
}
