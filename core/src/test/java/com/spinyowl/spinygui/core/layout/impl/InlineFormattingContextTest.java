package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.FontMetrics;
import com.spinyowl.spinygui.core.layout.TextMeasurer;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InlineFormattingContextTest {
  private InlineFormattingContext formattingContext;
  private Element parent;

  @BeforeEach
  void setUp() {
    formattingContext = new InlineFormattingContext(new FixedTextMeasurer());
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
  void layout_whenConsecutiveTextNodes_shareLineBox() {
    parent.box().content().width(200);
    Text first = NodeBuilder.text("Hello");
    Text second = NodeBuilder.text(" world");
    parent.addChildren(first, second);

    formattingContext.layout(parent, List.of(first, second), 0);

    assertEquals(0, first.inlineFragments().get(0).y());
    assertEquals(0, second.inlineFragments().get(0).y());
    assertEquals(60, second.inlineFragments().get(0).x());
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
    style.tabSize(4);
  }

  private static class FixedTextMeasurer implements TextMeasurer {
    @Override
    public float measure(@NonNull String text, @NonNull Font font, float fontSize) {
      return text.length() * 10f;
    }

    @Override
    public FontMetrics metrics(@NonNull Font font, float fontSize, float lineHeight) {
      return new FontMetrics(8, 2, Math.max(0, fontSize * lineHeight - 10));
    }
  }
}
