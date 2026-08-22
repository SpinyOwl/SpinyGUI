package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WORD_WRAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.layout.InlineSourceMapping;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.property.TextPropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.system.cache.TextCacheConfiguration;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontTestOwner;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerCapability;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InlineFormattingContextTest {
  private InlineFormattingContext formattingContext;
  private Element parent;
  private FixedTextMeasurer textMeasurer;

  @BeforeEach
  void setUp() {
    FontTestOwner.install();
    textMeasurer = new FixedTextMeasurer();
    formattingContext = new InlineFormattingContext(textMeasurer);
    formattingContext.m7CacheEnabled(true);
    parent = NodeBuilder.div();
    style(parent, Display.BLOCK);
    parent.box().contentSize(50, 0);
  }

  @Test
  void configuredCacheModeInitializesAllInlineFamiliesConsistently() {
    InlineFormattingContext enabled =
        new InlineFormattingContext(new FixedTextMeasurer(), TextCacheConfiguration.boundedDefaults());
    InlineFormattingContext disabled =
        new InlineFormattingContext(new FixedTextMeasurer(), TextCacheConfiguration.disabled());

    assertTrue(enabled.m7CacheEnabled());
    assertFalse(disabled.m7CacheEnabled());
    assertEquals(0, enabled.preparedTextCacheStats().entries());
    assertEquals(0, disabled.preparedTextCacheStats().entries());

    enabled.close();
    disabled.close();
  }

  @Test
  void layout_whenTextFits_placesItOnSingleLine() {
    Text text = NodeBuilder.text("abc");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(10, height);
    assertEquals(1, text.inlineFragments().size());
    assertEquals("abc", text.inlineFragments().get(0).text());
    assertEquals(0, text.inlineFragments().get(0).x());
    assertEquals(30, text.box().content().width());
  }

  @Test
  void layout_whenTextWrapsAtSpace_createsMultipleFragments() {
    Text text = NodeBuilder.text("hello world");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(2, text.inlineFragments().size());
    assertEquals("hello", text.inlineFragments().get(0).text());
    assertEquals("world", text.inlineFragments().get(1).text());
    assertEquals(10, text.inlineFragments().get(1).y());
  }

  @Test
  void layout_whenWrappingAtSpace_trimsTrailingSpaceButKeepsInterWordSpace() {
    Text text = NodeBuilder.text("a b ccc");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(4, text.inlineFragments().size());
    assertEquals("a", text.inlineFragments().get(0).text());
    assertEquals(" ", text.inlineFragments().get(1).text());
    assertEquals(10, text.inlineFragments().get(1).x());
    assertEquals("b", text.inlineFragments().get(2).text());
    assertEquals(20, text.inlineFragments().get(2).x());
    assertEquals("ccc", text.inlineFragments().get(3).text());
    assertEquals(0, text.inlineFragments().get(3).x());
    assertEquals(10, text.inlineFragments().get(3).y());
  }

  @Test
  void layout_whenOverflowWrapBreakWord_breaksLongWord() {
    parent.resolvedStyle().overflowWrap(OverflowWrap.BREAK_WORD);
    Text text = NodeBuilder.text("abcdef");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(6, text.inlineFragments().size());
    assertEquals("a", text.inlineFragments().get(0).text());
    assertEquals("f", text.inlineFragments().get(5).text());
    assertEquals(10, text.inlineFragments().get(5).y());
  }

  @Test
  void layout_whenWordWrapBreakWord_breaksLongWord() {
    applyStyleProperty(parent, WORD_WRAP, "break-word");
    Text text = NodeBuilder.text("abcdef");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(6, text.inlineFragments().size());
    assertEquals("a", text.inlineFragments().get(0).text());
    assertEquals("f", text.inlineFragments().get(5).text());
    assertEquals(10, text.inlineFragments().get(5).y());
  }

  @Test
  void layout_whenWordWrapAnywhere_breaksLongWord() {
    applyStyleProperty(parent, WORD_WRAP, "anywhere");
    Text text = NodeBuilder.text("abcdef");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(6, text.inlineFragments().size());
    assertEquals("a", text.inlineFragments().get(0).text());
    assertEquals("f", text.inlineFragments().get(5).text());
    assertEquals(10, text.inlineFragments().get(5).y());
  }

  @Test
  void layout_whenWordBreakBreakWord_breaksLongWord() {
    parent.resolvedStyle().wordBreak(WordBreak.BREAK_WORD);
    Text text = NodeBuilder.text("abcdef");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(6, text.inlineFragments().size());
    assertEquals("a", text.inlineFragments().get(0).text());
    assertEquals("f", text.inlineFragments().get(5).text());
    assertEquals(10, text.inlineFragments().get(5).y());
  }

  @Test
  void layout_whenWordBreakBreakAll_usesRemainingLineSpace() {
    Text text = NodeBuilder.text("abc def");
    parent.resolvedStyle().wordBreak(WordBreak.BREAK_ALL);
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals("abc", text.inlineFragments().get(0).text());
    assertEquals(" ", text.inlineFragments().get(1).text());
    assertEquals("d", text.inlineFragments().get(2).text());
    assertEquals(40, text.inlineFragments().get(2).x());
    assertEquals("e", text.inlineFragments().get(3).text());
    assertEquals(10, text.inlineFragments().get(3).y());
  }

  @Test
  void layout_whenWordBreakNormal_preservesSpaceBasedWrapping() {
    Text text = NodeBuilder.text("abc def");
    parent.resolvedStyle().wordBreak(WordBreak.NORMAL);
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(2, text.inlineFragments().size());
    assertEquals("abc", text.inlineFragments().get(0).text());
    assertEquals("def", text.inlineFragments().get(1).text());
    assertEquals(0, text.inlineFragments().get(1).x());
  }

  @Test
  void layout_whenWordBreakKeepAll_behavesLikeNormal() {
    Text text = NodeBuilder.text("abc def");
    parent.resolvedStyle().wordBreak(WordBreak.KEEP_ALL);
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(2, text.inlineFragments().size());
    assertEquals("abc", text.inlineFragments().get(0).text());
    assertEquals("def", text.inlineFragments().get(1).text());
    assertEquals(0, text.inlineFragments().get(1).x());
  }

  @Test
  void layout_whenWhiteSpaceNowrap_ignoresWordBreakBreakAll() {
    Text text = NodeBuilder.text("abc def");
    parent.resolvedStyle().whiteSpace(WhiteSpace.NOWRAP);
    parent.resolvedStyle().wordBreak(WordBreak.BREAK_ALL);
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(10, height);
    assertEquals(3, text.inlineFragments().size());
    assertEquals("abc", text.inlineFragments().get(0).text());
    assertEquals(" ", text.inlineFragments().get(1).text());
    assertEquals("def", text.inlineFragments().get(2).text());
    assertEquals(40, text.inlineFragments().get(2).x());
  }

  @Test
  void layout_whenConsecutiveTextNodes_shareLineBox() {
    parent.box().content().width(200);
    Text first = NodeBuilder.text("Hello");
    Text second = NodeBuilder.text(" world");
    parent.addChildren(first, second);

    formattingContext.layout(parent, List.of(first, second), 0);

    assertEquals(0, first.inlineFragments().get(0).y());
    assertEquals(0, second.inlineFragments().get(0).y());
    assertEquals(" ", second.inlineFragments().get(0).text());
    assertEquals(50, second.inlineFragments().get(0).x());
    assertEquals("world", second.inlineFragments().get(1).text());
    assertEquals(60, second.inlineFragments().get(1).x());
  }

  @Test
  void layout_whenLabelContainsSpace_advancesSecondWordBySpaceWidth() {
    parent.box().content().width(200);
    Text text = NodeBuilder.text("Horizontal auto");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    assertEquals("Horizontal", text.inlineFragments().get(0).text());
    assertEquals(" ", text.inlineFragments().get(1).text());
    assertEquals("auto", text.inlineFragments().get(2).text());
    assertEquals(110, text.inlineFragments().get(2).x());
  }

  @Test
  void layout_whenParsedBoundarySpacesSurroundInlineElement_advancesAcrossElement() {
    parent.box().content().width(300);
    Text left = NodeBuilder.text("Hello ");
    Element inline = new Element("span");
    style(inline, Display.INLINE);
    Text middle = NodeBuilder.text("wide");
    Text right = NodeBuilder.text(" world");
    inline.addChild(middle);
    parent.addChildren(left, inline, right);

    formattingContext.layout(parent, List.of(left, inline, right), 0);

    assertEquals(" ", left.inlineFragments().get(1).text());
    assertEquals(60, middle.inlineFragments().get(0).x());
    assertEquals(" ", right.inlineFragments().get(0).text());
    assertEquals(100, right.inlineFragments().get(0).x());
    assertEquals("world", right.inlineFragments().get(1).text());
    assertEquals(110, right.inlineFragments().get(1).x());
  }

  @Test
  void layout_whenInlineElementBetweenText_participatesInLine() {
    parent.box().content().width(200);
    Text left = NodeBuilder.text("a");
    Element inline = new Element("span");
    style(inline, Display.INLINE);
    Text middle = NodeBuilder.text("b");
    inline.addChild(middle);
    Text right = NodeBuilder.text("c");
    parent.addChildren(left, inline, right);

    formattingContext.layout(parent, List.of(left, inline, right), 0);

    assertEquals(10, middle.inlineFragments().get(0).x());
    assertEquals(20, right.inlineFragments().get(0).x());
    assertEquals(10, inline.box().content().width());
    assertEquals(1, inline.inlineFragments().size());
    assertEquals(10, inline.inlineFragments().get(0).x());
  }

  @Test
  void layout_whenInlineBlockElementBetweenText_participatesAsAtomicBox() {
    parent.box().content().width(200);
    Text left = NodeBuilder.text("a");
    Element inlineBlock = new Element("span");
    style(inlineBlock, Display.INLINE_BLOCK);
    Text childText = NodeBuilder.text("ignored-by-parent-flow");
    inlineBlock.addChild(childText);
    Text right = NodeBuilder.text("b");
    parent.addChildren(left, inlineBlock, right);
    formattingContext.inlineBlockLayout(
        (element, formattingParent) -> element.box().contentSize(20, 10));

    formattingContext.layout(parent, List.of(left, inlineBlock, right), 0);

    assertEquals(10, inlineBlock.inlineFragments().get(0).x());
    assertEquals(20, inlineBlock.inlineFragments().get(0).width());
    assertEquals(30, right.inlineFragments().get(0).x());
    assertEquals(0, childText.inlineFragments().size());
  }

  @Test
  void layout_whenInlineBlockHasInternalTextBaseline_alignsSurroundingTextToThatBaseline() {
    parent.box().content().width(200);
    Text left = NodeBuilder.text("before");
    Element inlineBlock = new Element("span");
    style(inlineBlock, Display.INLINE_BLOCK);
    inlineBlock.resolvedStyle().paddingTop(Length.pixel(4));
    inlineBlock.resolvedStyle().paddingRight(Length.pixel(8));
    inlineBlock.resolvedStyle().paddingBottom(Length.pixel(4));
    inlineBlock.resolvedStyle().paddingLeft(Length.pixel(8));
    inlineBlock.resolvedStyle().borderTopWidth(Length.pixel(3));
    inlineBlock.resolvedStyle().borderRightWidth(Length.pixel(3));
    inlineBlock.resolvedStyle().borderBottomWidth(Length.pixel(3));
    inlineBlock.resolvedStyle().borderLeftWidth(Length.pixel(3));
    inlineBlock.resolvedStyle().borderTopStyle(BorderStyle.SOLID);
    inlineBlock.resolvedStyle().borderRightStyle(BorderStyle.SOLID);
    inlineBlock.resolvedStyle().borderBottomStyle(BorderStyle.SOLID);
    inlineBlock.resolvedStyle().borderLeftStyle(BorderStyle.SOLID);
    Text inner = NodeBuilder.text("chip");
    inlineBlock.addChild(inner);
    Text right = NodeBuilder.text("after");
    parent.addChildren(left, inlineBlock, right);
    formattingContext.inlineBlockLayout(
        (element, formattingParent) -> {
          inner.inlineFragments(
              List.of(
                  InlineFragment.builder()
                      .node(inner)
                      .text("chip")
                      .x(0)
                      .y(0)
                      .width(40)
                      .height(10)
                      .baseline(8)
                      .font(Font.DEFAULT)
                      .fontSize(10)
                      .color(Color.BLACK)
                      .build()));
          element.box().contentSize(40, 10);
        });

    formattingContext.layout(parent, List.of(left, inlineBlock, right), 0);

    assertEquals(0, inlineBlock.inlineFragments().get(0).y());
    assertEquals(7, left.inlineFragments().get(0).y());
    assertEquals(7, right.inlineFragments().get(0).y());
  }

  @Test
  void layout_whenInlineElementWraps_createsElementFragmentForEachLine() {
    parent.box().content().width(25);
    Element inline = new Element("span");
    style(inline, Display.INLINE);
    inline.resolvedStyle().overflowWrap(OverflowWrap.BREAK_WORD);
    Text text = NodeBuilder.text("abcd");
    inline.addChild(text);
    parent.addChild(inline);

    formattingContext.layout(parent, List.of(inline), 0);

    assertTrue(inline.inlineFragments().size() > 1);
    assertEquals(0, inline.inlineFragments().get(0).y());
    assertEquals(10, inline.inlineFragments().get(1).y());
  }

  @Test
  void layout_whenNestedInlineHasOwnColor_usesNestedStyleForTextFragment() {
    parent.box().content().width(200);
    Element inline = new Element("span");
    style(inline, Display.INLINE);
    inline.resolvedStyle().color(Color.RED);
    Text text = NodeBuilder.text("red");
    inline.addChild(text);
    parent.addChild(inline);

    formattingContext.layout(parent, List.of(inline), 0);

    assertEquals(Color.RED, text.inlineFragments().get(0).color());
  }

  @Test
  void layout_whenInlineElementHasMarginPaddingAndBorder_advancesCursorButExcludesMarginFragment() {
    parent.box().content().width(200);
    Element inline = new Element("span");
    style(inline, Display.INLINE);
    inline.resolvedStyle().marginLeft(Length.pixel(5));
    inline.resolvedStyle().marginRight(Length.pixel(7));
    inline.resolvedStyle().paddingLeft(Length.pixel(3));
    inline.resolvedStyle().paddingRight(Length.pixel(4));
    inline.resolvedStyle().borderLeftWidth(Length.pixel(2));
    inline.resolvedStyle().borderRightWidth(Length.pixel(2));
    inline.resolvedStyle().borderTopStyle(BorderStyle.SOLID);
    inline.resolvedStyle().borderRightStyle(BorderStyle.SOLID);
    inline.resolvedStyle().borderBottomStyle(BorderStyle.SOLID);
    inline.resolvedStyle().borderLeftStyle(BorderStyle.SOLID);
    Text text = NodeBuilder.text("a");
    Text right = NodeBuilder.text("b");
    inline.addChild(text);
    parent.addChildren(inline, right);

    formattingContext.layout(parent, List.of(inline, right), 0);

    assertEquals(10, text.inlineFragments().get(0).x());
    assertEquals(33, right.inlineFragments().get(0).x());
    assertEquals(5, inline.inlineFragments().get(0).x());
    assertEquals(21, inline.inlineFragments().get(0).width());
    assertEquals(21, inline.box().content().width());
  }

  @Test
  void layout_whenPreLine_preservesNewlines() {
    parent.resolvedStyle().whiteSpace(WhiteSpace.PRE_LINE);
    Text text = NodeBuilder.text("a\nb");
    parent.addChild(text);

    float height = formattingContext.layout(parent, List.of(text), 0);

    assertEquals(20, height);
    assertEquals(2, text.inlineFragments().size());
    assertEquals(10, text.inlineFragments().get(1).y());
  }

  @Test
  void layout_whenPreWithTab_expandsTabUsingTabSize() {
    parent.resolvedStyle().whiteSpace(WhiteSpace.PRE);
    parent.resolvedStyle().tabSize(4);
    parent.box().content().width(200);
    Text text = NodeBuilder.text("a\tb");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    assertEquals(6, text.inlineFragments().size());
    assertEquals("b", text.inlineFragments().get(5).text());
    assertEquals(50, text.inlineFragments().get(5).x());
  }

  @Test
  void layout_whenTextAlignCenter_offsetsLineFragments() {
    parent.box().content().width(100);
    parent.resolvedStyle().textAlign(TextAlign.CENTER);
    Text text = NodeBuilder.text("abc");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    assertEquals(35, text.inlineFragments().get(0).x());
  }

  @Test
  void layout_whenTextBecomesEmpty_clearsPreviousBox() {
    Text text = NodeBuilder.text("abc");
    parent.addChild(text);
    formattingContext.layout(parent, List.of(text), 0);
    assertEquals(30, text.box().content().width());

    text.content(" ");
    formattingContext.layout(parent, List.of(text), 0);

    assertEquals(0, text.inlineFragments().size());
    assertEquals(0, text.box().content().width());
    assertEquals(0, text.box().content().height());
  }

  @Test
  void layout_whenInlineElementHasPercentPadding_usesParentContentAsBase() {
    parent.box().content().width(100);
    Element inline = new Element("span");
    style(inline, Display.INLINE);
    inline.resolvedStyle().paddingLeft(Length.percent(0.1f));
    inline.resolvedStyle().paddingRight(Length.percent(0.1f));
    Text text = NodeBuilder.text("a");
    inline.addChild(text);
    parent.addChild(inline);

    formattingContext.layout(parent, List.of(inline), 0);

    assertEquals(10, text.inlineFragments().get(0).x());
    assertEquals(30, inline.box().content().width());
    assertEquals(30, inline.inlineFragments().get(0).width());
  }

  @Test
  void layout_whenTextAlignRight_appliesExpectedOffset() {
    parent.box().content().width(100);
    parent.resolvedStyle().textAlign(TextAlign.RIGHT);
    Text text = NodeBuilder.text("abc");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    assertEquals(70, text.inlineFragments().get(0).x());
    assertEquals(30, text.box().content().width());
  }

  @Test
  void layout_whenUnitIsMeasured_usesSingleUnifiedMeasurementResult() {
    parent.box().content().width(100);
    Text text = NodeBuilder.text("abc");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    assertEquals(1, textMeasurer.measurementCount);
  }

  @Test
  void layout_reusesPreparedValuesAcrossPassesAndTypographyWithinThePass() {
    textMeasurer.diagnostics.reset();
    Text first = NodeBuilder.text("same");
    Text second = NodeBuilder.text("same");
    parent.addChildren(first, second);

    formattingContext.layout(parent, List.of(first, second), 0);

    var snapshot = textMeasurer.diagnostics.snapshot();
    assertEquals(1, snapshot.value(TextDiagnosticCounter.NORMALIZATION_SCANS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.INLINE_MEASUREMENT_RANGE_CALLS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.INLINE_MEASUREMENT_REUSES));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.INLINE_DURABLE_TEXT_STRINGS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.INLINE_PASS_CLEANUPS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS));

    formattingContext.layout(parent, List.of(first, second), 0);
    assertEquals(1, textMeasurer.diagnostics.snapshot().value(TextDiagnosticCounter.NORMALIZATION_SCANS));
    assertEquals(2, textMeasurer.measurementCount);
  }

  @Test
  void layout_whenBreakAllContainsSupplementaryCodePoint_keepsItAtomicAndOwned() {
    parent.box().content().width(15);
    parent.resolvedStyle().wordBreak(WordBreak.BREAK_ALL);
    Text text = NodeBuilder.text("\uD83D\uDE00b");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    assertEquals(2, text.inlineFragments().size());
    assertEquals("\uD83D\uDE00", text.inlineFragments().get(0).text());
    assertSame(text, text.inlineFragments().get(0).node());
    assertEquals("b", text.inlineFragments().get(1).text());
    assertSame(text, text.inlineFragments().get(1).node());
  }

  @Test
  void layout_preservesTextAndInlineElementOwnershipAtDurableBoundary() {
    textMeasurer.diagnostics.reset();
    Element inline = new Element("span");
    style(inline, Display.INLINE);
    Text text = NodeBuilder.text("a b");
    inline.addChild(text);
    parent.addChild(inline);

    formattingContext.layout(parent, List.of(inline), 0);

    assertEquals(
        List.of("a", " ", "b"),
        text.inlineFragments().stream().map(InlineFragment::text).toList());
    text.inlineFragments().forEach(fragment -> assertSame(text, fragment.node()));
    assertEquals(1, inline.inlineFragments().size());
    assertSame(inline, inline.inlineFragments().get(0).node());
    assertEquals(null, inline.inlineFragments().get(0).text());
    assertFalse(inline.inlineFragments().get(0).sourceMapping().mapped());
    var snapshot = textMeasurer.diagnostics.snapshot();
    assertEquals(3, snapshot.value(TextDiagnosticCounter.INLINE_DURABLE_TEXT_STRINGS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.INLINE_NULL_TEXT_FRAGMENTS));
  }

  @Test
  void layout_whenPreExpandsTab_groupsPreparedRangesButPreservesDurableFragments() {
    textMeasurer.diagnostics.reset();
    parent.resolvedStyle().whiteSpace(WhiteSpace.PRE);
    parent.resolvedStyle().tabSize(4);
    Text text = NodeBuilder.text("a\tb");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    var snapshot = textMeasurer.diagnostics.snapshot();
    assertEquals(3, snapshot.value(TextDiagnosticCounter.INLINE_PREPARED_RANGES));
    assertEquals(6, snapshot.value(TextDiagnosticCounter.INLINE_DURABLE_TEXT_STRINGS));
    assertEquals(6, text.inlineFragments().size());
    assertEquals(6, snapshot.value(TextDiagnosticCounter.INLINE_RANGE_CODE_POINT_VISITS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.INLINE_TEMPORARY_UNITS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS));
    assertEquals(
        List.of(0, 1, 1, 1, 1, 2),
        text.inlineFragments().stream()
            .map(fragment -> fragment.sourceMapping().sourceStart())
            .toList());
    assertEquals(
        List.of(1, 2, 2, 2, 2, 3),
        text.inlineFragments().stream()
            .map(fragment -> fragment.sourceMapping().sourceEnd())
            .toList());
    text.inlineFragments().subList(1, 5).forEach(
        fragment -> {
          assertEquals(0, fragment.runs().getFirst().sourceStart());
          assertEquals(1, fragment.runs().getFirst().sourceEnd());
          assertEquals(0, fragment.runs().getFirst().glyphs().getFirst().sourceStart());
          assertEquals(1, fragment.runs().getFirst().glyphs().getFirst().sourceEnd());
          assertEquals(
              new InlineSourceMapping.SourceSpan(1, 2),
              fragment.sourceMapping().sourceSpanAt(0));
        });
  }

  @Test
  void layout_keepsRenderedOffsetsLocalAndRetainsNonLinearOriginalSourceMapping() {
    parent.box().content().width(200);
    Text text = NodeBuilder.text("a\r\n\t\u96EA\uDBFF\uDFFFb");
    parent.addChild(text);

    formattingContext.layout(parent, List.of(text), 0);

    assertEquals(
        List.of("a", " ", "\u96EA\uDBFF\uDFFFb"),
        text.inlineFragments().stream().map(InlineFragment::text).toList());
    InlineFragment collapsed = text.inlineFragments().get(1);
    assertEquals(1, collapsed.sourceMapping().sourceStart());
    assertEquals(4, collapsed.sourceMapping().sourceEnd());
    assertEquals(0, collapsed.runs().getFirst().sourceStart());
    assertEquals(1, collapsed.runs().getFirst().sourceEnd());
    assertEquals(0, collapsed.runs().getFirst().glyphs().getFirst().sourceStart());
    assertEquals(1, collapsed.runs().getFirst().glyphs().getFirst().sourceEnd());

    InlineFragment suffix = text.inlineFragments().get(2);
    assertEquals(4, suffix.sourceMapping().sourceStart());
    assertEquals(8, suffix.sourceMapping().sourceEnd());
    assertEquals(
        List.of("0-1", "1-2", "2-3"),
        suffix.runs().stream()
            .map(run -> run.sourceStart() + "-" + run.sourceEnd())
            .toList());
    assertEquals(
        List.of("0-1", "1-2", "2-3"),
        suffix.runs().stream()
            .flatMap(run -> run.glyphs().stream())
            .map(glyph -> glyph.sourceStart() + "-" + glyph.sourceEnd())
            .toList());
    ResolvedGlyph replacement = suffix.runs().get(1).glyphs().getFirst();
    assertTrue(replacement.replacement());
    assertEquals(0x10FFFF, replacement.sourceCodePoint());
    assertEquals(0xFFFD, replacement.renderedCodePoint());
    assertEquals(3, suffix.sourceMapping().fragmentLength());
    assertEquals(4, suffix.sourceMapping().sourceSpanAt(0).start());
    assertEquals(5, suffix.sourceMapping().sourceSpanAt(0).end());
    assertEquals(5, suffix.sourceMapping().sourceSpanAt(1).start());
    assertEquals(7, suffix.sourceMapping().sourceSpanAt(1).end());
    assertEquals(7, suffix.sourceMapping().sourceSpanAt(2).start());
    assertEquals(8, suffix.sourceMapping().sourceSpanAt(2).end());
    InlineFragment translated = suffix.translate(5, 7);
    assertSame(suffix.sourceMapping(), translated.sourceMapping());
    assertEquals(suffix.runs(), translated.runs());
  }

  @Test
  void layout_whenMeasurementFails_dropsPassLocalState() {
    FixedTextMeasurer failing =
        new FixedTextMeasurer() {
          @Override
          public ResolvedMeasurement measureRange(
              String source,
              int start,
              int end,
              float offsetX,
              List<Font> fonts,
              float fontSize,
              float lineHeight,
              float maxWidth,
              boolean wordWrap) {
            throw new IllegalStateException("expected measurement failure");
          }
        };
    InlineFormattingContext context = new InlineFormattingContext(failing);
    Text text = NodeBuilder.text("failure");
    parent.addChild(text);

    assertThrows(IllegalStateException.class, () -> context.layout(parent, List.of(text), 0));
    assertEquals(
        1,
        failing.diagnostics.snapshot().value(TextDiagnosticCounter.INLINE_PASS_CLEANUPS));
  }

  @Test
  void layout_changedTypographyValuesDoNotAliasPassLocalEntries() {
    textMeasurer.diagnostics.reset();
    Element normal = new Element("span");
    style(normal, Display.INLINE);
    normal.addChild(NodeBuilder.text("same"));
    Element bold = new Element("strong");
    style(bold, Display.INLINE);
    bold.resolvedStyle().fontWeight(FontWeight.BOLD);
    bold.addChild(NodeBuilder.text("same"));
    parent.addChildren(normal, bold);

    formattingContext.layout(parent, List.of(normal, bold), 0);

    var snapshot = textMeasurer.diagnostics.snapshot();
    assertEquals(2, snapshot.value(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.INLINE_MEASUREMENT_RANGE_CALLS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.INLINE_MEASUREMENT_REUSES));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("scaledInlineScenarios")
  void layout_scaledPreparedRangeCountersRemainLinearAcrossPolicies(
      String name,
      String source,
      WhiteSpace whiteSpace,
      int tabSize,
      WordBreak wordBreak,
      float width) {
    DiagnosticSnapshot one = runScaledScenario(source, whiteSpace, tabSize, wordBreak, width, 1);
    DiagnosticSnapshot eight = runScaledScenario(source, whiteSpace, tabSize, wordBreak, width, 8);

    assertEquals(one.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED),
        eight.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED), name);
    assertEquals(one.value(TextDiagnosticCounter.NORMALIZATION_SCANS),
        eight.value(TextDiagnosticCounter.NORMALIZATION_SCANS), name);
    assertEquals(one.value(TextDiagnosticCounter.INLINE_PREPARED_CODE_POINTS_APPENDED),
        eight.value(TextDiagnosticCounter.INLINE_PREPARED_CODE_POINTS_APPENDED), name);
    assertEquals(one.value(TextDiagnosticCounter.INLINE_PREPARATION_FREEZES),
        eight.value(TextDiagnosticCounter.INLINE_PREPARATION_FREEZES), name);
    assertEquals(one.value(TextDiagnosticCounter.INLINE_PREPARED_RANGES),
        eight.value(TextDiagnosticCounter.INLINE_PREPARED_RANGES), name);
    assertEquals(8 * one.value(TextDiagnosticCounter.INLINE_RANGE_CODE_POINT_VISITS),
        eight.value(TextDiagnosticCounter.INLINE_RANGE_CODE_POINT_VISITS), name);
    assertEquals(8 * one.value(TextDiagnosticCounter.INLINE_DURABLE_TEXT_STRINGS),
        eight.value(TextDiagnosticCounter.INLINE_DURABLE_TEXT_STRINGS), name);
    long oneMeasurements =
        one.value(TextDiagnosticCounter.INLINE_MEASUREMENT_RANGE_CALLS)
            + one.value(TextDiagnosticCounter.INLINE_MEASUREMENT_REUSES);
    long eightMeasurements =
        eight.value(TextDiagnosticCounter.INLINE_MEASUREMENT_RANGE_CALLS)
            + eight.value(TextDiagnosticCounter.INLINE_MEASUREMENT_REUSES);
    long perAdditionalNodeProbe = WordBreak.BREAK_ALL.equals(wordBreak) ? 1 : 0;
    assertEquals(8 * oneMeasurements + 7 * perAdditionalNodeProbe, eightMeasurements, name);
    assertEquals(one.value(TextDiagnosticCounter.INLINE_MEASUREMENT_RANGE_CALLS),
        eight.value(TextDiagnosticCounter.INLINE_MEASUREMENT_RANGE_CALLS), name);
    assertEquals(0, eight.value(TextDiagnosticCounter.INLINE_TEMPORARY_UNITS), name);
    assertEquals(0, eight.value(TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS), name);
    assertEquals(1, eight.value(TextDiagnosticCounter.INLINE_PASS_CLEANUPS), name);
  }

  private DiagnosticSnapshot runScaledScenario(
      String source,
      WhiteSpace whiteSpace,
      int tabSize,
      WordBreak wordBreak,
      float width,
      int nodeCount) {
    FixedTextMeasurer measurer = new FixedTextMeasurer();
    Element scenarioParent = NodeBuilder.div();
    style(scenarioParent, Display.BLOCK);
    scenarioParent.resolvedStyle().whiteSpace(whiteSpace);
    scenarioParent.resolvedStyle().tabSize(tabSize);
    scenarioParent.resolvedStyle().wordBreak(wordBreak);
    scenarioParent.box().contentSize(width, 0);
    List<Node> nodes = new ArrayList<>();
    for (int index = 0; index < nodeCount; index++) {
      Text text = NodeBuilder.text(source);
      scenarioParent.addChild(text);
      nodes.add(text);
    }

    InlineFormattingContext context = new InlineFormattingContext(measurer);
    context.m7CacheEnabled(true);
    context.layout(scenarioParent, nodes, 0);

    return measurer.diagnostics.snapshot();
  }

  private static Stream<Arguments> scaledInlineScenarios() {
    return Stream.of(
        Arguments.of("normal", "abcdef", WhiteSpace.NORMAL, 4, WordBreak.NORMAL, 500f),
        Arguments.of("collapsed", "a \t\f\u000B b", WhiteSpace.NORMAL, 4, WordBreak.NORMAL, 500f),
        Arguments.of("pre", "a b", WhiteSpace.PRE, 4, WordBreak.NORMAL, 500f),
        Arguments.of("tab", "a\tb", WhiteSpace.PRE, 4, WordBreak.NORMAL, 500f),
        Arguments.of("break-all", "abcdef", WhiteSpace.NORMAL, 4, WordBreak.BREAK_ALL, 15f),
        Arguments.of("fallback-replacement", "a\u96EA\uDBFF\uDFFFb", WhiteSpace.NORMAL, 4, WordBreak.NORMAL, 500f));
  }

  private void style(Element element, Display display) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(display);
    style.position(Position.STATIC);
    style.fontFamilies(List.of("Roboto"));
    style.fontStyle(FontStyle.NORMAL);
    style.fontWeight(FontWeight.NORMAL);
    style.fontSize(Length.pixel(10));
    style.lineHeight(1f);
    style.color(Color.BLACK);
    style.whiteSpace(WhiteSpace.NORMAL);
    style.textAlign(TextAlign.LEFT);
    style.overflowWrap(OverflowWrap.NORMAL);
    style.wordBreak(WordBreak.NORMAL);
    style.tabSize(4);
  }

  private void applyStyleProperty(Element element, String name, String value) {
    Property property =
        new TextPropertyProvider().getProperties().stream()
            .filter(candidate -> name.equals(candidate.name()))
            .findFirst()
            .orElseThrow();
    property.apply(element, new TermIdent(value));
  }

  private static class FixedTextMeasurer extends AbstractFixedTextMeasurer
      implements RangeTextMeasurerCapability {
    private int measurementCount;
    private final DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));

    @Override
    public DiagnosticSession diagnostics() {
      return diagnostics;
    }

    @Override
    public ResolvedMeasurement measureRange(
        String source,
        int start,
        int end,
        float offsetX,
        List<Font> fonts,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      TextLineMetrics line = line(source.substring(start, end), start, end, fontSize, lineHeight);
      int[] boundaries = start == end ? new int[] {start} : new int[] {start, end};
      float[] advances = start == end ? new float[] {0} : new float[] {0, line.width()};
      TextMetrics metrics =
          TextMetrics.builder()
              .lines(List.of(line))
              .width(line.width())
              .height(line.height())
              .lineHeight(line.height())
              .fontMetrics(line.fontMetrics())
              .build();
      return new ResolvedMeasurement(
          metrics, List.of(new FinalLineCaretStops(boundaries, advances)));
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
      return line(text, 0, text.length(), fontSize, lineHeight);
    }

    private TextLineMetrics line(
        String text, int startIndex, int endIndex, float fontSize, float lineHeight) {
      measurementCount++;
      FontMetrics fontMetrics =
          new FontMetrics(8, 2, Math.max(0, fontSize * lineHeight - 10), 10, 8);
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(startIndex)
          .endIndex(endIndex)
          .charCount(endIndex - startIndex)
          .width(text.length() * 10f)
          .height(fontMetrics.lineHeight())
          .baseline(fontMetrics.baseline())
          .fontMetrics(fontMetrics)
          .runs(resolvedRuns(text, startIndex))
          .build();
    }

    private List<ResolvedTextRun> resolvedRuns(String text, int origin) {
      List<ResolvedTextRun> runs = new ArrayList<>();
      for (int index = 0; index < text.length(); ) {
        int codePoint = text.codePointAt(index);
        int next = index + Character.charCount(codePoint);
        Font font = codePoint > 0x7F ? Font.NOTO_SANS_CJK_SC_REGULAR : Font.DEFAULT;
        boolean replacement = codePoint == 0x10FFFF;
        ResolvedGlyph glyph =
            new ResolvedGlyph(
                origin + index,
                origin + next,
                codePoint,
                replacement ? 0xFFFD : codePoint,
                font,
                replacement);
        runs.add(
            new ResolvedTextRun(
                glyph.sourceStart(),
                glyph.sourceEnd(),
                font,
                List.of(glyph),
                (next - index) * 10f));
        index = next;
      }
      return List.copyOf(runs);
    }
  }
}
