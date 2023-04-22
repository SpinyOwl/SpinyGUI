package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import java.util.Set;
import lombok.NonNull;

/** Font service, responsible for loading and caching font data, and calculating text metrics. */
public interface FontService {

  /**
   * Loads font from file.
   *
   * @param path path to font file
   * @return loaded font
   * @throws FontLoadingException in case of font loading failure.
   */
  Font loadFont(String path) throws FontLoadingException;

  default Font getFirstAvailableFont(Set<Font> fonts) {
    return fonts.stream().filter(this::isFontAvailable).findFirst().orElse(Font.DEFAULT);
  }

  /**
   * Verifies if font exists and available to use.
   *
   * @param font font to verify.
   * @return true if font exists, false otherwise.
   */
  boolean isFontAvailable(@NonNull Font font);

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
   * @return text line metrics.
   */
  TextLineMetrics getTextLineMetrics(
      @NonNull String text, @NonNull Font font, float fontSize, float lineHeight);
}
