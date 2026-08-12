package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import lombok.NonNull;
import java.util.List;
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
                input.value(), findFonts(input.resolvedStyle()), fontSize(input), offsetX)
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

  private List<Font> findFonts(ResolvedStyle style) {
    if (style.fontFamilies() == null) {
      return List.of(Font.DEFAULT);
    }
    textMeasurer.diagnostics().increment(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS);
    return FontChainResolver.DEFAULT
        .resolve(
            style.fontFamilies(), style.fontStyle(), style.fontWeight(), FontStretch.NORMAL);
  }

  private float fontSize(InputElement input) {
    Length<?> fontSize = input.resolvedStyle().fontSize();
    return fontSize == null ? 16f : StyleUtils.getFontSize(input);
  }
}
