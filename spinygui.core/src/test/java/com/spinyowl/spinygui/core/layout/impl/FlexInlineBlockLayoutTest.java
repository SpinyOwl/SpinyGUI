package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.List;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class FlexInlineBlockLayoutTest {

  @Test
  void layout_whenInlineBlockIsDirectFlexItem_laysOutItsText() {
    Frame frame = NodeBuilder.frame();
    Element panel = NodeBuilder.div();
    Text titleText = NodeBuilder.text("Level complete");
    Element title = NodeBuilder.div(titleText);
    panel.addChild(title);
    frame.addChild(panel);

    style(frame);
    style(panel);
    style(title);
    frame.frameSize(800, 600);
    panel.resolvedStyle().display(Display.FLEX);
    panel.resolvedStyle().flexDirection(FlexDirection.COLUMN);
    panel.resolvedStyle().alignItems(AlignItems.CENTER);
    panel.resolvedStyle().width(Length.pixel(580));
    panel.resolvedStyle().height(Length.pixel(410));
    title.resolvedStyle().display(Display.INLINE_BLOCK);
    title.resolvedStyle().minHeight(Length.pixel(32));
    title.resolvedStyle().fontSize(Length.pixel(28));

    layoutService(new FixedTextMeasurer()).layout(frame);

    assertFalse(titleText.inlineFragments().isEmpty());
    assertTrue(title.box().borderBox().width() > 0);
    assertTrue(title.box().borderBox().height() >= 32);
  }

  @Test
  void layout_whenInlineBlockButtonIsDirectFlexItem_laysOutItsCaption() {
    Frame frame = NodeBuilder.frame();
    Element actions = NodeBuilder.div();
    Text caption = NodeBuilder.text("Play again");
    ButtonElement button = NodeBuilder.button(caption);
    actions.addChild(button);
    frame.addChild(actions);

    style(frame);
    style(actions);
    style(button);
    frame.frameSize(800, 600);
    actions.resolvedStyle().display(Display.FLEX);
    actions.resolvedStyle().flexDirection(FlexDirection.ROW);
    actions.resolvedStyle().justifyContent(JustifyContent.CENTER);
    actions.resolvedStyle().width(Length.pixel(580));
    actions.resolvedStyle().height(Length.pixel(80));
    button.resolvedStyle().display(Display.INLINE_BLOCK);
    button.resolvedStyle().minWidth(Length.pixel(150));
    button.resolvedStyle().paddingTop(Length.pixel(10));
    button.resolvedStyle().paddingRight(Length.pixel(14));
    button.resolvedStyle().paddingBottom(Length.pixel(10));
    button.resolvedStyle().paddingLeft(Length.pixel(14));

    layoutService(new FixedTextMeasurer()).layout(frame);

    assertFalse(caption.inlineFragments().isEmpty());
    assertTrue(button.box().borderBox().width() >= 150);
    assertTrue(button.box().borderBox().height() > 20);
  }

  private static LayoutService layoutService(TextMeasurer textMeasurer) {
    return LayoutServiceProvider.create(
        mock(SystemEventProcessor.class),
        mock(EventProcessor.class),
        mock(TimeService.class),
        mock(FontService.class),
        textMeasurer);
  }

  private static void style(Element element) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(Display.BLOCK);
    style.position(Position.STATIC);
    style.width(Unit.AUTO);
    style.height(Unit.AUTO);
    style.minWidth(null);
    style.maxWidth(null);
    style.minHeight(null);
    style.maxHeight(null);
    style.top(Unit.AUTO);
    style.right(Unit.AUTO);
    style.bottom(Unit.AUTO);
    style.left(Unit.AUTO);
    style.paddingTop(Length.ZERO);
    style.paddingRight(Length.ZERO);
    style.paddingBottom(Length.ZERO);
    style.paddingLeft(Length.ZERO);
    style.marginTop(Length.ZERO);
    style.marginRight(Length.ZERO);
    style.marginBottom(Length.ZERO);
    style.marginLeft(Length.ZERO);
    style.borderTopWidth(Length.ZERO);
    style.borderRightWidth(Length.ZERO);
    style.borderBottomWidth(Length.ZERO);
    style.borderLeftWidth(Length.ZERO);
    style.borderTopStyle(BorderStyle.NONE);
    style.borderRightStyle(BorderStyle.NONE);
    style.borderBottomStyle(BorderStyle.NONE);
    style.borderLeftStyle(BorderStyle.NONE);
    style.flexBasis(Unit.AUTO);
    style.flexDirection(FlexDirection.ROW);
    style.flexWrap(FlexWrap.NOWRAP);
    style.flexGrow(0);
    style.flexShrink(0);
    style.justifyContent(JustifyContent.FLEX_START);
    style.alignItems(AlignItems.FLEX_START);
    style.alignSelf(AlignSelf.AUTO);
    style.fontFamilies(List.of("Roboto"));
    style.fontStyle(FontStyle.NORMAL);
    style.fontWeight(FontWeight.NORMAL);
    style.fontSize(Length.pixel(16));
    style.lineHeight(1f);
    style.color(Color.WHITE);
    style.whiteSpace(WhiteSpace.NORMAL);
    style.textAlign(TextAlign.LEFT);
    style.overflowWrap(OverflowWrap.NORMAL);
    style.tabSize(4);
  }

  private static final class FixedTextMeasurer extends AbstractFixedTextMeasurer {
    @Override
    public TextLineMetrics getTextLineMetrics(
        @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
      float measuredHeight = Math.max(1f, fontSize * lineHeight);
      FontMetrics fontMetrics =
          new FontMetrics(
              measuredHeight * 0.8f,
              measuredHeight * 0.2f,
              0,
              measuredHeight,
              measuredHeight * 0.8f);
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(text.length() * fontSize * 0.6f)
          .height(measuredHeight)
          .baseline(fontMetrics.baseline())
          .fontMetrics(fontMetrics)
          .build();
    }
  }
}
