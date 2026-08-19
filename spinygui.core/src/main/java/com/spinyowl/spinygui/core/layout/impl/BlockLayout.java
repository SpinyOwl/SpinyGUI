package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.findPositionedAncestor;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.getChildNodesHeight;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.setBorders;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.setPadding;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLength;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLengthOptional;

import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Edges;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.util.ScrollbarGeometry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.NonNull;

public class BlockLayout implements ElementLayout {
  private static final float DEFAULT_TEXT_INPUT_WIDTH = 160f;
  private static final float DEFAULT_BUTTON_INPUT_WIDTH = 64f;
  private static final float DEFAULT_TOGGLE_INPUT_SIZE = 18f;
  private static final float DEFAULT_RANGE_INPUT_WIDTH = 160f;
  private static final float DEFAULT_RANGE_INPUT_HEIGHT = 18f;
  private static final int DEFAULT_TEXTAREA_COLS = 20;
  private static final int DEFAULT_TEXTAREA_ROWS = 2;
  private static final String TEXTAREA_COLUMN_WIDTH_SAMPLE =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  @NonNull private final LayoutService layoutService;
  @NonNull private final InlineFormattingContext inlineFormattingContext;
  private final TextMeasurer textMeasurer;

  public BlockLayout(
      @NonNull LayoutService layoutService,
      @NonNull InlineFormattingContext inlineFormattingContext) {
    this(layoutService, inlineFormattingContext, null);
  }

  public BlockLayout(
      @NonNull LayoutService layoutService,
      @NonNull InlineFormattingContext inlineFormattingContext,
      TextMeasurer textMeasurer) {
    this.layoutService = layoutService;
    this.inlineFormattingContext = inlineFormattingContext;
    this.textMeasurer = textMeasurer;
  }

  @Override
  public void layout(Element element, LayoutContext context) {
    layout(element, false, context);
  }

  public void layout(Element element, boolean skipChildren, LayoutContext ctx) {
    if (shouldSkip(element)) {
      return;
    }

    Box parentBox = getParentDimensions(element, element.parent());
    ResolvedStyle style = element.resolvedStyle();

    setBorders(style, element.box().border());
    setPadding(
        parentBox.content().width(), parentBox.content().height(), style, element.box().padding());

    Position elementPosition = element.resolvedStyle().position();
    if (Position.STATIC.equals(elementPosition)) {
      layoutStaticBlock(element, parentBox, style, skipChildren, ctx);
    } else if (Position.ABSOLUTE.equals(elementPosition)) {
      layoutAbsoluteBlock(element, parentBox, style, skipChildren, ctx);
    } else if (Position.RELATIVE.equals(elementPosition)) {
      layoutRelativeBlock(element, parentBox, style, skipChildren, ctx);
    }
  }

  void layoutInlineBlock(Element element, Element formattingParent) {
    layout(element, false, new LayoutContext());
    shrinkWrapInlineBlock(element);
  }

  private void layoutStaticBlock(
      Element e, Box parentBox, ResolvedStyle style, boolean skipChildren, LayoutContext ctx) {

    Box box = e.box();
    Edges padding = box.padding();
    Edges border = box.border();
    Edges margin = box.margin();

    float contentX =
        parentBox.border().left()
            + Math.max(parentBox.padding().left(), margin.left())
            + border.left()
            + padding.left();

    Float blockBottomY = ctx.lastBlockBottomY();
    float contentY =
        border.top()
            + padding.top()
            + (blockBottomY != null
                ? blockBottomY
                : Math.max(parentBox.padding().top(), margin.top()) + parentBox.border().top());

    box.contentPosition(contentX, contentY);

    float verticalAdditions = border.top() + border.bottom() + padding.top() + padding.bottom();
    float horizontalAdditions = border.left() + border.right() + padding.left() + padding.right();

    float contentWidth;
    if (e instanceof Frame frame) {
      contentWidth = frame.frameSize().x;
    } else {
      contentWidth =
          getElementWidth(e, style, parentBox.content().width(), horizontalAdditions);
    }
    contentWidth -= horizontalAdditions;
    box.content().width(Math.max(0f, contentWidth));

    if (e instanceof Frame frame) {
      box.content().height(Math.max(0f, frame.frameSize().y - verticalAdditions));
      if (!skipChildren) {
        layoutService.layoutChildNodes(e, ctx);
      }
      finishBlockFlow(e, ctx);
      return;
    }

    if (!style.height().isAuto()) {
      float borderBoxHeight = getHeight(parentBox.content().height(), verticalAdditions, style);
      box.content().height(Math.max(0f, borderBoxHeight - verticalAdditions));
      if (!skipChildren) {
        layoutFlowChildren(e);
      }
      finishBlockFlow(e, ctx);
      return;
    }

    float childrenHeight =
        e instanceof InputElement || e instanceof TextareaElement
            ? 0f
            : childrenHeight(e, style, skipChildren, ctx);
    float borderBoxHeight =
        getElementHeight(
            e,
            style,
            parentBox.content().height(),
            childrenHeight,
            verticalAdditions);
    box.content().height(Math.max(0f, borderBoxHeight - verticalAdditions));
    finishBlockFlow(e, ctx);
  }

