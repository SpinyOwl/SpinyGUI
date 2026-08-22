package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.LayoutResult;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
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

class BlockLayoutTest {

  @BeforeEach
  void installFontOwner() {
    FontTestOwner.install();
  }

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

  @Test
  void layout_whenInlineRunPrecedesBlockChild_includesInlineHeightInBlockFlow() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(200, 200);
    style(frame, 0);
    Element wrapper = NodeBuilder.div();
    style(wrapper, 0);
    Text left = NodeBuilder.text("a");
    Element inline = new Element("span");
    style(inline, 0);
    inline.resolvedStyle().display(Display.INLINE);
    inline.addChild(NodeBuilder.text("b"));
    Element blockChild = NodeBuilder.div();
    style(blockChild, 5);
    wrapper.addChildren(left, inline, blockChild);
    frame.addChild(wrapper);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(new FixedTextMeasurer()));
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(10, blockChild.box().borderBox().y());
    assertEquals(20, wrapper.box().content().height());
  }

  @Test
  void layout_whenInlineContentFollowsBlock_startsAfterBlock() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(200, 200);
    style(frame, 0);
    Element wrapper = NodeBuilder.div();
    style(wrapper, 0);
    Element blockChild = NodeBuilder.div();
    style(blockChild, 5);
    Text text = NodeBuilder.text("a");
    wrapper.addChildren(blockChild, text);
    frame.addChild(wrapper);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(new FixedTextMeasurer()));
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(10, text.inlineFragments().get(0).y());
  }

  @Test
  void layout_whenDisplayNoneDescendantInsideInline_ignoresDescendant() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(200, 200);
    style(frame, 0);
    Element wrapper = NodeBuilder.div();
    style(wrapper, 0);
    Element inline = new Element("span");
    style(inline, 0);
    inline.resolvedStyle().display(Display.INLINE);
    Text left = NodeBuilder.text("a");
    Element hidden = new Element("span");
    style(hidden, 0);
    hidden.resolvedStyle().display(Display.NONE);
    Text hiddenText = NodeBuilder.text("hidden");
    hidden.addChild(hiddenText);
    Text right = NodeBuilder.text("b");
    inline.addChildren(left, hidden, right);
    wrapper.addChild(inline);
    frame.addChild(wrapper);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(new FixedTextMeasurer()));
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(0, hiddenText.inlineFragments().size());
    assertEquals(20, inline.box().content().width());
  }

  @Test
  void layout_whenTextInputHasAutoSize_getsDefaultWidthAndMeasuredLineHeight() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input();
    style(input, 2);
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(156, input.box().content().width());
    assertEquals(10, input.box().content().height());
    assertEquals(160, input.box().borderBox().width());
    assertEquals(14, input.box().borderBox().height());
  }

  @Test
  void layout_whenTextInputHasStyledSize_usesStyledSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input();
    style(input, 2);
    input.resolvedStyle().width(Length.pixel(120));
    input.resolvedStyle().height(Length.pixel(24));
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(116, input.box().content().width());
    assertEquals(20, input.box().content().height());
    assertEquals(120, input.box().borderBox().width());
    assertEquals(24, input.box().borderBox().height());
  }

  @Test
  void layout_whenInlineBlockHasAutoWidth_shrinkWrapsAndStaysInInlineFlow() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(200, 200);
    style(frame, 0);
    Element wrapper = NodeBuilder.div();
    style(wrapper, 0);
    Text left = NodeBuilder.text("a");
    Element inlineBlock = NodeBuilder.div(NodeBuilder.text("bb"));
    style(inlineBlock, 0);
    inlineBlock.resolvedStyle().display(Display.INLINE_BLOCK);
    Text right = NodeBuilder.text("c");
    wrapper.addChildren(left, inlineBlock, right);
    frame.addChild(wrapper);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    InlineFormattingContext inlineFormattingContext = new InlineFormattingContext(textMeasurer);
    BlockLayout blockLayout =
        new BlockLayout(layoutService, inlineFormattingContext, textMeasurer);
    inlineFormattingContext.inlineBlockLayout(blockLayout::layoutInlineBlock);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(10, inlineBlock.inlineFragments().get(0).x());
    assertEquals(20, inlineBlock.inlineFragments().get(0).width());
    assertEquals(20, inlineBlock.box().content().width());
    assertEquals(30, right.inlineFragments().get(0).x());
    assertEquals(10, wrapper.box().content().height());
  }

  @Test
  void layout_whenInlineBlockBaselineMovesText_usesFullInlineRowHeightForFollowingBlock() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(200, 200);
    style(frame, 0);
    Element wrapper = NodeBuilder.div();
    style(wrapper, 0);
    Text left = NodeBuilder.text("a");
    Element inlineBlock = NodeBuilder.div(NodeBuilder.text("bb"));
    style(inlineBlock, 3);
    inlineBlock.resolvedStyle().display(Display.INLINE_BLOCK);
    inlineBlock.resolvedStyle().paddingTop(Length.pixel(4));
    inlineBlock.resolvedStyle().paddingRight(Length.pixel(8));
    inlineBlock.resolvedStyle().paddingBottom(Length.pixel(4));
    inlineBlock.resolvedStyle().paddingLeft(Length.pixel(8));
    Text right = NodeBuilder.text("c");
    Element followingBlock = NodeBuilder.div();
    style(followingBlock, 5);
    wrapper.addChildren(left, inlineBlock, right, followingBlock);
    frame.addChild(wrapper);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    InlineFormattingContext inlineFormattingContext = new InlineFormattingContext(textMeasurer);
    BlockLayout blockLayout =
        new BlockLayout(layoutService, inlineFormattingContext, textMeasurer);
    inlineFormattingContext.inlineBlockLayout(blockLayout::layoutInlineBlock);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(0, inlineBlock.inlineFragments().get(0).y());
    assertEquals(14, left.inlineFragments().get(0).y());
    assertEquals(14, right.inlineFragments().get(0).y());
    assertEquals(24, followingBlock.box().borderBox().y());
    assertEquals(34, wrapper.box().content().height());
  }

  @Test
  void layout_whenButtonInputHasAutoSize_usesValueDerivedWidthAndMeasuredLineHeight() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input(NodeBuilder.TYPE_BUTTON, "action", "Save");
    style(input, 2);
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(40, input.box().content().width());
    assertEquals(10, input.box().content().height());
    assertEquals(44, input.box().borderBox().width());
    assertEquals(14, input.box().borderBox().height());
  }

  @Test
  void layout_whenButtonInputHasEmptyValue_usesFallbackWidth() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input(NodeBuilder.TYPE_BUTTON, "action", "");
    style(input, 2);
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(64, input.box().content().width());
    assertEquals(68, input.box().borderBox().width());
    assertEquals(14, input.box().borderBox().height());
  }

  @Test
  void layout_whenButtonInputHasStyledSize_usesStyledSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input(NodeBuilder.TYPE_BUTTON, "action", "Save");
    style(input, 2);
    input.resolvedStyle().width(Length.pixel(120));
    input.resolvedStyle().height(Length.pixel(24));
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(116, input.box().content().width());
    assertEquals(20, input.box().content().height());
    assertEquals(120, input.box().borderBox().width());
    assertEquals(24, input.box().borderBox().height());
  }

  @Test
  void layout_whenButtonInputHasAutoSize_respectsMinAndMaxConstraints() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input(NodeBuilder.TYPE_BUTTON, "action", "LongLabel");
    style(input, 2);
    input.resolvedStyle().minWidth(Length.pixel(40));
    input.resolvedStyle().maxWidth(Length.pixel(80));
    input.resolvedStyle().minHeight(Length.pixel(8));
    input.resolvedStyle().maxHeight(Length.pixel(12));
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(76, input.box().content().width());
    assertEquals(8, input.box().content().height());
    assertEquals(80, input.box().borderBox().width());
    assertEquals(12, input.box().borderBox().height());
  }

  @Test
  void layout_whenTextInputValueLooksLikeButtonLabel_stillUsesTextInputDefaultWidth() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input(NodeBuilder.TYPE_TEXT, "action", "Save");
    style(input, 2);
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(156, input.box().content().width());
    assertEquals(160, input.box().borderBox().width());
  }

  @Test
  void layout_whenTextInputIsLaidOut_updatesScrollAndClientSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    InputElement input = NodeBuilder.input();
    style(input, 2);
    frame.addChild(input);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    layoutService.layout(frame);

    assertEquals(160, frame.scrollWidth());
    assertEquals(14, frame.scrollHeight());
    assertEquals(300, frame.clientWidth());
    assertEquals(200, frame.clientHeight());
    assertEquals(156, input.clientWidth());
    assertEquals(10, input.clientHeight());
  }

  @Test
  void layout_whenTextareaHasAutoSize_usesDefaultRowsAndColumns() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    TextareaElement textarea = NodeBuilder.textarea();
    style(textarea, 2);
    frame.addChild(textarea);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(200, textarea.box().content().width());
    assertEquals(20, textarea.box().content().height());
    assertEquals(204, textarea.box().borderBox().width());
    assertEquals(24, textarea.box().borderBox().height());
  }

  @Test
  void layout_whenTextareaHasRowsAndCols_usesAttributeDerivedAutoSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    TextareaElement textarea =
        NodeBuilder.textarea(NodeBuilder.attrs(NodeBuilder.rows("3"), NodeBuilder.cols("5")), "");
    style(textarea, 2);
    frame.addChild(textarea);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(50, textarea.box().content().width());
    assertEquals(30, textarea.box().content().height());
    assertEquals(54, textarea.box().borderBox().width());
    assertEquals(34, textarea.box().borderBox().height());
  }

  @Test
  void layout_whenTextareaHasStyledSize_usesStyledSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    TextareaElement textarea = NodeBuilder.textarea();
    style(textarea, 2);
    textarea.resolvedStyle().width(Length.pixel(120));
    textarea.resolvedStyle().height(Length.pixel(44));
    frame.addChild(textarea);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(116, textarea.box().content().width());
    assertEquals(40, textarea.box().content().height());
    assertEquals(120, textarea.box().borderBox().width());
    assertEquals(44, textarea.box().borderBox().height());
  }

  @Test
  void layout_whenButtonHasTextContent_usesContentDerivedAutoSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    ButtonElement button = NodeBuilder.button(NodeBuilder.text("Save"));
    style(button, 2);
    button.resolvedStyle().display(Display.INLINE_BLOCK);
    frame.addChild(button);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());
    blockLayout.layoutInlineBlock(button, frame);

    assertEquals(40, button.box().content().width());
    assertEquals(10, button.box().content().height());
    assertEquals(44, button.box().borderBox().width());
    assertEquals(14, button.box().borderBox().height());
  }

  @Test
  void layout_whenButtonAutoWidthDependsOnDisplay_blockFillsAndInlineBlockStaysIntrinsic() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    ButtonElement blockButton = NodeBuilder.button(NodeBuilder.text("Save"));
    style(blockButton, 2);
    frame.addChild(blockButton);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());
    assertEquals(300, blockButton.box().borderBox().width());

    ButtonElement inlineBlockButton = NodeBuilder.button(NodeBuilder.text("Save"));
    style(inlineBlockButton, 2);
    inlineBlockButton.resolvedStyle().display(Display.INLINE_BLOCK);
    frame.addChild(inlineBlockButton);
    blockLayout.layoutInlineBlock(inlineBlockButton, frame);

    assertEquals(44, inlineBlockButton.box().borderBox().width());
  }

  @Test
  void layout_whenButtonHasNestedInlineContent_usesNestedTextForAutoWidth() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    Element span = new Element("span");
    style(span, 0);
    span.resolvedStyle().display(Display.INLINE);
    span.addChild(NodeBuilder.text("Go"));
    ButtonElement button = NodeBuilder.button(span);
    style(button, 2);
    button.resolvedStyle().display(Display.INLINE_BLOCK);
    frame.addChild(button);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());
    blockLayout.layoutInlineBlock(button, frame);

    assertEquals(20, button.box().content().width());
    assertEquals(10, button.box().content().height());
    assertEquals(24, button.box().borderBox().width());
  }

  @Test
  void layout_whenButtonHasStyledSize_usesStyledSize() {
    Frame frame = NodeBuilder.frame();
    frame.frameSize(300, 200);
    style(frame, 0);
    ButtonElement button = NodeBuilder.button(NodeBuilder.text("Save"));
    style(button, 2);
    button.resolvedStyle().width(Length.pixel(120));
    button.resolvedStyle().height(Length.pixel(24));
    frame.addChild(button);

    RecursiveLayoutService layoutService = new RecursiveLayoutService();
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer();
    BlockLayout blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(textMeasurer), textMeasurer);
    layoutService.blockLayout(blockLayout);

    blockLayout.layout(frame, new LayoutContext());

    assertEquals(116, button.box().content().width());
    assertEquals(20, button.box().content().height());
    assertEquals(120, button.box().borderBox().width());
    assertEquals(24, button.box().borderBox().height());
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
    public LayoutResult layout(@NonNull Frame frame) {
      layoutNode(frame, new LayoutContext());
      updateScrollAndClientSize(frame);
      return LayoutResult.converged(1);
    }

    @Override public void resolveTransforms(@NonNull Frame frame) {}

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

    private void updateScrollAndClientSize(Element element) {
      float scrollWidth = 0;
      float scrollHeight = 0;
      for (Node node : element.childNodes()) {
        scrollWidth =
            Math.max(scrollWidth, node.box().marginBox().x() + node.box().marginBox().width());
        scrollHeight =
            Math.max(
                scrollHeight, node.box().marginBox().y() + node.box().marginBox().height());
      }
      element.scrollWidth(scrollWidth);
      element.scrollHeight(scrollHeight);
      element.clientWidth(element.box().content().width());
      element.clientHeight(element.box().content().height());
      element
          .childNodes()
          .stream()
          .filter(Element.class::isInstance)
          .map(Element.class::cast)
          .forEach(this::updateScrollAndClientSize);
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
