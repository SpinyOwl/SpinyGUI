package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;

/**
 * One source code point and the face/code point selected to display it.
 *
 * @param sourceStart absolute UTF-16 start offset in the original measured text.
 * @param sourceEnd absolute, exclusive-end UTF-16 offset in the original measured text.
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
