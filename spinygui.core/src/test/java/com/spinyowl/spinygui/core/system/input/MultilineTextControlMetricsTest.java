package com.spinyowl.spinygui.core.system.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.util.List;
import org.junit.jupiter.api.Test;

class MultilineTextControlMetricsTest {
  private final FontServiceImpl fontService = new FontServiceImpl(new FontStorageImpl(), false);
  private final MultilineTextControlMetrics metrics = new MultilineTextControlMetrics(fontService);

  @Test
  void caretAndHitTesting_useFallbackFaceForCjkAndKeepUtf16CaretBoundary() {
    TextareaElement textarea = textarea("a\u96EAb");

    MultilineTextControlMetrics.Caret cjkCaret = metrics.caret(textarea, 2);
    MultilineTextControlMetrics.Caret endCaret = metrics.caret(textarea, 3);

    assertEquals(2, cjkCaret.index());
    assertTrue(cjkCaret.x() > metrics.caret(textarea, 1).x());
    assertEquals(3, endCaret.index());
    assertTrue(endCaret.x() > cjkCaret.x());
  }

  @Test
  void caretAndHitTesting_keepSupplementaryCodePointAtomic() {
    TextareaElement textarea = textarea("a\uD83D\uDE00b");

    MultilineTextControlMetrics.Caret emojiCaret = metrics.caret(textarea, 3);
    assertEquals(3, emojiCaret.index());

    assertEquals(3, metrics.indexAt(textarea, new org.joml.Vector2f(emojiCaret.x(), 0)));
  }

  private TextareaElement textarea(String value) {
    TextareaElement textarea = new TextareaElement(value);
    textarea.resolvedStyle().fontFamilies(List.of(Font.ROBOTO_REGULAR.fontFamily(), Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily()));
    textarea.resolvedStyle().fontSize(com.spinyowl.spinygui.core.style.types.length.Length.pixel(16));
    textarea.resolvedStyle().lineHeight(1.2f);
    textarea.box().contentSize(400, 100);
    return textarea;
  }
}
