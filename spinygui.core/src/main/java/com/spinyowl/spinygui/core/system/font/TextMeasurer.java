package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import lombok.NonNull;
import java.util.List;

public interface TextMeasurer {

  default TextMetrics measureText(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float lineHeight) {
    return measureText(text, 0, fonts, fontSize, lineHeight, Float.MAX_VALUE, false);
  }

  default TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    return measureText(text, offsetX, fonts.isEmpty() ? Font.DEFAULT : fonts.get(0), fontSize, lineHeight, maxWidth, wordWrap);
  }

  default TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float lineHeight) {
    return measureText(text, fonts, fontSize, lineHeight).lines().get(0);
  }

  default TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float offsetX) {
    return getTextCaretMetrics(text, fonts.isEmpty() ? Font.DEFAULT : fonts.get(0), fontSize, offsetX);
  }

  /**
   * Measures a single text line and returns horizontal and vertical font metrics in one result.
   *
   * @param text text to calculate metrics for.
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @return text metrics.
   */
  TextMetrics measureText(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight);

  /**
   * Measures text with wrapping and returns horizontal and vertical font metrics in one result.
   *
   * @param text text to calculate metrics for.
   * @param offsetX starting x offset for the first line of text.
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @param maxWidth maximum width of text in pixels.
   * @param wordWrap if true, text will be wrapped by nearest characters to maxWidth, otherwise text
   *     will be wrapped by spaces to fit maxWidth.
   * @return text metrics.
   */
  TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap);

  /**
   * Calculates text metrics.
   *
   * @param text text to calculate metrics for.
   * @param offsetX starting x offset for the first line of text.
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight height of line box. It specifies the minimum height of line boxes within the
   *     element. Default is <b>{@code 1}</b>.
   * @param maxWidth maximum width of text in pixels.
   * @param wordWrap if true, text will be wrapped by nearest characters to maxWidth, otherwise text
   *     will be wrapped by spaces to fit maxWidth.
   * @return text metrics
   */
  TextMetrics getTextMetrics(
      @NonNull String text,
      float offsetX,
      @NonNull Font font,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap);

  /**
   * Calculates text line metrics.
   *
   * @param text text to calculate metrics for.
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @return text line metrics.
   */
  TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight);

  /**
   * Calculates caret placement inside a single text line.
   *
   * @param text text to calculate caret position for.
   * @param font font to use.
   * @param fontSize font size.
   * @param offsetX horizontal offset from the start of the line.
   * @return caret character index and x offset from the start of the line.
   */
  TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float offsetX);
}
