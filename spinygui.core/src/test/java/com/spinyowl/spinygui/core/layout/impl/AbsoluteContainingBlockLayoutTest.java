package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import java.util.List;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class AbsoluteContainingBlockLayoutTest {

  @Test
  void layout_whenAbsoluteAutoHeightUsesTopAndBottom_sizesBeforeNestedLayout() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(400, 300);
    style(frame);

    Element root = NodeBuilder.div();
    style(root);
    root.resolvedStyle().position(Position.RELATIVE);
    root.resolvedStyle().width(Length.pixel(400));
    root.resolvedStyle().height(Length.pixel(300));

    Element workspace = NodeBuilder.div();
    style(workspace);
    workspace.resolvedStyle().position(Position.ABSOLUTE);
    workspace.resolvedStyle().left(Length.ZERO);
    workspace.resolvedStyle().right(Length.ZERO);
    workspace.resolvedStyle().top(Length.pixel(48));
    workspace.resolvedStyle().bottom(Length.pixel(28));
    workspace.resolvedStyle().height(Unit.AUTO);

    Element nested = NodeBuilder.div();
    style(nested);
    nested.resolvedStyle().position(Position.ABSOLUTE);
    nested.resolvedStyle().left(Length.ZERO);
    nested.resolvedStyle().right(Length.ZERO);
    nested.resolvedStyle().top(Length.ZERO);
    nested.resolvedStyle().bottom(Length.ZERO);
    nested.resolvedStyle().height(Unit.AUTO);

    workspace.addChild(nested);
    root.addChild(workspace);
    frame.addChild(root);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer measurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(measurer), measurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(300f, root.box().content().height());
    assertEquals(224f, workspace.box().content().height());
    assertEquals(224f, nested.box().content().height());
    assertTrue(Float.isFinite(workspace.box().borderBox().height()));
    assertTrue(workspace.box().borderBox().height() >= 0f);
    assertTrue(Float.isFinite(nested.box().borderBox().height()));
    assertTrue(nested.box().borderBox().height() >= 0f);
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
  }

  private static final class RecursiveLayoutService implements LayoutService {
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

  private static final class FixedTextMeasurer extends AbstractFixedTextMeasurer {
    @Override
    public TextLineMetrics getTextLineMetrics(
        @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
      FontMetrics fontMetrics = new FontMetrics(8, 2, 0, 10, 8);
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
