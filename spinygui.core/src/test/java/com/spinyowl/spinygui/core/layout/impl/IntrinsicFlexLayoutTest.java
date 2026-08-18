package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Test;

class IntrinsicFlexLayoutTest {

  @Test
  void layout_whenNestedFlexRowHasAutoHeight_followingSiblingStartsAfterItsChildren() {
    Frame frame = NodeBuilder.frame();
    Element panel = NodeBuilder.div();
    Element metrics = NodeBuilder.div();
    Element card = NodeBuilder.div();
    Element footer = NodeBuilder.div();

    frame.addChild(panel);
    panel.addChildren(metrics, footer);
    metrics.addChild(card);

    style(frame);
    style(panel);
    style(metrics);
    style(card);
    style(footer);

    frame.frameSize(640, 480);

    panel.resolvedStyle().display(Display.FLEX);
    panel.resolvedStyle().flexDirection(FlexDirection.COLUMN);
    panel.resolvedStyle().width(Length.pixel(580));
    panel.resolvedStyle().height(Length.pixel(300));

    metrics.resolvedStyle().display(Display.FLEX);
    metrics.resolvedStyle().flexDirection(FlexDirection.ROW);
    metrics.resolvedStyle().width(Length.pixel(500));
    metrics.resolvedStyle().height(Unit.AUTO);

    card.resolvedStyle().width(Length.pixel(150));
    card.resolvedStyle().height(Length.pixel(112));

    footer.resolvedStyle().width(Length.pixel(500));
    footer.resolvedStyle().height(Length.pixel(22));

    layoutService().layout(frame);

    float metricsBottom = metrics.box().borderBox().y() + metrics.box().borderBox().height();
    assertTrue(metrics.box().borderBox().height() >= 112f);
    assertTrue(footer.box().borderBox().y() >= metricsBottom);
  }

  private static LayoutService layoutService() {
    return LayoutServiceProvider.create(
        mock(SystemEventProcessor.class),
        mock(EventProcessor.class),
        mock(TimeService.class),
        mock(FontService.class),
        mock(TextMeasurer.class));
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
    style.borderTopWidth(Length.pixel(0));
    style.borderRightWidth(Length.pixel(0));
    style.borderBottomWidth(Length.pixel(0));
    style.borderLeftWidth(Length.pixel(0));
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
  }
}
