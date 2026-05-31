package com.spinyowl.spinygui.core.system.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TextInputViewportBehaviorTest {

  @Test
  void ensureCaretVisible_whenCaretIsRightOfVisibleContent_scrollsRight() {
    InputElement input = input("abcdef", 5);
    input.box().contentSize(30, 20);

    new TextInputViewportBehavior(new FixedTextMeasurer()).ensureCaretVisible(input);

    assertEquals(20, input.textScrollLeft());
  }

  @Test
  void ensureCaretVisible_whenCaretIsLeftOfVisibleContent_scrollsLeft() {
    InputElement input = input("abcdef", 1);
    input.box().contentSize(30, 20);
    input.textScrollLeft(30);

    new TextInputViewportBehavior(new FixedTextMeasurer()).ensureCaretVisible(input);

    assertEquals(10, input.textScrollLeft());
  }

  private InputElement input(String value, int caretIndex) {
    InputElement input = new InputElement();
    input.value(value);
    input.caretIndex(caretIndex);
    input.resolvedStyle().fontFamilies(Set.of(Font.DEFAULT.fontFamily()));
    input.resolvedStyle().fontSize(Length.pixel(16));
    input.resolvedStyle().lineHeight(1f);
    return input;
  }

  private static final class FixedTextMeasurer implements TextMeasurer {
    private static final float CHAR_WIDTH = 10;

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TextMetrics measureText(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TextMetrics getTextMetrics(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return TextLineMetrics.builder()
          .characters(text)
          .width(text.length() * CHAR_WIDTH)
          .height(16)
          .fontMetrics(new FontMetrics(12, 4, 0, 16, 12))
          .build();
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      throw new UnsupportedOperationException();
    }
  }
}
