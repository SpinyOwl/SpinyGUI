package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FontServiceImplTest {
  private FontServiceImpl fontService;

  @BeforeEach
  void setUp() {
    fontService = new FontServiceImpl(new FontStorageImpl(), false);
  }

  @Test
  void measureText_whenSingleLine_returnsWidthAndVerticalMetrics() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.2f);

    assertEquals(1, metrics.lines().size());
    assertTrue(metrics.width() > 0);
    assertTrue(metrics.height() > 0);
    assertTrue(metrics.fontMetrics().ascent() > 0);
    assertTrue(metrics.fontMetrics().descent() > 0);
    assertEquals(metrics.fontMetrics().baseline(), metrics.lines().get(0).baseline());
  }

  @Test
  void compatibilityWrappers_matchUnifiedApi() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.2f);
    TextLineMetrics line = fontService.getTextLineMetrics("abc", Font.DEFAULT, 16, 1.2f);

    assertEquals(metrics.lines().get(0), line);
    assertEquals(metrics.fontMetrics(), fontService.getFontMetrics(Font.DEFAULT, 16, 1.2f));
  }

  @Test
  void measureText_whenMaxWidthIsNearZero_returnsEmptyMetrics() {
    TextMetrics metrics = fontService.measureText("abc", 0, Font.DEFAULT, 16, 1.2f, 0, true);

    assertTrue(metrics.lines().isEmpty());
    assertEquals(0, metrics.width());
    assertEquals(0, metrics.height());
  }

  @Test
  void measureText_whenWrapped_preservesLineOrder() {
    TextMetrics metrics = fontService.measureText("aa aa", 0, Font.DEFAULT, 16, 1.2f, 20, true);

    assertFalse(metrics.lines().isEmpty());
    assertEquals("aa", metrics.lines().get(0).characters().toString());
  }

  @Test
  void measureText_whenWhitespace_reportsAdvance() {
    TextMetrics metrics = fontService.measureText(" ", Font.DEFAULT, 16, 1.2f);

    assertTrue(metrics.width() > 0);
  }

  @Test
  void measureText_roundsGlyphAdvancesLikeNanoVgFontStash() {
    TextMetrics metrics = fontService.measureText("Horizontal auto", Font.DEFAULT, 16, 1.2f);

    assertEquals(Math.round(metrics.width()), metrics.width());
  }

  @Test
  void measureText_usesStbMappingEmScaleLikeNanoVgFontStash() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.0f);

    assertTrue(metrics.fontMetrics().ascent() + metrics.fontMetrics().descent() > 16);
    assertEquals(metrics.fontMetrics().ascent(), metrics.fontMetrics().baseline());
  }

  @Test
  void getTextCaretMetrics_whenOffsetBeforeLine_returnsStartCaret() {
    TextCaretMetrics caret = fontService.getTextCaretMetrics("abc", Font.DEFAULT, 16, -1);

    assertEquals(0, caret.charIndex());
    assertEquals(0, caret.x());
  }

  @Test
  void getTextCaretMetrics_whenOffsetPastLine_returnsEndCaretAtMeasuredWidth() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.2f);

    TextCaretMetrics caret = fontService.getTextCaretMetrics("abc", Font.DEFAULT, 16, 10_000);

    assertEquals(3, caret.charIndex());
    assertEquals(metrics.width(), caret.x());
  }

  @Test
  void getTextCaretMetrics_whenOffsetPassesFirstGlyph_returnsNextCaretStop() {
    TextMetrics firstGlyph = fontService.measureText("a", Font.DEFAULT, 16, 1.2f);

    TextCaretMetrics caret =
        fontService.getTextCaretMetrics("abc", Font.DEFAULT, 16, firstGlyph.width() + 1);

    assertTrue(caret.charIndex() >= 1);
    assertTrue(caret.x() >= firstGlyph.width());
  }
}
