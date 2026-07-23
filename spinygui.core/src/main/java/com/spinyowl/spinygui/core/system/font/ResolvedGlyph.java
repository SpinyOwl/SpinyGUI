package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;

/** One source code point and the face/code point selected to display it. */
public record ResolvedGlyph(
    int sourceStart,
    int sourceEnd,
    int sourceCodePoint,
    int renderedCodePoint,
    Font font,
    boolean replacement) {}
