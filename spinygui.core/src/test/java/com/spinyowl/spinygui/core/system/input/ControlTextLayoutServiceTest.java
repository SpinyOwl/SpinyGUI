package com.spinyowl.spinygui.core.system.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.FontTestOwner;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerCapability;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ControlTextLayoutServiceTest {

  @AfterEach
  void closeSemanticOwner() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
  }

  @Test
  void query_reusesWarmSnapshotAndReplacesOnlyOnKeyMutation() {
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutService service = new ControlTextLayoutService(measurer);
    InputElement input = input("abc");

    ControlTextLayoutSnapshot first = service.query(input);
    input.caretIndex(2);
    input.textScrollLeft(10);
    input.focused(true);
    input.box().contentPosition(40, 50);
    assertSame(first, service.query(input));
    assertEquals(1, measurer.builds);

    input.value("abcd");
    ControlTextLayoutSnapshot second = service.query(input);
    assertNotSame(first, second);
    assertSame(second, service.query(input));
    assertEquals(2, measurer.builds);

    input.resolvedStyle().fontSize(Length.pixel(18));
    ControlTextLayoutSnapshot third = service.query(input);
    assertNotSame(second, third);
    assertEquals(3, measurer.builds);

    input.resolvedStyle().lineHeight(1.25f);
    ControlTextLayoutSnapshot fourth = service.query(input);
    assertNotSame(third, fourth);
    input.resolvedStyle().fontStyle(FontStyle.ITALIC);
    ControlTextLayoutSnapshot fifth = service.query(input);
    assertNotSame(fourth, fifth);
    input.resolvedStyle().fontWeight(FontWeight.BOLD);
    assertNotSame(fifth, service.query(input));
    assertEquals(6, measurer.builds);
  }

  @Test
  void textareaWidthIsAKeyButContentHeightAndScrollAreNot() {
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutService service = new ControlTextLayoutService(measurer);
    TextareaElement textarea = new TextareaElement("abc");
    textarea.box().contentSize(100, 40);

    ControlTextLayoutSnapshot first = service.query(textarea);
    textarea.box().contentSize(100, 80);
    textarea.textScrollLeft(7);
    textarea.textScrollTop(9);
    assertSame(first, service.query(textarea));

    textarea.box().content().width(90);
    assertNotSame(first, service.query(textarea));
    assertEquals(2, measurer.builds);
  }

  @Test
  void populatedSlotDoesNotAffectGeneratedNodeValueMethodsAndCanBeCleared() {
    ControlTextLayoutService service = new ControlTextLayoutService(new CountingMeasurer());
    InputElement populated = input("same");
    int hash = populated.hashCode();
    String text = populated.toString();

    service.query(populated);

    assertEquals(hash, populated.hashCode());
    assertEquals(text, populated.toString());
    populated.clearTextLayoutSnapshot();
    assertNull(populated.currentTextLayoutSnapshot());
  }

  @Test
  void snapshotCaretAndHitTestUseImmutableAbsoluteUtf16Boundaries() {
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutSnapshot snapshot =
        new ControlTextLayoutService(measurer).query(input("a\uD83D\uDE00b"));

    assertEquals(1, snapshot.caret(2, measurer.diagnostics()).index());
    assertEquals(4, snapshot.caretAt(25, 0, measurer.diagnostics()).index());
    assertEquals(4, snapshot.indexAt(35, 0, measurer.diagnostics()));
    assertEquals(1, measurer.builds);
  }

  @Test
  void invalidatingInputAndTextareaEditsBuildExactlyOnceThenReturnToWarmReuse() {
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutService service = new ControlTextLayoutService(measurer);
    InputElement input = input("ab");
    TextareaElement textarea = new TextareaElement("ab");
    textarea.box().contentSize(100, 40);
    service.query(input);
    service.query(textarea);

    new TextInputBehavior().insertPrintable(input, 'x');
    new TextareaBehavior().insertPrintable(textarea, 'x');
    ControlTextLayoutSnapshot inputReplacement = service.query(input);
    ControlTextLayoutSnapshot textareaReplacement = service.query(textarea);
    assertSame(inputReplacement, service.query(input));
    assertSame(textareaReplacement, service.query(textarea));
    assertEquals(4, measurer.builds);
  }

  @Test
  void snapshotAndNestedKeyAndRunCollectionsAreUnmodifiable() {
    ControlTextLayoutSnapshot snapshot =
        new ControlTextLayoutService(new CountingMeasurer()).query(input("abc"));

    assertThrows(UnsupportedOperationException.class, () -> snapshot.lines().clear());
    assertThrows(UnsupportedOperationException.class, () -> snapshot.paragraphs().clear());
    assertThrows(UnsupportedOperationException.class, () -> snapshot.key().fontFamilies().add("x"));
    assertThrows(UnsupportedOperationException.class, () -> snapshot.key().resolvedFonts().clear());
    assertThrows(UnsupportedOperationException.class, () -> snapshot.lines().get(0).runs().clear());
  }

  @Test
  void changingTheMeasurementServiceContextReplacesTheControlSlot() {
    InputElement input = input("abc");
    ControlTextLayoutSnapshot first =
        new ControlTextLayoutService(new CountingMeasurer()).query(input);

    ControlTextLayoutSnapshot second =
        new ControlTextLayoutService(new CountingMeasurer()).query(input);

    assertNotSame(first, second);
    assertSame(second, input.currentTextLayoutSnapshot());
  }

  @Test
  void serviceWrappersAroundTheSameMeasurementContextShareTheControlSlot() {
    InputElement input = input("abc");
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutSnapshot first = new ControlTextLayoutService(measurer).query(input);

    ControlTextLayoutSnapshot second = new ControlTextLayoutService(measurer).query(input);

    assertSame(first, second);
    assertEquals(1, measurer.builds);
  }

  @Test
  void separatorIndicesBelongToThePrecedingLineAcrossCrLfCrLfAndTrailingSeparators() {
    String value = "a\r\nb\nc\rd\r\n";
    ControlTextLayoutSnapshot snapshot = snapshotWithLines(value);

    assertEquals("a", snapshot.lineForIndex(1).text());
    assertEquals("a", snapshot.lineForIndex(2).text());
    assertEquals("b", snapshot.lineForIndex(4).text());
    assertEquals("c", snapshot.lineForIndex(6).text());
    assertEquals("d", snapshot.lineForIndex(8).text());
    assertEquals("d", snapshot.lineForIndex(9).text());
    assertEquals("", snapshot.lineForIndex(10).text());
    assertEquals(
        List.of(
            new ControlTextLayoutSnapshot.Paragraph(0, 1, 0, 1),
            new ControlTextLayoutSnapshot.Paragraph(3, 4, 1, 1),
            new ControlTextLayoutSnapshot.Paragraph(5, 6, 2, 1),
            new ControlTextLayoutSnapshot.Paragraph(7, 8, 3, 1),
            new ControlTextLayoutSnapshot.Paragraph(10, 10, 4, 1)),
        snapshot.paragraphs());
  }

  @Test
  void completeKeyMatrixReplacesOnceForFamiliesTypographyGenerationWidthAndContext() {
    FontTestOwner.install();
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutService service = new ControlTextLayoutService(measurer);
    TextareaElement textarea = new TextareaElement("abc");
    textarea.box().contentSize(100, 40);
    textarea.resolvedStyle().fontFamilies(
        List.of(Font.ROBOTO_REGULAR.fontFamily(), Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily()));
    textarea.resolvedStyle().fontSize(Length.pixel(16));
    textarea.resolvedStyle().lineHeight(1f);

    ControlTextLayoutSnapshot current = service.query(textarea);
    assertEquals(com.spinyowl.spinygui.core.font.FontStretch.NORMAL, current.key().fontStretch());
    assertFalse(current.key().wordWrap());

    textarea.resolvedStyle().fontFamilies(
        List.of(Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily(), Font.ROBOTO_REGULAR.fontFamily()));
    current = assertOneReplacement(service, textarea, current, measurer, 2);
    textarea.resolvedStyle().fontStyle(FontStyle.ITALIC);
    current = assertOneReplacement(service, textarea, current, measurer, 3);
    textarea.resolvedStyle().fontWeight(FontWeight.BOLD);
    current = assertOneReplacement(service, textarea, current, measurer, 4);
    textarea.resolvedStyle().fontSize(Length.pixel(18));
    current = assertOneReplacement(service, textarea, current, measurer, 5);
    textarea.resolvedStyle().lineHeight(1.25f);
    current = assertOneReplacement(service, textarea, current, measurer, 6);
    textarea.box().content().width(90);
    current = assertOneReplacement(service, textarea, current, measurer, 7);

    ArrayList<String> directFamilies =
        new ArrayList<>(List.of(Font.ROBOTO_REGULAR.fontFamily()));
    textarea.resolvedStyle().styles().put(
        com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_FAMILY, directFamilies);
    current = assertOneReplacement(service, textarea, current, measurer, 8);
    directFamilies.add(Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily());
    current = assertOneReplacement(service, textarea, current, measurer, 9);

    Font registered =
        new Font(
            "M5 matrix family",
            FontStyle.NORMAL,
            com.spinyowl.spinygui.core.font.FontStretch.NORMAL,
            FontWeight.REGULAR,
            "fonts/m5-matrix.ttf");
    long generation = Font.semanticOwner().generation();
    Font.semanticOwner()
        .add(
            SemanticFontOwner.FontRequest.from(
                registered,
                () -> ByteBuffer.wrap(registered.path().getBytes(StandardCharsets.UTF_8)),
                bytes -> {},
                (request, bytes) -> {}));
    assertEquals(generation + 1, Font.semanticOwner().generation());
    current = assertOneReplacement(service, textarea, current, measurer, 10);

    ControlTextLayoutSnapshot otherContext =
        new ControlTextLayoutService(new CountingMeasurer()).query(textarea);
    assertNotSame(current, otherContext);
  }

  @Test
  void inputCompleteKeyMatrixReplacesOnceForFamiliesTypographyGenerationAndContext() {
    FontTestOwner.install();
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutService service = new ControlTextLayoutService(measurer);
    InputElement input = input("abc");
    input.resolvedStyle().fontFamilies(
        List.of(Font.ROBOTO_REGULAR.fontFamily(), Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily()));

    ControlTextLayoutSnapshot current = service.query(input);
    assertEquals(com.spinyowl.spinygui.core.font.FontStretch.NORMAL, current.key().fontStretch());
    assertEquals(Float.MAX_VALUE, current.key().maxWidth());
    assertFalse(current.key().wordWrap());

    input.resolvedStyle().fontFamilies(
        List.of(Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily(), Font.ROBOTO_REGULAR.fontFamily()));
    current = assertOneReplacement(service, input, current, measurer, 2);
    input.resolvedStyle().fontStyle(FontStyle.ITALIC);
    current = assertOneReplacement(service, input, current, measurer, 3);
    input.resolvedStyle().fontWeight(FontWeight.BOLD);
    current = assertOneReplacement(service, input, current, measurer, 4);
    input.resolvedStyle().fontSize(Length.pixel(18));
    current = assertOneReplacement(service, input, current, measurer, 5);
    input.resolvedStyle().lineHeight(1.25f);
    current = assertOneReplacement(service, input, current, measurer, 6);

    ArrayList<String> directFamilies =
        new ArrayList<>(List.of(Font.ROBOTO_REGULAR.fontFamily()));
    input.resolvedStyle().styles().put(
        com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_FAMILY, directFamilies);
    current = assertOneReplacement(service, input, current, measurer, 7);
    directFamilies.add(Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily());
    current = assertOneReplacement(service, input, current, measurer, 8);

    Font registered =
        new Font(
            "M5 input matrix family",
            FontStyle.NORMAL,
            com.spinyowl.spinygui.core.font.FontStretch.NORMAL,
            FontWeight.REGULAR,
            "fonts/m5-input-matrix.ttf");
    long generation = Font.semanticOwner().generation();
    Font.semanticOwner()
        .add(
            SemanticFontOwner.FontRequest.from(
                registered,
                () -> ByteBuffer.wrap(registered.path().getBytes(StandardCharsets.UTF_8)),
                bytes -> {},
                (request, bytes) -> {}));
    assertEquals(generation + 1, Font.semanticOwner().generation());
    current = assertOneReplacement(service, input, current, measurer, 9);

    ControlTextLayoutSnapshot otherContext =
        new ControlTextLayoutService(new CountingMeasurer()).query(input);
    assertNotSame(current, otherContext);
  }

  @Test
  void aggregateNonKeyAndWarmConsumerMatrixReusesSlotsWithoutAnyMeasurerEntrypoint() {
    CountingMeasurer measurer = new CountingMeasurer();
    ControlTextLayoutService service = new ControlTextLayoutService(measurer);
    InputElement input = input("abcdef");
    input.box().contentSize(60, 20);
    TextareaElement textarea = new TextareaElement("ab\ncd");
    textarea.box().contentSize(100, 40);
    Element ancestor = new Element("ancestor");
    input.offsetParent(ancestor);
    textarea.offsetParent(ancestor);
    ControlTextLayoutSnapshot inputSnapshot = service.query(input);
    ControlTextLayoutSnapshot textareaSnapshot = service.query(textarea);

    input.resolvedStyle().color(Color.BLUE);
    input.focused(true);
    input.select(1, 4);
    input.textScrollLeft(8);
    input.box().contentPosition(30, 40);
    textarea.resolvedStyle().color(Color.BLUE);
    textarea.focused(true);
    textarea.select(0, 3);
    textarea.textScrollLeft(4);
    textarea.textScrollTop(6);
    textarea.box().contentSize(100, 80);
    ancestor.scrollTop(9);
    input.presentationState().setValue(
        com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM,
        new Transform.Translate(Length.pixel(2), Length.pixel(3)));

    assertSame(inputSnapshot, service.query(input));
    assertSame(textareaSnapshot, service.query(textarea));
    inputSnapshot.caret(3, service.diagnostics());
    inputSnapshot.indexAt(25, 0, service.diagnostics());
    MultilineTextControlMetrics metrics = new MultilineTextControlMetrics(service);
    metrics.lines(textarea);
    metrics.caret(textarea, 1);
    metrics.indexAt(textarea, new Vector2f(5, 5));
    metrics.lineStart(textarea, 1);
    metrics.lineEnd(textarea, 1);
    metrics.verticalCaretIndex(textarea, 1, 1);
    new TextInputMouseCaretBehavior(service).placeCaret(input, new Vector2f(35, 40));
    new TextInputViewportBehavior(service).ensureCaretVisible(input);
    new TextareaMouseCaretBehavior(metrics).placeCaret(textarea, new Vector2f(5, 5), false);
    new TextareaViewportBehavior(metrics).ensureCaretVisible(textarea);

    assertEquals(2, measurer.builds);
  }

  private ControlTextLayoutSnapshot assertOneReplacement(
      ControlTextLayoutService service,
      TextareaElement textarea,
      ControlTextLayoutSnapshot previous,
      CountingMeasurer measurer,
      int expectedBuilds) {
    ControlTextLayoutSnapshot replacement = service.query(textarea);
    assertNotSame(previous, replacement);
    assertSame(replacement, service.query(textarea));
    assertEquals(expectedBuilds, measurer.builds);
    return replacement;
  }

  private ControlTextLayoutSnapshot assertOneReplacement(
      ControlTextLayoutService service,
      InputElement input,
      ControlTextLayoutSnapshot previous,
      CountingMeasurer measurer,
      int expectedBuilds) {
    ControlTextLayoutSnapshot replacement = service.query(input);
    assertNotSame(previous, replacement);
    assertSame(replacement, service.query(input));
    assertEquals(expectedBuilds, measurer.builds);
    return replacement;
  }

  private ControlTextLayoutSnapshot snapshotWithLines(String value) {
    List<ControlTextLayoutSnapshot.Line> lines =
        List.of(
            line("a", 0, 1, 0),
            line("b", 3, 4, 16),
            line("c", 5, 6, 32),
            line("d", 7, 8, 48),
            line("", 10, 10, 64));
    return new ControlTextLayoutSnapshot(
        new ControlTextLayoutSnapshot.Key(
            value,
            List.of(),
            FontStyle.NORMAL,
            FontWeight.NORMAL,
            com.spinyowl.spinygui.core.font.FontStretch.NORMAL,
            List.of(Font.DEFAULT),
            16,
            1,
            0,
            false,
            new Object(),
            100,
            false),
        lines,
        10,
        80);
  }

  private ControlTextLayoutSnapshot.Line line(String text, int start, int end, float y) {
    int[] boundaries = start == end ? new int[] {start} : new int[] {start, end};
    float[] advances = start == end ? new float[] {0} : new float[] {0, text.length() * 10};
    return new ControlTextLayoutSnapshot.Line(
        text,
        start,
        end,
        text.length() * 10,
        16,
        12,
        List.of(),
        y,
        new FinalLineCaretStops(boundaries, advances));
  }

  private InputElement input(String value) {
    InputElement input = new InputElement();
    input.value(value);
    input.resolvedStyle().fontSize(Length.pixel(16));
    input.resolvedStyle().lineHeight(1f);
    return input;
  }

  private static final class CountingMeasurer
      implements TextMeasurer, RangeTextMeasurerCapability {
    private int builds;

    @Override
    public ResolvedMeasurement measureRange(String source, int start, int end, float offsetX,
        List<Font> fonts, float fontSize, float lineHeight, float maxWidth, boolean wordWrap) {
      builds++;
      int count = source.codePointCount(start, end);
      int[] boundaries = new int[count + 1];
      float[] advances = new float[count + 1];
      int boundary = start;
      boundaries[0] = start;
      for (int index = 1; index <= count; index++) {
        boundary = source.offsetByCodePoints(boundary, 1);
        boundaries[index] = boundary;
        advances[index] = index * 10f;
      }
      FontMetrics fontMetrics = new FontMetrics(12, 4, 0, 16, 12);
      TextLineMetrics line = new TextLineMetrics(source.substring(start, end), start, end, end - start,
          count * 10f, 16, 12, fontMetrics, List.of());
      TextMetrics metrics = new TextMetrics(List.of(line), line.width(), 16, 16, fontMetrics);
      return new ResolvedMeasurement(metrics, List.of(new FinalLineCaretStops(boundaries, advances)));
    }

    @Override public TextMetrics measureText(String text, Font font, float size, float height) {
      throw new UnsupportedOperationException();
    }
    @Override public TextMetrics measureText(String text, float offset, Font font, float size,
        float height, float width, boolean wrap) { throw new UnsupportedOperationException(); }
    @Override public TextMetrics getTextMetrics(String text, float offset, Font font, float size,
        float height, float width, boolean wrap) { throw new UnsupportedOperationException(); }
    @Override public TextLineMetrics getTextLineMetrics(String text, Font font, float size,
        float height) { throw new UnsupportedOperationException(); }
    @Override public TextCaretMetrics getTextCaretMetrics(String text, Font font, float size,
        float offset) { throw new UnsupportedOperationException(); }
  }
}
