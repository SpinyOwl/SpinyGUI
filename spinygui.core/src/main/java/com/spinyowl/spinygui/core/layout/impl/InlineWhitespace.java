package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.style.types.WhiteSpace.NORMAL;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.NOWRAP;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.PRE_LINE;

import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;

final class InlineWhitespace {

  private InlineWhitespace() {}

  static String normalize(String text, ResolvedStyle style) {
    String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
    int tabSize = Math.max(1, style.tabSize() == null ? 4 : style.tabSize());
    normalized = normalized.replace("\t", " ".repeat(tabSize));
    WhiteSpace whiteSpace = style.whiteSpace();
    if (NORMAL.equals(whiteSpace) || NOWRAP.equals(whiteSpace)) {
      return normalized.replaceAll("\\s+", " ");
    }
    if (PRE_LINE.equals(whiteSpace)) {
      return normalized.replaceAll("[ \\f\\t\\x0B]+", " ");
    }
    return normalized;
  }
}
