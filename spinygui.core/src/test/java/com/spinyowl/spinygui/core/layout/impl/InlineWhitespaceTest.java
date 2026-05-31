package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import org.junit.jupiter.api.Test;

class InlineWhitespaceTest {

  @Test
  void normalize_whenWhiteSpaceNormal_collapsesWhitespaceRunsIncludingNewlines() {
    ResolvedStyle style = style(WhiteSpace.NORMAL, 4);

    assertEquals(" alpha beta gamma ", InlineWhitespace.normalize(" alpha\tbeta\r\ngamma ", style));
  }

  @Test
  void normalize_whenWhiteSpaceNowrap_collapsesWhitespaceRunsIncludingNewlines() {
    ResolvedStyle style = style(WhiteSpace.NOWRAP, 4);

    assertEquals(" alpha beta gamma ", InlineWhitespace.normalize(" alpha\tbeta\ngamma ", style));
  }

  @Test
  void normalize_whenWhiteSpacePre_preservesSpacesTabsAsExpandedSpacesAndNewlines() {
    ResolvedStyle style = style(WhiteSpace.PRE, 2);

    assertEquals("alpha  beta\ngamma", InlineWhitespace.normalize("alpha\tbeta\r\ngamma", style));
  }

  @Test
  void normalize_whenWhiteSpacePreWrap_preservesSpacesTabsAsExpandedSpacesAndNewlines() {
    ResolvedStyle style = style(WhiteSpace.PRE_WRAP, 3);

    assertEquals("alpha   beta\ngamma", InlineWhitespace.normalize("alpha\tbeta\rgamma", style));
  }

  @Test
  void normalize_whenWhiteSpacePreLine_collapsesSpacesButPreservesNewlines() {
    ResolvedStyle style = style(WhiteSpace.PRE_LINE, 4);

    assertEquals("alpha beta\ngamma", InlineWhitespace.normalize("alpha \t beta\r\ngamma", style));
  }

  @Test
  void normalize_whenTabSizeIsLessThanOne_usesSingleSpace() {
    ResolvedStyle style = style(WhiteSpace.PRE, 0);

    assertEquals("alpha beta", InlineWhitespace.normalize("alpha\tbeta", style));
  }

  private ResolvedStyle style(WhiteSpace whiteSpace, Integer tabSize) {
    ResolvedStyle style = new ResolvedStyle();
    style.whiteSpace(whiteSpace);
    style.tabSize(tabSize);
    return style;
  }
}
