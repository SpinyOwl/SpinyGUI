package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.TextLayout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.util.OverflowUtils;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class OverflowLayoutTest {

  @Test
  void layout_whenBlockChildExceedsFixedHeight_preservesClientHeightAndMeasuresScrollHeight() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 300);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(100, container.box().content().height());
    assertEquals(100, container.clientHeight());
    assertEquals(300, container.scrollHeight());

    container.scrollTop(500);
    layoutService().layout(frame);

    assertEquals(200, container.scrollTop());
  }

  @Test
  void layout_nestedScrollableRows_keepsEveryRowAndRefreshesOverflowAfterMutation() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 200, 200);
    container.resolvedStyle().overflowX(Overflow.HIDDEN);
    container.resolvedStyle().overflowY(Overflow.SCROLL);
    Element lines = NodeBuilder.div();
    style(lines, Display.BLOCK, 188, 800);
    for (int i = 0; i < 40; i++) {
      Element row = NodeBuilder.div();
      style(row, Display.BLOCK, 188, 20);
      lines.addChild(row);
    }
    container.addChild(lines);
    frame.addChild(container);

    LayoutService layoutService = layoutService();
    layoutService.layout(frame);

    assertEquals(800, container.scrollHeight());
    assertEquals(40, lines.layoutChildNodes().size());
    container.scrollTop(300);
    layoutService.layout(frame);
    assertEquals(300, container.scrollTop());
    assertTrue(lines.layoutChildNodes().contains(lines.childNodes().get(25)));

    for (int i = 0; i < 30; i++) {
      lines.removeChild(lines.childNodes().get(lines.childNodes().size() - 1));
    }
    lines.resolvedStyle().height(Unit.AUTO);
    layoutService.layout(frame);

    assertEquals(200, container.scrollHeight());
    assertEquals(0, container.scrollTop());
    assertEquals(10, lines.layoutChildNodes().size());
  }

  @Test
  void layout_whenFlexItemChildExceedsAllocatedHeight_preservesItemSizeAndMeasuresOverflow() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.FLEX, 500, 100);
    frame.resolvedStyle().alignItems(AlignItems.FLEX_START);
    Element item = NodeBuilder.div();
    style(item, Display.BLOCK, 100, 100);
    item.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 300);
    item.addChild(child);
    frame.addChild(item);

    layoutService().layout(frame);

    assertEquals(100, item.box().content().height());
    assertEquals(100, item.clientHeight());
    assertEquals(300, item.scrollHeight());
    assertEquals(200, OverflowUtils.maxScrollTop(item));
  }

  @Test
  void layout_whenOverflowIsVisible_preservesMetricsButDoesNotAcceptWheelScroll() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.VISIBLE);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 300);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(100, container.clientHeight());
    assertEquals(300, container.scrollHeight());
    assertEquals(200, OverflowUtils.maxScrollTop(container));
    assertFalse(OverflowUtils.acceptsWheelY(container));
  }

  @Test
  void layout_whenChildIsAbsolutelyPositioned_doesNotIncludeItInScrollSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 300);
    child.resolvedStyle().position(Position.ABSOLUTE);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(100, container.clientHeight());
    assertEquals(0, container.scrollHeight());
    assertEquals(0, OverflowUtils.maxScrollTop(container));
  }

  @Test
  void layout_whenChildContentFits_resetsScrollOffsetAfterLayout() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 50);
    container.addChild(child);
    container.scrollTop(25);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(100, container.clientHeight());
    assertEquals(50, container.scrollHeight());
    assertEquals(0, container.scrollTop());
    assertFalse(OverflowUtils.acceptsWheelY(container));
  }

  @Test
  void layout_whenVisibleChildBecomesDisplayNone_removesStaleScrollFootprintAndLayoutState() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 300);
    Element grandchild = NodeBuilder.div();
    style(grandchild, Display.BLOCK, 100, 50);
    child.addChild(grandchild);
    container.addChild(child);
    frame.addChild(container);

    LayoutService layoutService = layoutService();
    layoutService.layout(frame);

    assertEquals(300, container.scrollHeight());
    assertTrue(container.layoutChildNodes().contains(child));
    assertTrue(child.layoutChildNodes().contains(grandchild));

    child.resolvedStyle().display(Display.NONE);
    layoutService.layout(frame);

    assertEquals(0, container.scrollHeight());
    assertEquals(0, container.scrollWidth());
    assertFalse(container.layoutChildNodes().contains(child));
    assertTrue(child.layoutChildNodes().isEmpty());
    assertNull(child.offsetParent());
    assertEquals(0, child.scrollWidth());
    assertEquals(0, child.scrollHeight());
    assertEquals(0, child.clientWidth());
    assertEquals(0, child.clientHeight());
    assertNull(child.scrollbarMetrics());
    assertEquals(0, child.box().content().width());
    assertEquals(0, child.box().content().height());
    assertTrue(grandchild.layoutChildNodes().isEmpty());
    assertNull(grandchild.offsetParent());
    assertEquals(0, grandchild.scrollWidth());
    assertEquals(0, grandchild.scrollHeight());
    assertEquals(0, grandchild.clientWidth());
    assertEquals(0, grandchild.clientHeight());
    assertNull(grandchild.scrollbarMetrics());
  }

  @Test
  void layout_whenHiddenAbsoluteChildExists_excludesItFromLayoutTreeAndScrollMetrics() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.NONE, 100, 300);
    child.resolvedStyle().position(Position.ABSOLUTE);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(0, container.scrollHeight());
    assertEquals(0, container.scrollWidth());
    assertFalse(container.layoutChildNodes().contains(child));
    assertNull(child.offsetParent());
  }

  @Test
  void layout_whenBlockChildIsDisplayNone_doesNotReserveFlowSpace() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element first = NodeBuilder.div();
    style(first, Display.BLOCK, 100, 20);
    Element hidden = NodeBuilder.div();
    style(hidden, Display.NONE, 100, 50);
    Element second = NodeBuilder.div();
    style(second, Display.BLOCK, 100, 20);
    frame.addChildren(first, hidden, second);

    layoutService().layout(frame);

    assertEquals(0, first.box().borderBox().y());
    assertEquals(20, second.box().borderBox().y());
    assertTrue(frame.layoutChildNodes().contains(first));
    assertFalse(frame.layoutChildNodes().contains(hidden));
    assertTrue(frame.layoutChildNodes().contains(second));
  }

  @Test
  void layout_whenOverflowYIsScroll_reservesVerticalScrollbarGutterEvenWhenContentFits() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.SCROLL);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 50, 50);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(88, container.clientWidth());
    assertEquals(100, container.clientHeight());
  }

  @Test
  void layout_whenVerticalScrollbarIsVisible_sizesPercentBlockToClientWidth() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    container.setAttribute("class", "panel");
    frame.addChild(container);
    applyStyleSheet(frame, ".panel::-webkit-scrollbar { width: 10px; }");
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.SCROLL);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 150);
    child.resolvedStyle().width(Length.percent(1));
    container.addChild(child);

    layoutService().layout(frame);

    assertEquals(90, container.clientWidth());
    assertEquals(90, child.box().borderBox().width());
    assertTrue(
        child.box().borderBox().x() + child.box().borderBox().width()
            <= container.scrollbarMetrics().verticalTrack().x());
  }

  @Test
  void layout_whenScrollbarAndPaddedDescendantsExist_keepsBorderBoxBeforeTrack() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    container.setAttribute("class", "panel");
    frame.addChild(container);
    applyStyleSheet(frame, ".panel::-webkit-scrollbar { width: 10px; }");
    style(container, Display.BLOCK, 120, 100);
    container.resolvedStyle().overflowY(Overflow.SCROLL);
    Element wrapper = NodeBuilder.div();
    style(wrapper, Display.BLOCK, 100, 20);
    wrapper.resolvedStyle().width(Length.percent(1));
    wrapper.resolvedStyle().height(Unit.AUTO);
    wrapper.resolvedStyle().paddingLeft(Length.pixel(5));
    wrapper.resolvedStyle().paddingRight(Length.pixel(5));
    Element button = NodeBuilder.button();
    style(button, Display.BLOCK, 100, 20);
    button.resolvedStyle().width(Length.percent(1));
    button.resolvedStyle().paddingLeft(Length.pixel(4));
    button.resolvedStyle().paddingRight(Length.pixel(4));
    button.resolvedStyle().borderLeftWidth(Length.pixel(2));
    button.resolvedStyle().borderRightWidth(Length.pixel(2));
    button.resolvedStyle().borderLeftStyle(BorderStyle.SOLID);
    button.resolvedStyle().borderRightStyle(BorderStyle.SOLID);
    Element spacer = NodeBuilder.div();
    style(spacer, Display.BLOCK, 100, 150);
    wrapper.addChild(button);
    container.addChildren(wrapper, spacer);

    layoutService().layout(frame);

    assertEquals(container.clientWidth(), wrapper.box().borderBox().width());
    assertTrue(
        button.box().borderBox().x() + button.box().borderBox().width()
            <= container.scrollbarMetrics().verticalTrack().x());
  }

  @Test
  void layout_whenOverflowAutoContentFits_doesNotReserveScrollbarGutter() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowX(Overflow.AUTO);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 50, 50);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(100, container.clientWidth());
    assertEquals(100, container.clientHeight());
  }

  @Test
  void layout_whenAutoVerticalScrollbarChanges_visibilityUpdatesDescendantWidth() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 50);
    child.resolvedStyle().width(Length.percent(1));
    container.addChild(child);
    frame.addChild(container);

    LayoutService layoutService = layoutService();
    layoutService.layout(frame);
    assertEquals(100, container.clientWidth());
    assertEquals(100, child.box().borderBox().width());

    child.resolvedStyle().height(Length.pixel(150));
    layoutService.layout(frame);
    assertEquals(88, container.clientWidth());
    assertEquals(88, child.box().borderBox().width());

    child.resolvedStyle().height(Length.pixel(50));
    layoutService.layout(frame);
    assertEquals(100, container.clientWidth());
    assertEquals(100, child.box().borderBox().width());
  }

  @Test
  void layout_whenAutoVerticalScrollbarReducesWidth_canCreateHorizontalScrollbar() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowX(Overflow.AUTO);
    container.resolvedStyle().overflowY(Overflow.AUTO);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 95, 300);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertEquals(88, container.clientWidth());
    assertEquals(88, container.clientHeight());
  }

  @Test
  void layout_usesWebkitScrollbarWidthAndHeightForGutters() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    container.setAttribute("class", "panel");
    frame.addChild(container);
    applyStyleSheet(frame, ".panel::-webkit-scrollbar { width: 12px; height: 10px; }");
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowX(Overflow.SCROLL);
    container.resolvedStyle().overflowY(Overflow.SCROLL);

    layoutService().layout(frame);

    assertEquals(88, container.clientWidth());
    assertEquals(90, container.clientHeight());
  }

  @Test
  void layout_whenBothScrollbarsAreVisible_usesReducedVerticalTrackHeight() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element container = NodeBuilder.div();
    style(container, Display.BLOCK, 100, 100);
    container.resolvedStyle().overflowX(Overflow.SCROLL);
    container.resolvedStyle().overflowY(Overflow.SCROLL);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 200, 200);
    container.addChild(child);
    frame.addChild(container);

    layoutService().layout(frame);

    assertTrue(container.scrollbarMetrics().verticalVisible());
    assertTrue(container.scrollbarMetrics().horizontalVisible());
    assertEquals(88, container.scrollbarMetrics().verticalTrack().height());
    assertEquals(88, container.scrollbarMetrics().horizontalTrack().width());
  }

  @Test
  void layout_whenScrollContainersAreNested_reservesEachScrollbarGutter() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(500, 500);
    style(frame, Display.BLOCK, 500, 500);
    Element outer = NodeBuilder.div();
    style(outer, Display.BLOCK, 120, 120);
    outer.resolvedStyle().overflowY(Overflow.SCROLL);
    Element inner = NodeBuilder.div();
    style(inner, Display.BLOCK, 100, 80);
    inner.resolvedStyle().width(Length.percent(1));
    inner.resolvedStyle().overflowY(Overflow.SCROLL);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 100, 160);
    child.resolvedStyle().width(Length.percent(1));
    inner.addChild(child);
    outer.addChild(inner);
    frame.addChild(outer);

    layoutService().layout(frame);

    assertTrue(
        inner.box().borderBox().x() + inner.box().borderBox().width()
            <= outer.scrollbarMetrics().verticalTrack().x());
    assertTrue(
        child.box().borderBox().x() + child.box().borderBox().width()
            <= inner.scrollbarMetrics().verticalTrack().x());
  }

  private static void applyStyleSheet(Frame frame, String css) {
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    frame.styleSheets().add(parser.parse(css));
    new StyleManagerImpl(propertyStore, parser).recalculate(frame);
  }

  private static LayoutService layoutService() {
    var layoutMap = new HashMap<Display, ElementLayout>();
    LayoutService layoutService = new LayoutServiceImpl(mock(TextLayout.class), layoutMap);
    var blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(mock(TextMeasurer.class)));
    layoutMap.put(Display.NONE, new NoneLayout());
    layoutMap.put(Display.BLOCK, blockLayout);
    layoutMap.put(
        Display.FLEX,
        new FlexLayout(blockLayout, layoutService));
    return layoutService;
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
    style.flexShrink(0);
    style.justifyContent(JustifyContent.FLEX_START);
    style.alignItems(AlignItems.FLEX_START);
    style.alignSelf(AlignSelf.AUTO);
  }
}
