package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import lombok.NonNull;

/** Font service, responsible for loading and caching font data. */
public interface FontService {

  /**
   * Loads font from file.
   *
   * @param path path to font file
   * @return loaded font
   * @throws FontLoadingException in case of font loading failure.
   */
  Font loadFont(String path) throws FontLoadingException;

  /**
   * Verifies if font exists and available to use.
   *
   * @param font font to verify.
   * @return true if font exists, false otherwise.
   */
  boolean isFontAvailable(@NonNull Font font);

  /**
   * Calculates font vertical metrics.
   *
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @return font metrics in pixels.
   */
  FontMetrics getFontMetrics(@NonNull Font font, float fontSize, float lineHeight);
}
