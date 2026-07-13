package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TRANSFORM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.style.types.TransformOrigin;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Test;

class LayoutServiceProviderGridTest {

  @Test
  void layout_resolvesPercentageTransformWithoutChangingGeometry() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 40);
    child.resolvedStyle().transform(
        new Transform.Operations(java.util.List.of(new Transform.Translate(Length.percent(.5f), Length.percent(.5f)))));
    child.resolvedStyle().transformOrigin(TransformOrigin.CENTER);
    frame.addChild(child);

    layoutService().layout(frame);

    assertEquals(100, child.box().borderBoxSize().x);
    assertEquals(40, child.box().borderBoxSize().y);
    assertEquals(50, child.presentationState().transform().tx());
    assertEquals(20, child.presentationState().transform().ty());
  }

  @Test
  void layout_composesPresentedTransformAfterSizingWithoutChangingGeometry() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 40);
    child.resolvedStyle().transform(Transform.NONE);
    child.resolvedStyle().transformOrigin(TransformOrigin.CENTER);
    child.presentationState().setValue(
        TRANSFORM, new Transform.Translate(Length.percent(.5f), Length.percent(.5f)));
    frame.addChild(child);

    layoutService().layout(frame);

    assertEquals(100, child.box().borderBoxSize().x);
    assertEquals(40, child.box().borderBoxSize().y);
    assertEquals(50, child.presentationState().transform().tx());
    assertEquals(20, child.presentationState().transform().ty());
  }

  @Test
  void layout_keepsScrollAndClientMetricsStableWhilePaintValuesChange() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element scrollContainer = NodeBuilder.div();
    style(scrollContainer, Display.BLOCK, 100, 40);
    scrollContainer.resolvedStyle().overflowX(Overflow.AUTO);
    scrollContainer.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 160, 80);
    scrollContainer.addChild(child);
    frame.addChild(scrollContainer);

    LayoutService layoutService = layoutService();
    layoutService.layout(frame);
    float scrollWidth = scrollContainer.scrollWidth();
    float scrollHeight = scrollContainer.scrollHeight();
    float clientWidth = scrollContainer.clientWidth();
    float clientHeight = scrollContainer.clientHeight();
    scrollContainer.presentationState().setValue(BACKGROUND_COLOR, Color.BLUE);
    scrollContainer.presentationState().setValue(OPACITY, 0.5f);
    scrollContainer.presentationState().setValue(
        TRANSFORM, new Transform.Translate(Length.percent(.5f), Length.ZERO));

    layoutService.layout(frame);

    assertEquals(scrollWidth, scrollContainer.scrollWidth());
    assertEquals(scrollHeight, scrollContainer.scrollHeight());
    assertEquals(clientWidth, scrollContainer.clientWidth());
    assertEquals(clientHeight, scrollContainer.clientHeight());
  }

  @Test
  void layout_whenGridLayoutIsNotImplementedYet_usesBlockLayoutFallback() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 100, 100);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 80, 20);
    grid.addChild(child);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(100, grid.box().content().width());
    assertEquals(100, grid.box().content().height());
    assertTrue(grid.layoutChildNodes().contains(child));
    assertEquals(20, child.box().content().height());
  }

  @Test
  void layout_whenGridFallbackHasAutoHeight_sizesToFlowChildren() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 100, Float.NaN);
    grid.resolvedStyle().height(Unit.AUTO);
    Element first = NodeBuilder.div();
    style(first, Display.BLOCK, 80, 20);
    Element second = NodeBuilder.div();
    style(second, Display.BLOCK, 80, 30);
    grid.addChildren(first, second);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(50, grid.box().content().height());
    assertEquals(0, first.box().content().y());
    assertEquals(20, second.box().content().y());
  }

  private static LayoutService layoutService() {
    FontService fontService =
        mock(FontService.class, withSettings().extraInterfaces(TextMeasurer.class));
    return LayoutServiceProvider.create(
        mock(SystemEventProcessor.class),
        mock(EventProcessor.class),
        mock(TimeService.class),
        fontService);
  }

  private static void style(Element element, Display display, float width, float height) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(display);
    style.position(Position.STATIC);
    style.width(Length.pixel(width));
    style.height(Length.pixel(height));
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
    style.overflowX(Overflow.VISIBLE);
    style.overflowY(Overflow.VISIBLE);
    style.flexBasis(Unit.AUTO);
    style.flexDirection(FlexDirection.ROW);
    style.flexWrap(FlexWrap.NOWRAP);
    style.flexGrow(0);
    style.flexShrink(1);
  }
}
