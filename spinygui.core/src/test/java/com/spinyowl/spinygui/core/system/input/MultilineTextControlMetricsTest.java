package com.spinyowl.spinygui.core.system.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

class MultilineTextControlMetricsTest {
  private final FontServiceImpl fontService = installedService();
  private final MultilineTextControlMetrics metrics = new MultilineTextControlMetrics(fontService);

  @AfterEach
  void closeFontOwner() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
  }

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

  @Test
  void independentTextareaQueriesReuseOneCurrentSnapshotWithoutChangingResults() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
    FontServiceImpl instrumentedFontService =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumentedFontService.installSemanticOwner();
    MultilineTextControlMetrics instrumentedMetrics =
        new MultilineTextControlMetrics(instrumentedFontService);
    TextareaElement textarea = textarea("abc");

    List<MultilineTextControlMetrics.Line> lines = instrumentedMetrics.lines(textarea);
    MultilineTextControlMetrics.Caret first = instrumentedMetrics.caret(textarea, 1);
    MultilineTextControlMetrics.Caret second = instrumentedMetrics.caret(textarea, 2);

    assertEquals("abc", lines.getFirst().text());
    assertEquals(1, first.index());
    assertEquals(2, second.index());
    assertTrue(second.x() > first.x());
    assertEquals(
        1, diagnostics.snapshot().value(TextDiagnosticCounter.TEXTAREA_COMPLETE_LAYOUTS));
  }

  @Test
  void textareaMetricsObserveOwnerMutationThroughTheSharedResolver() {
    String family = "T3 Textarea Family";
    TextareaElement textarea = textarea("abc");
    textarea.resolvedStyle().fontFamilies(List.of(family));

    assertTrue(metrics.textStyle(textarea).fonts().isEmpty());

    long generation = Font.semanticOwner().generation();
    Font registered =
        new Font(
            family,
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.REGULAR,
            Font.ROBOTO_REGULAR.path());
    Font.semanticOwner()
        .add(
            SemanticFontOwner.FontRequest.from(
                registered,
                () -> ByteBuffer.wrap(registered.path().getBytes(StandardCharsets.UTF_8)),
                bytes -> {},
                (request, bytes) -> {}));

    assertEquals(List.of(registered), metrics.textStyle(textarea).fonts());
    assertEquals(generation + 1, Font.semanticOwner().generation());
  }

  @Test
  void multiParagraphFallbackReplacementAndSeparatorsKeepAbsoluteSourceMappings() {
    String replacementSource = new String(Character.toChars(0x10FFFF));
    TextareaElement textarea = textarea("a\u96EA\r\n" + replacementSource + "\n");

    ControlTextLayoutSnapshot snapshot = metrics.layoutService().query(textarea);

    assertEquals(3, snapshot.paragraphs().size());
    assertEquals("a\u96EA", snapshot.lineForIndex(3).text());
    assertEquals(replacementSource, snapshot.lineForIndex(6).text());
    assertEquals("", snapshot.lineForIndex(7).text());
    assertTrue(
        snapshot.lines().stream()
            .flatMap(line -> line.runs().stream())
            .flatMap(run -> run.glyphs().stream())
            .anyMatch(
                glyph ->
                    glyph.sourceStart() == 1
                        && glyph.sourceEnd() == 2
                        && glyph.font().equals(Font.NOTO_SANS_CJK_SC_REGULAR)));
    assertTrue(
        snapshot.lines().stream()
            .flatMap(line -> line.runs().stream())
            .flatMap(run -> run.glyphs().stream())
            .anyMatch(
                glyph ->
                    glyph.sourceStart() == 4
                        && glyph.sourceEnd() == 6
                        && glyph.replacement()
                        && glyph.renderedCodePoint() == 0xFFFD));
  }

  private static FontServiceImpl installedService() {
    FontServiceImpl service = new FontServiceImpl(new FontStorageImpl(), false);
    service.installSemanticOwner();
    return service;
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
