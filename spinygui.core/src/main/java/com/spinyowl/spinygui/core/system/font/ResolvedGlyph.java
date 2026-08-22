package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;

/**
 * One source code point and the face/code point selected to display it.
 *
 * @param sourceStart UTF-16 start in the containing result's coordinate space: absolute measured
 *     source for {@link TextMetrics}, or fragment-local rendered text for an {@code InlineFragment}.
 * @param sourceEnd exclusive UTF-16 end in the same containing-result coordinate space.
 * @param sourceCodePoint code point read from the original measured text.
 * @param renderedCodePoint code point requested for rendering; it may resolve to {@code .notdef}.
 * @param font face selected for the resolved glyph.
 * @param replacement whether the rendered code point replaces an unavailable source glyph.
 */
public record ResolvedGlyph(
    int sourceStart,
    int sourceEnd,
    int sourceCodePoint,
    int renderedCodePoint,
    Font font,
    boolean replacement) {}
