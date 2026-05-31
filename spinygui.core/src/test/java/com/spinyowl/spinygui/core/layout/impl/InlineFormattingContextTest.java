package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WORD_WRAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.TextMeasurer;
import com.spinyowl.spinygui.core.node.Element;
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
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InlineFormattingContextTest {
  private InlineFormattingContext formattingContext;
  private Element parent;
  private FixedTextMeasurer textMeasurer;

  @BeforeEach
  void setUp() {
    textMeasurer = new FixedTextMeasurer();
    formattingContext = new InlineFormattingContext(textMeasurer);
    parent = NodeBuilder.div();
    style(parent, Display.BLOCK);
    parent.box().contentSize(50, 0);
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

  private void style(Element element, Display display) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(display);
    style.position(Position.STATIC);
    style.fontFamilies(Set.of("Roboto"));
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

  private static class FixedTextMeasurer implements TextMeasurer {
    private int measurementCount;

    @Override
    public TextLineMetrics measure(
        @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
      measurementCount++;
      FontMetrics fontMetrics = new FontMetrics(8, 2, Math.max(0, fontSize * lineHeight - 10), 10, 8);
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(text.length() * 10f)
          .height(fontMetrics.lineHeight())
          .baseline(fontMetrics.baseline())
          .fontMetrics(fontMetrics)
          .build();
    }
  }
}
