package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.FontMetrics;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.TextMeasurer;
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
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Set;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class BlockLayoutTest {

  @Test
  void layout_whenNestedBlockHasInlineTextBeforeBlockChild_doesNotDoubleCountParentOffset() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(200, 200);
    style(frame, 0);

    Element wrapper = NodeBuilder.div();
    style(wrapper, 8);
    Element mixed = NodeBuilder.div();
    style(mixed, 8);
    Text text = NodeBuilder.text("c1");
    Element blockChild = NodeBuilder.div("c11");
    style(blockChild, 8);

    mixed.addChildren(text, blockChild);
    wrapper.addChild(mixed);
    frame.addChild(wrapper);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(new FixedTextMeasurer()));
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(8, text.inlineFragments().get(0).y());
    assertEquals(18, blockChild.box().borderBox().y());
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