  private void finishBlockFlow(Element e, LayoutContext ctx) {
    ctx.lastTextEndY(null);
    ctx.previousNode(e);
    ctx.lastBlockBottomY(e.box().borderBox().y() + e.box().borderBox().height());
  }

  private void layoutAbsoluteBlock(
      Element e, Box parentBox, ResolvedStyle style, boolean skipChildren, LayoutContext ctx) {
    Element ancestor = findPositionedAncestor(e);

    float verticalAdditions =
        e.box().border().top()
            + e.box().padding().top()
            + e.box().border().bottom()
            + e.box().padding().bottom();
    float horizontalAdditions =
        e.box().border().left()
            + e.box().border().right()
            + e.box().padding().left()
            + e.box().padding().right();

    calculateHorizontalPositionAndWidth(
        e, parentBox, style, ancestor.box(), e.box(), horizontalAdditions);

    if (stretchesVertically(style)) {
      layoutVerticallyStretchedAbsoluteBlock(e, ancestor, style, verticalAdditions);
      if (!skipChildren) {
        layoutFlowChildren(e);
      }
      return;
    }

    float childrenHeight = childrenHeight(e, style, skipChildren, ctx);

    float contentY;
    float borderBoxHeight;
    if (style.top().isAuto() && style.bottom().isAuto()) {
      float parentPaddingBoxHeight = parentBox.paddingBox().height();
      float parentOffset = parentBox.content().y();
      contentY = getAutoVerticalContentY(ctx, e.box().border(), e.box().padding(), parentOffset);
      borderBoxHeight =
          getElementHeight(
              e, style, parentPaddingBoxHeight, childrenHeight, verticalAdditions);
    } else {
      float parentPaddingBoxHeight =
          ancestor.box().padding().top()
              + ancestor.box().padding().bottom()
              + ancestor.box().content().height();

      contentY = ancestor.box().border().top() + e.box().border().top() + e.box().padding().top();
      float bottom = contentY + parentPaddingBoxHeight;

      if (style.top().isLength()) {
        contentY += getFloatLength(style.top(), parentPaddingBoxHeight);
      }
      if (style.bottom().isLength()) {
        bottom =
            ancestor.box().border().top()
                + ancestor.box().paddingBox().height()
                - getFloatLength(style.bottom(), parentPaddingBoxHeight);
      }

      if (style.bottom().isLength() && style.top().isLength()) {
        borderBoxHeight =
            getBorderBoxHeight(
                e,
                style,
                verticalAdditions,
                childrenHeight,
                contentY,
                parentPaddingBoxHeight,
                bottom);
      } else {
        borderBoxHeight =
            getElementHeight(
                e, style, parentPaddingBoxHeight, childrenHeight, verticalAdditions);

        if (style.bottom().isLength()) {
          contentY = bottom - borderBoxHeight + e.box().border().top() + e.box().padding().top();
        }
      }
    }

    e.box().content().y(contentY);
    e.box().content().height(Math.max(0f, borderBoxHeight - verticalAdditions));
  }

  private boolean stretchesVertically(ResolvedStyle style) {
    return style.height().isAuto() && style.top().isLength() && style.bottom().isLength();
  }

