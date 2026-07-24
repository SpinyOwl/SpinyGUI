package com.spinyowl.spinygui.benchmark;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TextWorkloadsTest {

  @Test
  void providesDeterministicTextCoverage() {
    assertTrue(TextWorkloads.LATIN.contains("quick brown fox"));
    assertTrue(TextWorkloads.WRAPPED_PARAGRAPH.length() > TextWorkloads.LATIN.length());
    assertTrue(TextWorkloads.MIXED_CJK.codePoints().anyMatch(codePoint -> codePoint == 0x4E2D));
    assertTrue(TextWorkloads.SUPPLEMENTARY_UNICODE.codePoints().anyMatch(codePoint -> codePoint > 0xFFFF));
    assertTrue(TextWorkloads.MISSING_GLYPHS.codePoints().anyMatch(codePoint -> codePoint == 0x10FFFF));
    assertTrue(TextWorkloads.LONG_SINGLE_FONT.length() >= 5_000);
  }
}
