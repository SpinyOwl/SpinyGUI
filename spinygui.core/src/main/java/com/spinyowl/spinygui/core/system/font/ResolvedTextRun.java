package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import java.util.List;

/**
 * Immutable contiguous text run resolved to one face.
 *
 * @param sourceStart absolute UTF-16 start offset in the original measured text.
 * @param sourceEnd absolute, exclusive-end UTF-16 offset in the original measured text.
 * @param font face selected for the run.
 * @param glyphs resolved source glyphs in UTF-16 source order.
 * @param advance line-local horizontal advance of the run in pixels; it excludes the measurement's
 *     initial x offset and all layout, viewport, scroll, and presentation transforms.
 */
public record ResolvedTextRun(
    int sourceStart, int sourceEnd, Font font, List<ResolvedGlyph> glyphs, float advance) {
  public ResolvedTextRun {
    glyphs = List.copyOf(glyphs);
  }

  public boolean replacementMarker() {
    return glyphs.stream().anyMatch(ResolvedGlyph::replacement);
  }

  public String renderedText() {
    StringBuilder text = new StringBuilder();
    glyphs.forEach(glyph -> text.appendCodePoint(glyph.renderedCodePoint()));
    return text.toString();
  }
}