  private void layoutVerticallyStretchedAbsoluteBlock(
      Element e, Element ancestor, ResolvedStyle style, float verticalAdditions) {
    float parentPaddingBoxHeight =
        ancestor.box().padding().top()
            + ancestor.box().padding().bottom()
            + ancestor.box().content().height();

    float contentY =
        ancestor.box().border().top()
            + e.box().border().top()
            + e.box().padding().top()
            + getFloatLength(style.top(), parentPaddingBoxHeight);
    float bottom =
        ancestor.box().border().top()
            + ancestor.box().paddingBox().height()
            - getFloatLength(style.bottom(), parentPaddingBoxHeight);
    float borderBoxHeight =
        getBorderBoxHeight(
            e, style, verticalAdditions, 0f, contentY, parentPaddingBoxHeight, bottom);

    e.box().content().y(contentY);
    e.box().content().height(Math.max(0f, borderBoxHeight - verticalAdditions));
  }

  private float getBorderBoxHeight(
      Element e,
      ResolvedStyle style,
      float verticalAdditions,
      float childrenHeight,
      float contentY,
      float parentPaddingBoxHeight,
      float bottom) {
    float borderBoxHeight;
    if (style.height().isAuto()) {
      borderBoxHeight = bottom - contentY + e.box().padding().top() + e.box().border().top();
    } else {
      borderBoxHeight =
          getElementHeight(
              e, style, parentPaddingBoxHeight, childrenHeight, verticalAdditions);
    }
    return borderBoxHeight;
  }

  private static float getAutoVerticalContentY(
      LayoutContext ctx, Edges border, Edges padding, float parentOffset) {
    float contentY = parentOffset + border.top() + padding.top();
    Float blockBottomY = ctx.lastBlockBottomY();
    if (blockBottomY != null) {
      contentY = blockBottomY + border.top();
    }
    return contentY;
  }

  private void calculateHorizontalPositionAndWidth(
      Element element,
      Box parentBox,
      ResolvedStyle style,
      Box ancestorBox,
      Box box,
      float horizontalAdditions) {
    float contentX;
    float contentWidth;
    if (style.left().isAuto() && style.right().isAuto()) {
      float parentOffset = parentBox.content().x();
      contentX = parentOffset + box.border().left() + box.padding().left();

      float parentPaddingBoxWidth =
          Math.max(
              parentBox.paddingBox().width(),
              ancestorBox.paddingBox().width() - parentOffset + ancestorBox.border().right());

      contentWidth = getElementWidth(element, style, parentPaddingBoxWidth, horizontalAdditions);
    } else {
      float parentPaddingBoxWidth = ancestorBox.paddingBox().width();
      float left = box.border().left() + box.padding().left() + ancestorBox.border().left();
      float right = left + parentPaddingBoxWidth;
      if (style.left().isLength()) {
        left += getFloatLength(style.left(), parentPaddingBoxWidth);
      }
      if (style.right().isLength()) {
        right -= getFloatLength(style.right(), parentPaddingBoxWidth);
      }

      if (style.left().isLength() && style.right().isLength()) {
        contentX = left;
        contentWidth = right - left;
      } else {
        contentWidth = getElementWidth(element, style, parentPaddingBoxWidth, horizontalAdditions);
        if (style.left().isLength()) {
          contentX = left;
        } else {
          contentX = right - contentWidth;
        }
      }
    }

    contentWidth -= horizontalAdditions;
    box.content().x(contentX);
    box.content().width(Math.max(0f, contentWidth));
  }

  private void layoutRelativeBlock(
      Element element,
      Box parentBox,
      ResolvedStyle style,
      boolean skipChildren,
      LayoutContext context) {
    Box box = element.box();
    layoutStaticBlock(element, parentBox, style, skipChildren, context);
    float x = box.content().x();
    float y = box.content().y();

    if (!style.left().isAuto()) {
      x += getFloatLength(style.left(), parentBox.content().width());
    } else if (!style.right().isAuto()) {
      x -= getFloatLength(style.right(), parentBox.content().width());
    }

    if (!style.top().isAuto()) {
      y += getFloatLength(style.top(), parentBox.content().height());
    } else if (!style.bottom().isAuto()) {
      y -= getFloatLength(style.bottom(), parentBox.content().height());
    }
    box.contentPosition(x, y);
  }

