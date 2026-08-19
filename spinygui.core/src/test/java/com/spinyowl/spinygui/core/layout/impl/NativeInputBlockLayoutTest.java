package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontTestOwner;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import java.util.List;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NativeInputBlockLayoutTest {

  @BeforeEach
  void installFontOwner() {
    FontTestOwner.install();
  }

  @Test
  void layout_toggleInputsUseNativeIntrinsicSize() {
    Frame frame = frame();
    InputElement checkbox = input(TYPE_CHECKBOX, 2);
    InputElement radio = input(TYPE_RADIO, 2);
    frame.addChild(checkbox);
    frame.addChild(radio);

    layout(frame);

    assertEquals(18, checkbox.box().content().width());
    assertEquals(18, checkbox.box().content().height());
    assertEquals(22, checkbox.box().borderBox().width());
    assertEquals(22, checkbox.box().borderBox().height());
    assertEquals(18, radio.box().content().width());
    assertEquals(18, radio.box().content().height());
  }

  @Test
  void layout_rangeUsesNativeIntrinsicSize() {
    Frame frame = frame();
    InputElement range = input(TYPE_RANGE, 2);
    frame.addChild(range);

    layout(frame);

    assertEquals(160, range.box().content().width());
    assertEquals(18, range.box().content().height());
    assertEquals(164, range.box().borderBox().width());
    assertEquals(22, range.box().borderBox().height());
  }

  @Test
  void layout_nativeControlStyledSizeOverridesIntrinsicSize() {
    Frame frame = frame();
    InputElement range = input(TYPE_RANGE, 2);
    range.resolvedStyle().width(Length.pixel(240));
    range.resolvedStyle().height(Length.pixel(30));
    frame.addChild(range);

    layout(frame);

    assertEquals(236, range.box().content().width());
    assertEquals(26, range.box().content().height());
    assertEquals(240, range.box().borderBox().width());
    assertEquals(30, range.box().borderBox().height());
  }

  private Frame frame() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(400, 200);
    style(frame, 0);
    return frame;
  }

  private InputElement input(String type, float borderWidth) {
    InputElement input = NodeBuilder.input(type);
    style(input, borderWidth);
    return input;
  }

  private void layout(Frame frame) {
    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);
    blockLayout.layout(frame, new LayoutContext());
  }

  private void style(Element element, float borderWidth) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(Display.BLOCK);
    style.position(Position.STATIC);
    style.width(Unit.AUTO);
    style.height(Unit.AUTO);
    style.minWidth(null);
    style.maxWidth(null);
    style.minHeight(null);
    style.maxHeight(null);
    style.paddingTop(Length.ZERO);
    style.paddingRight(Length.ZERO);
    style.paddingBottom(Length.ZERO);
    style.paddingLeft(Length.ZERO);
    style.marginTop(Length.ZERO);
    style.marginRight(Length.ZERO);
    style.marginBottom(Length.ZERO);
    style.marginLeft(Length.ZERO);
    style.borderTopWidth(Length.pixel(borderWidth));
    style.borderRightWidth(Length.pixel(borderWidth));
    style.borderBottomWidth(Length.pixel(borderWidth));
    style.borderLeftWidth(Length.pixel(borderWidth));
    BorderStyle borderStyle = borderWidth == 0 ? BorderStyle.NONE : BorderStyle.SOLID;
    style.borderTopStyle(borderStyle);
    style.borderRightStyle(borderStyle);
    style.borderBottomStyle(borderStyle);
    style.borderLeftStyle(borderStyle);
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

  private static class RecursiveLayoutService implements LayoutService {
    private ElementLayout blockLayout;

    void blockLayout(ElementLayout blockLayout) {
      this.blockLayout = blockLayout;
    }

    @Override
    public void layout(@NonNull Frame frame) {
      layoutNode(frame, new LayoutContext());
    }

    @Override
    public void layoutNode(@NonNull Node node, @NonNull LayoutContext context) {
      if (node instanceof Element element) {
        blockLayout.layout(element, context);
      }
    }

    @Override
    public void layoutChildNodes(@NonNull Element element, @NonNull LayoutContext context) {
      LayoutContext inner = new LayoutContext();
      element.childNodes().forEach(node -> layoutNode(node, inner));
    }
  }

  private static class FixedTextMeasurer extends AbstractFixedTextMeasurer {
    @Override
    public TextLineMetrics getTextLineMetrics(
        @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
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
