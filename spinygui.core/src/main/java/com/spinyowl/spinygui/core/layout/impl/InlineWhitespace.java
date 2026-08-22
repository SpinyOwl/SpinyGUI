package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.style.ResolvedStyle;

final class InlineWhitespace {

  private InlineWhitespace() {}

  static String normalize(String text, ResolvedStyle style) {
    return PreparedInlineText.prepare(text, style, DiagnosticSession.disabled())
        .text();
  }
}