  private Box getParentDimensions(Element element, Element parent) {
    Box parentBox;
    if (element instanceof Frame frame) {
      parentBox = new Box();
      parentBox.contentSize(frame.frameSize().x, frame.frameSize().y);
    } else if (parent == null) {
      parentBox = new Box();
      var frame = element.frame();
      parentBox.contentSize(frame.frameSize().x, frame.frameSize().y);
    } else {
      parentBox = parent.box();
      ScrollbarGeometry.Metrics scrollbarMetrics = parent.scrollbarMetrics();
      if (scrollbarMetrics != null
          && (scrollbarMetrics.verticalVisible() || scrollbarMetrics.horizontalVisible())) {
        parentBox = copyWithClientSize(parentBox, scrollbarMetrics);
      }
    }
    return parentBox;
  }

  private Box copyWithClientSize(Box source, ScrollbarGeometry.Metrics scrollbarMetrics) {
    Box copy = new Box();
    copy.contentPosition(source.content().x(), source.content().y());
    copy.contentSize(scrollbarMetrics.clientWidth(), scrollbarMetrics.clientHeight());
    copyEdges(source.padding(), copy.padding());
    copyEdges(source.border(), copy.border());
    copyEdges(source.margin(), copy.margin());
    return copy;
  }

  private void copyEdges(Edges source, Edges destination) {
    destination.top(source.top());
    destination.right(source.right());
    destination.bottom(source.bottom());
    destination.left(source.left());
  }

  private float childrenHeight(
      Element element, ResolvedStyle style, boolean skipChildren, LayoutContext context) {
    float childrenHeight = 0;
    Unit height = style.height();
    if (!skipChildren) {
      layoutFlowChildren(element);
    }
    if ((style.display().equals(Display.BLOCK)
            || style.display().equals(Display.INLINE_BLOCK)
            || style.display().equals(Display.GRID))
        && height.isAuto()
        && !skipChildren) {
      childrenHeight = getChildNodesHeight(element);
    }
    return childrenHeight;
  }

  private void shrinkWrapInlineBlock(Element element) {
    ResolvedStyle style = element.resolvedStyle();
    if (!style.width().isAuto()
        || element instanceof InputElement
        || element instanceof ButtonElement
        || element instanceof TextareaElement) {
      return;
    }

    float contentWidth = 0;
    for (Node child : element.childNodes()) {
      if (child instanceof Element childElement
          && Display.NONE.equals(childElement.resolvedStyle().display())) {
        continue;
      }
      contentWidth =
          Math.max(contentWidth, child.box().marginBox().x() + child.box().marginBox().width());
    }
    element.box().content().width(contentWidth);
  }

  private float getElementWidth(
      Element element, ResolvedStyle style, float parentWidth, float horizontalAdditions) {
    if (element instanceof InputElement input) {
      if (input.textInput()) {
        return getTextInputWidth(style, parentWidth);
      }
      if (input.buttonInput()) {
        return getButtonInputWidth(input, style, parentWidth, horizontalAdditions);
      }
      if (input.toggleInput()) {
        return getIntrinsicInputWidth(
            style, parentWidth, horizontalAdditions, DEFAULT_TOGGLE_INPUT_SIZE);
      }
      if (input.rangeInput()) {
        return getIntrinsicInputWidth(
            style, parentWidth, horizontalAdditions, DEFAULT_RANGE_INPUT_WIDTH);
      }
    }
    if (element instanceof ButtonElement button) {
      return getButtonWidth(button, style, parentWidth, horizontalAdditions);
    }
    if (element instanceof TextareaElement textarea) {
      return getTextareaWidth(textarea, style, parentWidth, horizontalAdditions);
    }
    return getWidth(parentWidth, style);
  }

  private float getElementHeight(
      Element element,
      ResolvedStyle style,
      float parentHeight,
      float childrenHeight,
      float verticalAdditions) {
    if (element instanceof InputElement input) {
      if (input.textInput()) {
        return getTextInputHeight(input, style, parentHeight, verticalAdditions);
      }
      if (input.buttonInput()) {
        return getButtonInputHeight(input, style, parentHeight, verticalAdditions);
      }
      if (input.toggleInput()) {
        return getIntrinsicInputHeight(
            style, parentHeight, verticalAdditions, DEFAULT_TOGGLE_INPUT_SIZE);
      }
      if (input.rangeInput()) {
        return getIntrinsicInputHeight(
            style, parentHeight, verticalAdditions, DEFAULT_RANGE_INPUT_HEIGHT);
      }
    }
    if (element instanceof ButtonElement button) {
      return getButtonHeight(
          button, style, parentHeight, childrenHeight, verticalAdditions);
    }
    if (element instanceof TextareaElement textarea) {
      return getTextareaHeight(textarea, style, parentHeight, verticalAdditions);
    }
    return getHeight(parentHeight, childrenHeight + verticalAdditions, style);
  }

