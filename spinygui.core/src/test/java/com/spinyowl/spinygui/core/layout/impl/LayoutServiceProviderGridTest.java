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
import com.spinyowl.spinygui.core.style.types.grid.GridPlacement;
import com.spinyowl.spinygui.core.style.types.grid.GridTemplateAreas;
import com.spinyowl.spinygui.core.style.types.grid.GridTrack;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackList;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackSize;
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
  void layout_placesGridChildrenIntoFixedTracks() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 140, 90);
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(50))),
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(70))))));
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(30))))));
    grid.resolvedStyle().gridColumnGap(Length.pixel(10));
    Element first = NodeBuilder.div();
    style(first, Display.BLOCK, Float.NaN, Float.NaN);
    first.resolvedStyle().width(Unit.AUTO);
    first.resolvedStyle().height(Unit.AUTO);
    Element second = NodeBuilder.div();
    style(second, Display.BLOCK, Float.NaN, Float.NaN);
    second.resolvedStyle().width(Unit.AUTO);
    second.resolvedStyle().height(Unit.AUTO);
    grid.addChildren(first, second);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(50, first.box().content().width());
    assertEquals(30, first.box().content().height());
    assertEquals(0, first.box().content().x());
    assertEquals(0, first.box().content().y());
    assertEquals(70, second.box().content().width());
    assertEquals(30, second.box().content().height());
    assertEquals(60, second.box().content().x());
    assertEquals(0, second.box().content().y());
  }

  @Test
  void layout_gridAutoHeightSizesToRows() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 100, Float.NaN);
    grid.resolvedStyle().height(Unit.AUTO);
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(20))),
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(30))))));
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(80))))));
    Element first = NodeBuilder.div();
    style(first, Display.BLOCK, Float.NaN, Float.NaN);
    first.resolvedStyle().width(Unit.AUTO);
    first.resolvedStyle().height(Unit.AUTO);
    Element second = NodeBuilder.div();
    style(second, Display.BLOCK, Float.NaN, Float.NaN);
    second.resolvedStyle().width(Unit.AUTO);
    second.resolvedStyle().height(Unit.AUTO);
    grid.addChildren(first, second);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(50, grid.box().content().height());
    assertEquals(0, first.box().content().y());
    assertEquals(20, second.box().content().y());
  }

  @Test
  void layout_honorsExplicitGridPlacementAndExcludesAbsoluteChildren() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 120, 120);
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(40))),
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(60))))));
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(20))),
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(30))))));
    Element placed = NodeBuilder.div();
    style(placed, Display.BLOCK, Float.NaN, Float.NaN);
    placed.resolvedStyle().width(Unit.AUTO);
    placed.resolvedStyle().height(Unit.AUTO);
    placed.resolvedStyle().gridColumnStart(GridPlacement.line(2));
    placed.resolvedStyle().gridRowStart(GridPlacement.line(2));
    Element absolute = NodeBuilder.div();
    style(absolute, Display.BLOCK, 10, 10);
    absolute.resolvedStyle().position(Position.ABSOLUTE);
    grid.addChildren(placed, absolute);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(40, placed.box().content().x());
    assertEquals(20, placed.box().content().y());
    assertEquals(60, placed.box().content().width());
    assertEquals(30, placed.box().content().height());
  }

  @Test
  void layout_placesItemsByNamedTemplateAreas() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 120, 80);
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(40))),
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(80))))));
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(30))),
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(50))))));
    grid.resolvedStyle()
        .gridTemplateAreas(
            GridTemplateAreas.of(
                java.util.List.of(
                    java.util.List.of("header", "header"),
                    java.util.List.of("sidebar", "main"))));
    Element header = NodeBuilder.div();
    style(header, Display.BLOCK, Float.NaN, Float.NaN);
    header.resolvedStyle().width(Unit.AUTO);
    header.resolvedStyle().height(Unit.AUTO);
    header.resolvedStyle().gridRowStart(GridPlacement.line("header"));
    Element main = NodeBuilder.div();
    style(main, Display.BLOCK, Float.NaN, Float.NaN);
    main.resolvedStyle().width(Unit.AUTO);
    main.resolvedStyle().height(Unit.AUTO);
    main.resolvedStyle().gridRowStart(GridPlacement.line("main"));
    grid.addChildren(header, main);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(120, header.box().content().width());
    assertEquals(30, header.box().content().height());
    assertEquals(0, header.box().content().x());
    assertEquals(0, header.box().content().y());
    assertEquals(80, main.box().content().width());
    assertEquals(50, main.box().content().height());
    assertEquals(40, main.box().content().x());
    assertEquals(30, main.box().content().y());
  }

  @Test
  void layout_placesGridItemsRelativeToPositionedPaddedGridContainer() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(720, 460);
    style(frame, Display.BLOCK, 720, 460);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 430, 256);
    grid.resolvedStyle().position(Position.ABSOLUTE);
    grid.resolvedStyle().left(Length.pixel(32));
    grid.resolvedStyle().top(Length.pixel(112));
    grid.resolvedStyle().paddingTop(Length.pixel(16));
    grid.resolvedStyle().paddingRight(Length.pixel(16));
    grid.resolvedStyle().paddingBottom(Length.pixel(16));
    grid.resolvedStyle().paddingLeft(Length.pixel(16));
    grid.resolvedStyle().borderTopWidth(Length.pixel(3));
    grid.resolvedStyle().borderRightWidth(Length.pixel(3));
    grid.resolvedStyle().borderBottomWidth(Length.pixel(3));
    grid.resolvedStyle().borderLeftWidth(Length.pixel(3));
    grid.resolvedStyle().borderTopStyle(BorderStyle.SOLID);
    grid.resolvedStyle().borderRightStyle(BorderStyle.SOLID);
    grid.resolvedStyle().borderBottomStyle(BorderStyle.SOLID);
    grid.resolvedStyle().borderLeftStyle(BorderStyle.SOLID);
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(
                        GridTrackSize.flexible(
                            com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(2))),
                    GridTrack.of(
                        GridTrackSize.minmax(
                            GridTrackSize.fixed(Length.pixel(112)),
                            GridTrackSize.flexible(
                                com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(1)))))));
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(
                        GridTrackSize.flexible(
                            com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(1))),
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(58))))));
    grid.resolvedStyle()
        .gridTemplateAreas(
            GridTemplateAreas.of(
                java.util.List.of(
                    java.util.List.of("featured", "actions"),
                    java.util.List.of("summary", "actions"))));
    grid.resolvedStyle().gridRowGap(Length.pixel(12));
    grid.resolvedStyle().gridColumnGap(Length.pixel(18));
    Element featured = demoGridCard("featured");
    Element summary = demoGridCard("summary");
    Element actions = demoGridCard("actions");
    grid.addChildren(featured, summary, actions);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(51, grid.box().content().x(), 0.001);
    assertEquals(131, grid.box().content().y(), 0.001);
    assertEquals(392, grid.box().content().width(), 0.001);
    assertEquals(218, grid.box().content().height(), 0.001);
    assertEquals(19, featured.box().borderBox().x(), 0.001);
    assertEquals(19, featured.box().borderBox().y(), 0.001);
    assertEquals(174.667, featured.box().borderBox().width(), 0.001);
    assertEquals(148, featured.box().borderBox().height(), 0.001);
    assertEquals(51, featured.absolutePosition().x(), 0.001);
    assertEquals(131, featured.absolutePosition().y(), 0.001);
    assertEquals(19, summary.box().borderBox().x(), 0.001);
    assertEquals(179, summary.box().borderBox().y(), 0.001);
    assertEquals(211.667, actions.box().borderBox().x(), 0.001);
    assertEquals(19, actions.box().borderBox().y(), 0.001);
    assertEquals(199.333, actions.box().borderBox().width(), 0.001);
    assertEquals(218, actions.box().borderBox().height(), 0.001);
    assertEquals(243.667, actions.absolutePosition().x(), 0.001);
    assertEquals(131, actions.absolutePosition().y(), 0.001);
    assertEquals(224.667, actions.box().content().x(), 0.001);
    assertEquals(32, actions.box().content().y(), 0.001);
  }

  @Test
  void layout_resolvesPercentageAndFlexibleTracksAfterGaps() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 200, 40);
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.percent(0.25f))),
                    GridTrack.of(
                        GridTrackSize.flexible(
                            com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(1))),
                    GridTrack.of(
                        GridTrackSize.flexible(
                            com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(2))))));
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(20))))));
    grid.resolvedStyle().gridColumnGap(Length.pixel(10));
    Element first = gridItem();
    Element second = gridItem();
    Element third = gridItem();
    grid.addChildren(first, second, third);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(50, first.box().content().width(), 0.001);
    assertEquals(43.333, second.box().content().width(), 0.001);
    assertEquals(86.666, third.box().content().width(), 0.001);
    assertEquals(60, second.box().content().x(), 0.001);
    assertEquals(113.333, third.box().content().x(), 0.001);
  }

  @Test
  void layout_distributesFreeSpaceToMinmaxFlexibleTracksAboveTheirMinimum() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 200, 40);
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(
                java.util.List.of(
                    GridTrack.of(GridTrackSize.fixed(Length.pixel(50))),
                    GridTrack.of(
                        GridTrackSize.flexible(
                            com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(1))),
                    GridTrack.of(
                        GridTrackSize.minmax(
                            GridTrackSize.fixed(Length.pixel(30)),
                            GridTrackSize.flexible(
                                com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(2)))))));
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(20))))));
    grid.resolvedStyle().gridColumnGap(Length.pixel(10));
    Element first = gridItem();
    Element second = gridItem();
    Element third = gridItem();
    grid.addChildren(first, second, third);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(50, first.box().content().width(), 0.001);
    assertEquals(33.333, second.box().content().width(), 0.001);
    assertEquals(96.666, third.box().content().width(), 0.001);
    assertEquals(103.333, third.box().content().x(), 0.001);
  }

  @Test
  void layout_gridOverflowContributesScrollMetrics() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 300);
    style(frame, Display.BLOCK, 300, 300);
    Element grid = NodeBuilder.div();
    style(grid, Display.GRID, 100, 100);
    grid.resolvedStyle().overflowX(Overflow.AUTO);
    grid.resolvedStyle().overflowY(Overflow.AUTO);
    grid.resolvedStyle()
        .gridTemplateColumns(
            GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(140))))));
    grid.resolvedStyle()
        .gridTemplateRows(
            GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(120))))));
    Element item = gridItem();
    grid.addChild(item);
    frame.addChild(grid);

    layoutService().layout(frame);

    assertEquals(140, grid.scrollWidth());
    assertEquals(120, grid.scrollHeight());
    assertTrue(grid.clientWidth() < grid.scrollWidth());
    assertTrue(grid.clientHeight() < grid.scrollHeight());
  }

  @Test
  void layout_reflowsNestedNamedAreaGridAfterEachOuterTrackSizeChange() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(600, 120);
    style(frame, Display.BLOCK, 600, 120);
    Element outer = NodeBuilder.div();
    style(outer, Display.GRID, 600, 60);
    outer.resolvedStyle().gridTemplateRows(GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(60))))));
    outer.resolvedStyle().gridTemplateAreas(GridTemplateAreas.of(java.util.List.of(java.util.List.of("diagnostics"))));
    Element nested = gridItem();
    nested.resolvedStyle().display(Display.GRID);
    nested.resolvedStyle().gridRowStart(GridPlacement.line("diagnostics"));
    nested.resolvedStyle().gridTemplateColumns(
        GridTrackList.of(java.util.List.of(
            GridTrack.of(GridTrackSize.flexible(com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(1))),
            GridTrack.of(GridTrackSize.fixed(Length.pixel(8))))));
    nested.resolvedStyle().gridTemplateRows(GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.flexible(com.spinyowl.spinygui.core.style.types.grid.GridFraction.fr(1))))));
    nested.resolvedStyle().gridTemplateAreas(GridTemplateAreas.of(java.util.List.of(java.util.List.of("scroll", "resize"))));
    Element viewport = gridItem();
    viewport.resolvedStyle().gridRowStart(GridPlacement.line("scroll"));
    Element viewportChild = gridItem();
    viewportChild.resolvedStyle().width(Length.pixel(20));
    viewportChild.resolvedStyle().height(Length.pixel(10));
    viewport.addChild(viewportChild);
    Element handle = gridItem();
    handle.resolvedStyle().gridRowStart(GridPlacement.line("resize"));
    nested.addChildren(viewport, handle);
    outer.addChild(nested);
    frame.addChild(outer);
    LayoutService layoutService = layoutService();

    assertNestedGridTrack(layoutService, outer, nested, handle, viewportChild, 260);
    assertNestedGridTrack(layoutService, outer, nested, handle, viewportChild, 220);
    assertNestedGridTrack(layoutService, outer, nested, handle, viewportChild, 480);
  }

  @Test
  void layout_absoluteZeroInsetsUsePositionedAncestorPaddingBox() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(400, 300);
    style(frame, Display.BLOCK, 400, 300);
    Element ancestor = NodeBuilder.div();
    style(ancestor, Display.BLOCK, 180, 100);
    ancestor.resolvedStyle().position(Position.RELATIVE);
    ancestor.resolvedStyle().left(Length.pixel(40));
    ancestor.resolvedStyle().top(Length.pixel(30));
    ancestor.resolvedStyle().paddingTop(Length.pixel(7));
    ancestor.resolvedStyle().paddingRight(Length.pixel(11));
    ancestor.resolvedStyle().borderTopWidth(Length.pixel(3));
    ancestor.resolvedStyle().borderRightWidth(Length.pixel(5));
    ancestor.resolvedStyle().borderTopStyle(BorderStyle.SOLID);
    ancestor.resolvedStyle().borderRightStyle(BorderStyle.SOLID);
    Element child = NodeBuilder.div();
    style(child, Display.BLOCK, 20, 10);
    child.resolvedStyle().position(Position.ABSOLUTE);
    child.resolvedStyle().top(Length.ZERO);
    child.resolvedStyle().right(Length.ZERO);
    ancestor.addChild(child);
    frame.addChild(ancestor);

    layoutService().layout(frame);

    assertEquals(ancestor.absolutePosition().y() + ancestor.box().border().top(), child.absolutePosition().y(), 0.001);
    assertEquals(
        ancestor.absolutePosition().x() + ancestor.box().border().left() + ancestor.box().paddingBox().width(),
        child.absolutePosition().x() + child.box().borderBox().width(),
        0.001);
  }

  private static void assertNestedGridTrack(
      LayoutService layoutService, Element outer, Element nested, Element handle, Element viewportChild, float width) {
    outer.resolvedStyle().gridTemplateColumns(
        GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(width))))));
    layoutService.layout(outer.frame());

    assertEquals(8, handle.box().content().width(), 0.001);
    assertEquals(width - 8, handle.box().content().x(), 0.001);
    assertEquals(
        nested.absolutePosition().x() + nested.box().border().left() + nested.box().paddingBox().width(),
        handle.absolutePosition().x() + handle.box().borderBox().width(),
        0.001);
    assertEquals(0, viewportChild.box().content().x(), 0.001);
    assertEquals(0, viewportChild.box().content().y(), 0.001);
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
    style.gridAutoColumns(GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.AUTO))));
    style.gridAutoRows(GridTrackList.of(java.util.List.of(GridTrack.of(GridTrackSize.AUTO))));
  }

  private static Element gridItem() {
    Element item = NodeBuilder.div();
    style(item, Display.BLOCK, Float.NaN, Float.NaN);
    item.resolvedStyle().width(Unit.AUTO);
    item.resolvedStyle().height(Unit.AUTO);
    return item;
  }

  private static Element demoGridCard(String areaName) {
    Element item = gridItem();
    item.resolvedStyle().paddingTop(Length.pixel(10));
    item.resolvedStyle().paddingRight(Length.pixel(10));
    item.resolvedStyle().paddingBottom(Length.pixel(10));
    item.resolvedStyle().paddingLeft(Length.pixel(10));
    item.resolvedStyle().borderTopWidth(Length.pixel(3));
    item.resolvedStyle().borderRightWidth(Length.pixel(3));
    item.resolvedStyle().borderBottomWidth(Length.pixel(3));
    item.resolvedStyle().borderLeftWidth(Length.pixel(3));
    item.resolvedStyle().borderTopStyle(BorderStyle.SOLID);
    item.resolvedStyle().borderRightStyle(BorderStyle.SOLID);
    item.resolvedStyle().borderBottomStyle(BorderStyle.SOLID);
    item.resolvedStyle().borderLeftStyle(BorderStyle.SOLID);
    item.resolvedStyle().gridRowStart(GridPlacement.line(areaName));
    return item;
  }
}
