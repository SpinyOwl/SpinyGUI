package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import java.util.List;

/**
 * Immutable contiguous text run resolved to one face.
 *
 * @param sourceStart UTF-16 start in the containing result's coordinate space: absolute measured
 *     source for {@link TextMetrics}, or fragment-local rendered text for an {@code InlineFragment}.
 * @param sourceEnd exclusive UTF-16 end in the same containing-result coordinate space.
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