  private float getWidth(float parentWidth, ResolvedStyle style) {
    Optional<Float> width = getFloatLengthOptional(style.width(), parentWidth);
    Optional<Float> minWidth = getFloatLengthOptional(style.minWidth(), parentWidth);
    Optional<Float> maxWidth = getFloatLengthOptional(style.maxWidth(), parentWidth);

    float w = width.orElse(parentWidth);
    w = Math.max(w, minWidth.orElse(w));
    w = Math.min(w, maxWidth.orElse(w));
    return w;
  }

  private float getTextInputWidth(ResolvedStyle style, float parentWidth) {
    float width = style.width().isAuto() ? DEFAULT_TEXT_INPUT_WIDTH : getWidth(parentWidth, style);
    Optional<Float> minWidth = getFloatLengthOptional(style.minWidth(), parentWidth);
    Optional<Float> maxWidth = getFloatLengthOptional(style.maxWidth(), parentWidth);
    width = Math.max(width, minWidth.orElse(width));
    width = Math.min(width, maxWidth.orElse(width));
    return width;
  }

  private float getTextInputHeight(
      Element element, ResolvedStyle style, float parentHeight, float verticalAdditions) {
    if (!style.height().isAuto()) {
      return getHeight(parentHeight, verticalAdditions, style);
    }
    float lineHeight = measureTextInputLineHeight(element, style);
    float borderBoxHeight = lineHeight + verticalAdditions;
    Optional<Float> minHeight = getFloatLengthOptional(style.minHeight(), parentHeight);
    Optional<Float> maxHeight = getFloatLengthOptional(style.maxHeight(), parentHeight);
    borderBoxHeight = Math.max(borderBoxHeight, minHeight.orElse(borderBoxHeight));
    borderBoxHeight = Math.min(borderBoxHeight, maxHeight.orElse(borderBoxHeight));
    return borderBoxHeight;
  }

  private float getButtonWidth(
      ButtonElement button, ResolvedStyle style, float parentWidth, float horizontalAdditions) {
    float width =
        style.width().isAuto()
            ? Display.BLOCK.equals(style.display())
                ? parentWidth
                : measureButtonContentWidth(button, style) + horizontalAdditions
            : getWidth(parentWidth, style);
    Optional<Float> minWidth = getFloatLengthOptional(style.minWidth(), parentWidth);
    Optional<Float> maxWidth = getFloatLengthOptional(style.maxWidth(), parentWidth);
    width = Math.max(width, minWidth.orElse(width));
    width = Math.min(width, maxWidth.orElse(width));
    return width;
  }

  private float getButtonHeight(
      ButtonElement button,
      ResolvedStyle style,
      float parentHeight,
      float childrenHeight,
      float verticalAdditions) {
    if (!style.height().isAuto()) {
      return getHeight(parentHeight, verticalAdditions, style);
    }
    float contentHeight = Math.max(childrenHeight, measureTextInputLineHeight(button, style));
    float borderBoxHeight = contentHeight + verticalAdditions;
    Optional<Float> minHeight = getFloatLengthOptional(style.minHeight(), parentHeight);
    Optional<Float> maxHeight = getFloatLengthOptional(style.maxHeight(), parentHeight);
    borderBoxHeight = Math.max(borderBoxHeight, minHeight.orElse(borderBoxHeight));
    borderBoxHeight = Math.min(borderBoxHeight, maxHeight.orElse(borderBoxHeight));
    return borderBoxHeight;
  }

  private float getButtonInputWidth(
      InputElement input, ResolvedStyle style, float parentWidth, float horizontalAdditions) {
    float width =
        style.width().isAuto()
            ? measureButtonInputValueWidth(input, style) + horizontalAdditions
            : getWidth(parentWidth, style);
    Optional<Float> minWidth = getFloatLengthOptional(style.minWidth(), parentWidth);
    Optional<Float> maxWidth = getFloatLengthOptional(style.maxWidth(), parentWidth);
    width = Math.max(width, minWidth.orElse(width));
    width = Math.min(width, maxWidth.orElse(width));
    return width;
  }

