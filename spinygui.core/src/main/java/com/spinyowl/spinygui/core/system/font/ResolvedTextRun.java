package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import java.util.List;

/** Immutable contiguous text run resolved to one face. */
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
