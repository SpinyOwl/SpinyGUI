package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.TextLayout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
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
import com.spinyowl.spinygui.core.system.font.FontTestOwner;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlexLayoutTest {

  @BeforeEach
  void installFontOwner() {
    FontTestOwner.install();
  }

  @Test
  void setLength_whenPixelLengthHasIntegerLookingValue_passesFloatValue() {
    AtomicReference<Float> value = new AtomicReference<>();

    FlexLayout.setLength(
        Length.pixel(620), 11L, (node, pixel) -> value.set(pixel), (node, percent) -> {});

    assertEquals(620f, value.get());
  }

  @Test
  void setLength_whenPixelLengthHasFractionalValue_passesFloatValue() {
    AtomicReference<Float> value = new AtomicReference<>();

    FlexLayout.setLength(
        Length.pixel(12.5f), 11L, (node, pixel) -> value.set(pixel), (node, percent) -> {});

    assertEquals(12.5f, value.get());
  }

  @Test
  void setLengthWithSide_whenPixelLengthIsUsed_passesFloatValue() {
    AtomicReference<Float> value = new AtomicReference<>();
    AtomicReference<Integer> side = new AtomicReference<>();

    FlexLayout.setLength(
        Length.pixel(7),
        11L,
        3,
        (node, edge, pixel) -> {
          side.set(edge);
          value.set(pixel);
        },
        (node, edge, percent) -> {});

    assertEquals(3, side.get());
    assertEquals(7f, value.get());
  }

  @Test
  void setLength_whenPercentLengthIsUsed_passesYogaPercentValue() {
    AtomicReference<Float> value = new AtomicReference<>();

    FlexLayout.setLength(
        Length.percent(1), 11L, (node, pixel) -> {}, (node, percent) -> value.set(percent));

    assertEquals(100f, value.get());
  }

  @Test
  void setLengthWithSide_whenPercentLengthIsUsed_passesYogaPercentValue() {
    AtomicReference<Float> value = new AtomicReference<>();
    AtomicReference<Integer> side = new AtomicReference<>();

    FlexLayout.setLength(
        Length.percent(1),
        11L,
        3,
        (node, edge, pixel) -> {},
        (node, edge, percent) -> {
          side.set(edge);
          value.set(percent);
        });

    assertEquals(3, side.get());
    assertEquals(100f, value.get());
  }

  @Test
  void setBorderLength_whenPixelLengthIsUsed_passesFloatValue() {
    AtomicReference<Float> value = new AtomicReference<>();

    FlexLayout.setLength(Length.pixel(2.25f), 11L, 3, (node, edge, pixel) -> value.set(pixel));

    assertEquals(2.25f, value.get());
  }

  @Test
  void setUnit_whenPixelLengthIsUsed_passesFloatValue() {
    AtomicReference<Float> value = new AtomicReference<>();
    AtomicBoolean autoCalled = new AtomicBoolean(false);

    FlexLayout.setUnit(
        Length.pixel(34),
        11L,
        node -> autoCalled.set(true),
        (node, pixel) -> value.set(pixel),
        (node, percent) -> {});

    assertFalse(autoCalled.get());
    assertEquals(34f, value.get());
  }

  @Test
  void setUnit_whenPercentLengthIsUsed_passesYogaPercentValue() {
    AtomicReference<Float> value = new AtomicReference<>();
    AtomicBoolean autoCalled = new AtomicBoolean(false);

    FlexLayout.setUnit(
        Length.percent(1),
        11L,
        node -> autoCalled.set(true),
        (node, pixel) -> {},
        (node, percent) -> value.set(percent));

    assertFalse(autoCalled.get());
    assertEquals(100f, value.get());
  }

  @Test
  void applyPixelOrPercentToSide_whenPixelLengthIsUsed_passesFloatValue() {
    AtomicReference<Float> value = new AtomicReference<>();

    FlexLayout.applyPixelOrPercentToSide(
        Length.pixel(12.5f),
        11L,
        3,
        (node, edge, pixel) -> value.set(pixel),
        (node, edge, percent) -> {});

    assertEquals(12.5f, value.get());
  }

  @Test
  void applyPixelOrPercentToSide_whenPercentLengthIsUsed_passesYogaPercentValue() {
    AtomicReference<Float> value = new AtomicReference<>();

    FlexLayout.applyPixelOrPercentToSide(
        Length.percent(1),
        11L,
        3,
        (node, edge, pixel) -> {},
        (node, edge, percent) -> value.set(percent));

    assertEquals(100f, value.get());
  }

  @Test
  void layout_whenFlexNodeUsesPixelLengths_doesNotThrow() {
    Frame parent = NodeBuilder.frame();
    Element child = NodeBuilder.div();
    parent.addChild(child);

    style(parent);
    style(child);
    parent.box().contentSize(620, 34);
    parent.resolvedStyle().display(Display.FLEX);
    parent.resolvedStyle().width(Length.pixel(620));
    parent.resolvedStyle().height(Length.pixel(34));
    parent.resolvedStyle().paddingTop(Length.pixel(7));
    parent.resolvedStyle().paddingRight(Length.pixel(7));
    parent.resolvedStyle().paddingBottom(Length.pixel(7));
    parent.resolvedStyle().paddingLeft(Length.pixel(7));
    child.resolvedStyle().width(Length.pixel(26.5f));
    child.resolvedStyle().height(Length.pixel(10));

    LayoutService layoutService = new NoopLayoutService();
    BlockLayout blockLayout = mock(BlockLayout.class);
    doAnswer(invocation -> null)
        .when(blockLayout)
        .layout(eq(parent), eq(true), any(LayoutContext.class));
    FlexLayout layout =
        new FlexLayout(
            mock(SystemEventProcessor.class),
            mock(EventProcessor.class),
            mock(TimeService.class),
            blockLayout,
            layoutService);

    layout.layout(parent, new LayoutContext());

    assertTrue(child.box().content().width() > 0);
  }

  @Test
  void layout_whenFrameCentersSingleFlexChild_positionsChildInFrameCenter() {
    Frame frame = NodeBuilder.frame();
    Element child = NodeBuilder.div();
    frame.addChild(child);

    style(frame);
    style(child);
    frame.frameSize(720, 640);
    frame.resolvedStyle().display(Display.FLEX);
    frame.resolvedStyle().flexDirection(FlexDirection.COLUMN);
    frame.resolvedStyle().alignItems(AlignItems.CENTER);
    frame.resolvedStyle().justifyContent(JustifyContent.CENTER);
    child.resolvedStyle().width(Length.pixel(420));
    child.resolvedStyle().height(Length.pixel(320));

    var layoutMap = new java.util.HashMap<Display, ElementLayout>();
    LayoutService layoutService = new LayoutServiceImpl(mock(TextLayout.class), layoutMap);
    var blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(mock(TextMeasurer.class)));
    layoutMap.put(Display.NONE, new NoneLayout());
    layoutMap.put(Display.BLOCK, blockLayout);
    layoutMap.put(
        Display.FLEX,
        new FlexLayout(
            mock(SystemEventProcessor.class),
            mock(EventProcessor.class),
            mock(TimeService.class),
            blockLayout,
            layoutService));

    layoutService.layout(frame);

    assertEquals(150, child.box().content().x());
    assertEquals(160, child.box().content().y());
  }

  @Test
  void layout_whenColumnFlexStartHasAutoWidthTextChildren_preservesChildTextWidth() {
    Frame frame = NodeBuilder.frame();
    Element button = NodeBuilder.button();
    Element label = NodeBuilder.div();
    Text labelText = NodeBuilder.text("Start Game");
    Element status = NodeBuilder.div();
    Text statusText = NodeBuilder.text("enabled");
    label.addChild(labelText);
    status.addChild(statusText);
    button.addChildren(label, status);
    frame.addChild(button);

    style(frame);
    style(button);
    style(label);
    style(status);
    frame.frameSize(240, 120);
    button.resolvedStyle().display(Display.FLEX);
    button.resolvedStyle().flexDirection(FlexDirection.COLUMN);
    button.resolvedStyle().alignItems(AlignItems.FLEX_START);
    button.resolvedStyle().width(Length.pixel(180));
    button.resolvedStyle().height(Length.pixel(54));

    layoutService(new FixedTextMeasurer()).layout(frame);

    assertTrue(label.box().content().width() > 0);
    assertTrue(status.box().content().width() > 0);
    assertFalse(labelText.inlineFragments().isEmpty());
    assertFalse(statusText.inlineFragments().isEmpty());
  }

  @Test
  void layout_whenRowFlexStartHasAutoHeightTextChild_preservesChildTextHeight() {
    Frame frame = NodeBuilder.frame();
    Element row = NodeBuilder.div();
    Element label = NodeBuilder.div();
    Text labelText = NodeBuilder.text("Start");
    label.addChild(labelText);
    row.addChild(label);
    frame.addChild(row);

    style(frame);
    style(row);
    style(label);
    frame.frameSize(240, 120);
    row.resolvedStyle().display(Display.FLEX);
    row.resolvedStyle().flexDirection(FlexDirection.ROW);
    row.resolvedStyle().alignItems(AlignItems.FLEX_START);
    row.resolvedStyle().width(Length.pixel(180));
    row.resolvedStyle().height(Length.pixel(54));
    label.resolvedStyle().width(Length.pixel(80));

    layoutService(new FixedTextMeasurer()).layout(frame);

    assertTrue(label.box().content().height() > 0);
    assertFalse(labelText.inlineFragments().isEmpty());
  }

  @Test
  void layout_whenFlexChildIsDisplayNone_doesNotAllocateSpace() {
    Frame frame = NodeBuilder.frame();
    Element first = NodeBuilder.div();
    Element hidden = NodeBuilder.div();
    Element second = NodeBuilder.div();
    frame.addChildren(first, hidden, second);

    style(frame);
    style(first);
    style(hidden);
    style(second);
    frame.frameSize(300, 100);
    frame.resolvedStyle().display(Display.FLEX);
    frame.resolvedStyle().width(Length.pixel(300));
    frame.resolvedStyle().height(Length.pixel(100));
    frame.resolvedStyle().alignItems(AlignItems.FLEX_START);
    first.resolvedStyle().width(Length.pixel(50));
    first.resolvedStyle().height(Length.pixel(20));
    hidden.resolvedStyle().display(Display.NONE);
    hidden.resolvedStyle().width(Length.pixel(100));
    hidden.resolvedStyle().height(Length.pixel(20));
    second.resolvedStyle().width(Length.pixel(50));
    second.resolvedStyle().height(Length.pixel(20));

    layoutService(new FixedTextMeasurer()).layout(frame);

    assertEquals(0, first.box().borderBox().x());
    assertEquals(50, second.box().borderBox().x());
    assertFalse(frame.layoutChildNodes().contains(hidden));
    assertTrue(hidden.layoutChildNodes().isEmpty());
  }

  @Test
  void layout_whenRowFlexChildUsesPercentHeight_resolvesAgainstParentHeight() {
    Frame frame = NodeBuilder.frame();
    Element panel = NodeBuilder.div();
    frame.addChild(panel);

    style(frame);
    style(panel);
    frame.frameSize(300, 120);
    frame.resolvedStyle().display(Display.FLEX);
    frame.resolvedStyle().width(Length.pixel(300));
    frame.resolvedStyle().height(Length.pixel(120));
    panel.resolvedStyle().width(Length.pixel(80));
    panel.resolvedStyle().height(Length.percent(1));

    layoutService(new FixedTextMeasurer()).layout(frame);

    assertEquals(120, panel.box().content().height());
  }

  private static LayoutService layoutService(TextMeasurer textMeasurer) {
    var layoutMap = new HashMap<Display, ElementLayout>();
    LayoutService layoutService = new LayoutServiceImpl(mock(TextLayout.class), layoutMap);
    var blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutMap.put(Display.NONE, new NoneLayout());
    layoutMap.put(Display.BLOCK, blockLayout);
    layoutMap.put(
        Display.FLEX,
        new FlexLayout(
            mock(SystemEventProcessor.class),
            mock(EventProcessor.class),
            mock(TimeService.class),
            blockLayout,
            layoutService));
    return layoutService;
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
    style.fontFamilies(List.of("Roboto"));
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

  private static class NoopLayoutService implements LayoutService {
    @Override
    public void layout(@NonNull Frame frame) {}

    @Override
    public void layoutNode(@NonNull Node node, @NonNull LayoutContext context) {}

    @Override
    public void layoutChildNodes(@NonNull Element element, @NonNull LayoutContext context) {}
  }

  private static class FixedTextMeasurer extends AbstractFixedTextMeasurer {
    @Override
    public TextLineMetrics getTextLineMetrics(
        @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
      FontMetrics fontMetrics =
          new FontMetrics(8, 2, Math.max(0, fontSize * lineHeight - 10), 10, 8);
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
