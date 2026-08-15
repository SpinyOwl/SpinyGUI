package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import lombok.NonNull;
import java.util.List;

public interface TextMeasurer {

  /** Narrow diagnostics hook; implementations remain allocation-free when returning the default. */
  default DiagnosticSession diagnostics() {
    return DiagnosticSession.disabled();
  }

  /**
   * Measures text without a first-line offset or practical wrapping limit.
   *
   * <p><strong>Default implementation:</strong> Invokes the full list overload with zero offset,
   * {@link Float#MAX_VALUE} width, and character-boundary wrapping. List fallback behavior is
   * determined by that overload.
   *
   * @param text text to calculate metrics for.
   * @param fonts fonts offered to the implementation.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @return text metrics.
   */
  default TextMetrics measureText(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float lineHeight) {
    diagnostics().increment(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES);
    return measureText(text, 0, fonts, fontSize, lineHeight, Float.MAX_VALUE, false);
  }

  /**
   * Measures text with wrapping through the font-list compatibility overload.
   *
   * <p><strong>Default implementation:</strong> Delegates to the single-font overload using the
   * first list entry, or {@link Font#DEFAULT} when the list is empty. It does not resolve across
   * the full list; implementations may override this method to provide ordered fallback.
   *
   * @param text text to calculate metrics for.
   * @param offsetX initial occupied x extent for the first measured line, reducing its remaining
   *     {@code maxWidth} capacity; this is text-local and is not a layout, viewport, scroll, or
   *     presentation-transform coordinate.
   * @param fonts fonts offered to the implementation.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @param maxWidth maximum width of text in pixels.
   * @param wordWrap if true, wrap at the nearest preceding word boundary and fall back to a
   *     character boundary when none fits; if false, wrap at a character boundary.
   * @return text metrics.
   */
  default TextMetrics measureText(
      @NonNull String text,
      float offsetX,
      @NonNull List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    diagnostics().increment(
        TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES);
    return measureText(text, offsetX, fonts.isEmpty() ? Font.DEFAULT : fonts.get(0), fontSize, lineHeight, maxWidth, wordWrap);
  }

  default TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float lineHeight) {
    diagnostics().increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES);
    return measureText(text, fonts, fontSize, lineHeight).lines().get(0);
  }

  /**
   * Calculates caret placement inside the supplied text.
   *
   * <p><strong>Default implementation:</strong> Uses the first list entry, or {@link Font#DEFAULT}
   * when the list is empty.
   *
   * @param text text to calculate caret placement for.
   * @param fonts fonts offered to the implementation.
   * @param fontSize font size.
   * @param offsetX text-local horizontal hit-test offset from the start of the supplied text.
   * @return a UTF-16 offset into {@code text} and a text-local horizontal advance.
   */
  default TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull List<Font> fonts, float fontSize, float offsetX) {
    diagnostics().increment(
        TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES);
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
   * @param offsetX initial occupied x extent for the first measured line, reducing its remaining
   *     {@code maxWidth} capacity; this is text-local and is not a layout, viewport, scroll, or
   *     presentation-transform coordinate.
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @param maxWidth maximum width of text in pixels.
   * @param wordWrap if true, wrap at the nearest preceding word boundary and fall back to a
   *     character boundary when none fits; if false, wrap at a character boundary.
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
   * @param offsetX initial occupied x extent for the first measured line, reducing its remaining
   *     {@code maxWidth} capacity; this is text-local and is not a layout, viewport, scroll, or
   *     presentation-transform coordinate.
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight height of line box. It specifies the minimum height of line boxes within the
   *     element. Default is <b>{@code 1}</b>.
   * @param maxWidth maximum width of text in pixels.
   * @param wordWrap if true, wrap at the nearest preceding word boundary and fall back to a
   *     character boundary when none fits; if false, wrap at a character boundary.
   * @return text metrics.
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
   * @param offsetX text-local horizontal hit-test offset from the start of the supplied line.
   * @return a UTF-16 offset into {@code text} and a text-local horizontal advance.
   */
  TextCaretMetrics getTextCaretMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float offsetX);
}