  private float getButtonInputHeight(
      InputElement input, ResolvedStyle style, float parentHeight, float verticalAdditions) {
    if (!style.height().isAuto()) {
      return getHeight(parentHeight, verticalAdditions, style);
    }
    float borderBoxHeight = measureTextInputLineHeight(input, style) + verticalAdditions;
    Optional<Float> minHeight = getFloatLengthOptional(style.minHeight(), parentHeight);
    Optional<Float> maxHeight = getFloatLengthOptional(style.maxHeight(), parentHeight);
    borderBoxHeight = Math.max(borderBoxHeight, minHeight.orElse(borderBoxHeight));
    borderBoxHeight = Math.min(borderBoxHeight, maxHeight.orElse(borderBoxHeight));
    return borderBoxHeight;
  }

  private float getIntrinsicInputWidth(
      ResolvedStyle style,
      float parentWidth,
      float horizontalAdditions,
      float intrinsicContentWidth) {
    float width =
        style.width().isAuto()
            ? intrinsicContentWidth + horizontalAdditions
            : getWidth(parentWidth, style);
    Optional<Float> minWidth = getFloatLengthOptional(style.minWidth(), parentWidth);
    Optional<Float> maxWidth = getFloatLengthOptional(style.maxWidth(), parentWidth);
    width = Math.max(width, minWidth.orElse(width));
    width = Math.min(width, maxWidth.orElse(width));
    return width;
  }

  private float getIntrinsicInputHeight(
      ResolvedStyle style,
      float parentHeight,
      float verticalAdditions,
      float intrinsicContentHeight) {
    if (!style.height().isAuto()) {
      return getHeight(parentHeight, verticalAdditions, style);
    }
    float borderBoxHeight = intrinsicContentHeight + verticalAdditions;
    Optional<Float> minHeight = getFloatLengthOptional(style.minHeight(), parentHeight);
    Optional<Float> maxHeight = getFloatLengthOptional(style.maxHeight(), parentHeight);
    borderBoxHeight = Math.max(borderBoxHeight, minHeight.orElse(borderBoxHeight));
    borderBoxHeight = Math.min(borderBoxHeight, maxHeight.orElse(borderBoxHeight));
    return borderBoxHeight;
  }

  private float measureButtonInputValueWidth(InputElement input, ResolvedStyle style) {
    String text = input.value();
    if (text.isEmpty()) {
      return DEFAULT_BUTTON_INPUT_WIDTH;
    }
    if (textMeasurer == null) {
      return text.length() * StyleUtils.getFontSize(input) * 0.8f;
    }
    return textMeasurer
        .getTextLineMetrics(
            text, findFonts(style), StyleUtils.getFontSize(input), style.lineHeight())
        .width();
  }

  private float measureButtonContentWidth(ButtonElement button, ResolvedStyle style) {
    String text = buttonTextContent(button);
    if (textMeasurer == null) {
      return text.length() * StyleUtils.getFontSize(button) * 0.8f;
    }
    return textMeasurer
        .getTextLineMetrics(
            text, findFonts(style), StyleUtils.getFontSize(button), style.lineHeight())
        .width();
  }

  private float getTextareaWidth(
      TextareaElement textarea, ResolvedStyle style, float parentWidth, float horizontalAdditions) {
    float width =
        style.width().isAuto()
            ? measureTextareaColumnWidth(textarea, style)
                    * intAttribute(textarea, "cols", DEFAULT_TEXTAREA_COLS)
                + horizontalAdditions
            : getWidth(parentWidth, style);
    Optional<Float> minWidth = getFloatLengthOptional(style.minWidth(), parentWidth);
    Optional<Float> maxWidth = getFloatLengthOptional(style.maxWidth(), parentWidth);
    width = Math.max(width, minWidth.orElse(width));
    width = Math.min(width, maxWidth.orElse(width));
    return width;
  }

  private float getTextareaHeight(
      TextareaElement textarea, ResolvedStyle style, float parentHeight, float verticalAdditions) {
    if (!style.height().isAuto()) {
      return getHeight(parentHeight, verticalAdditions, style);
    }
    float lineHeight = measureTextInputLineHeight(textarea, style);
    float borderBoxHeight =
        lineHeight * intAttribute(textarea, "rows", DEFAULT_TEXTAREA_ROWS) + verticalAdditions;
    Optional<Float> minHeight = getFloatLengthOptional(style.minHeight(), parentHeight);
    Optional<Float> maxHeight = getFloatLengthOptional(style.maxHeight(), parentHeight);
    borderBoxHeight = Math.max(borderBoxHeight, minHeight.orElse(borderBoxHeight));
    borderBoxHeight = Math.min(borderBoxHeight, maxHeight.orElse(borderBoxHeight));
    return borderBoxHeight;
  }

  private float measureTextareaColumnWidth(TextareaElement textarea, ResolvedStyle style) {
    float fontSize = StyleUtils.getFontSize(textarea);
    float lineHeight = style.lineHeight();
    if (textMeasurer == null) {
      return fontSize * 0.8f;
    }
    return textMeasurer
            .getTextLineMetrics(
                TEXTAREA_COLUMN_WIDTH_SAMPLE, findFonts(style), fontSize, lineHeight)
            .width()
        / TEXTAREA_COLUMN_WIDTH_SAMPLE.length();
  }

  private int intAttribute(Element element, String name, int fallback) {
    try {
      return Math.max(1, Integer.parseInt(element.getAttribute(name)));
    } catch (RuntimeException ignored) {
      return fallback;
    }
  }

  private float measureTextInputLineHeight(Element element, ResolvedStyle style) {
    float fontSize = StyleUtils.getFontSize(element);
    float lineHeight = style.lineHeight();
    if (textMeasurer == null) {
      return fontSize * lineHeight;
    }
    return textMeasurer.getTextLineMetrics("", findFonts(style), fontSize, lineHeight).height();
  }

  private String buttonTextContent(Node node) {
    if (node instanceof Text text) {
      return text.content();
    }
    if (node instanceof Element element) {
      return element.childNodes().stream()
          .map(this::buttonTextContent)
          .collect(Collectors.joining());
    }
    return "";
  }

  private List<Font> findFonts(ResolvedStyle style) {
    if (textMeasurer != null) {
      textMeasurer.diagnostics().increment(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS);
    }
    return Font.semanticOwner().resolver()
        .resolve(
            style.fontFamilies(), style.fontStyle(), style.fontWeight(), FontStretch.NORMAL);
  }

  private float getHeight(float parentHeight, float borderBoxHeight, ResolvedStyle style) {
    Optional<Float> height;
    if (!style.height().isAuto()) {
      height = getFloatLengthOptional(style.height(), parentHeight);
    } else {
      height = Optional.empty();
    }
    Optional<Float> minHeight = getFloatLengthOptional(style.minHeight(), parentHeight);
    Optional<Float> maxHeight = getFloatLengthOptional(style.maxHeight(), parentHeight);

    float h = height.orElse(borderBoxHeight);
    h = Math.max(h, minHeight.orElse(h));
    h = Math.min(h, maxHeight.orElse(h));
    return h;
  }

  private boolean shouldSkip(Element element) {
    return element.frame() == null || (element.parent() == null && !(element instanceof Frame));
  }

  private void layoutFlowChildren(Element element) {
    LayoutContext context = new LayoutContext();
    List<Node> inlineNodes = new ArrayList<>();
    for (Node child : element.childNodes()) {
      if (inlineFormattingContext.inlineNode(child)) {
        inlineNodes.add(child);
      } else {
        flushInlineNodes(element, context, inlineNodes);
        layoutService.layoutNode(child, context);
      }
    }
    flushInlineNodes(element, context, inlineNodes);
  }

  private void flushInlineNodes(Element element, LayoutContext context, List<Node> inlineNodes) {
    if (inlineNodes.isEmpty()) {
      return;
    }
    float contentStart = element.box().border().top() + element.box().padding().top();
    float startY =
        context.lastBlockBottomY() == null
            ? 0
            : Math.max(0, context.lastBlockBottomY() - contentStart);
    float height = inlineFormattingContext.layout(element, inlineNodes, startY);
    context.lastBlockBottomY(contentStart + startY + height);
    inlineNodes.clear();
  }

  private void applyPadding(
      PixelLength borderWidth, BorderStyle borderStyle, Consumer<Float> borderConsumer) {
    if (borderWidth != null && !BorderStyle.NONE.equals(borderStyle)) {
      borderConsumer.accept(borderWidth.convert());
    }
  }
}
