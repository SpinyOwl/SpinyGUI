package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.findPositionedAncestor;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.getChildNodesHeight;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.getContentX;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.getContentY;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.setPadding;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLength;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLengthOptional;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.layout.Dimensions;
import com.spinyowl.spinygui.core.node.layout.Edges;
import com.spinyowl.spinygui.core.style.CalculatedStyle;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BlockLayout implements Layout<Element> {

  @NonNull private final LayoutService layoutService;

  /**
   * Used to lay out element and it's child nodes.
   *
   * @param element element to lay out.
   */
  @Override
  public void layout(Element element, LayoutContext context) {
    layout(element, false, context);
  }

  public void layout(Element element, boolean skipChildren, LayoutContext context) {
    if (shouldSkip(element)) {
      return;
    }

    Dimensions parentDimensions = getParentDimensions(element, element.parent());

    Dimensions dimensions = element.dimensions();
    CalculatedStyle style = element.calculatedStyle();

    // calculate borders
    setBorders(dimensions.border(), style);

    // calculate paddings
    setPadding(
        parentDimensions.content().width(),
        parentDimensions.content().height(),
        style,
        dimensions.padding());

    // calculate content position
    Position elementPosition = element.calculatedStyle().position();
    if (Position.STATIC.equals(elementPosition)) {
      layoutStaticBlock(element, parentDimensions, dimensions, style, skipChildren, context);
    } else if (Position.ABSOLUTE.equals(elementPosition)) {
      //      if (!skipChildren) layoutService.layoutChildNodes(element, context);
      Element ancestor = findPositionedAncestor(element);
      layoutAbsoluteBlock(
          element,
          parentDimensions,
          ancestor.dimensions(),
          dimensions,
          style,
          skipChildren,
          context);
    } else if (Position.RELATIVE.equals(elementPosition)) {
      layoutRelativeBlock(element, parentDimensions, dimensions, style, skipChildren, context);
    }
  }

  private void layoutAbsoluteBlock(
      Element element,
      Dimensions parentDimensions,
      Dimensions ancestorDimensions,
      Dimensions dimensions,
      CalculatedStyle style,
      boolean skipChildren,
      LayoutContext context) {

    float verticalAdditions =
        dimensions.border().top()
            + dimensions.padding().top()
            + dimensions.border().bottom()
            + dimensions.padding().bottom();
    float horizontalAdditions =
        dimensions.border().left()
            + dimensions.border().right()
            + dimensions.padding().left()
            + dimensions.padding().right();

    float contentX;
    float contentWidth;

    // calculate content x position and width
    if (style.left().isAuto() && style.right().isAuto()) {

      float parentOffset = parentDimensions.content().x();
      contentX = parentOffset + dimensions.border().left() + dimensions.padding().left();

      float parentPaddingBoxWidth =
          Math.max(
              parentDimensions.paddingBox().width(),
              ancestorDimensions.paddingBox().width()
                  - parentOffset
                  + ancestorDimensions.border().right());

      contentWidth = getWidth(parentPaddingBoxWidth, style);

    } else {
      float parentPaddingBoxWidth = ancestorDimensions.paddingBox().width();
      float left =
          dimensions.border().left()
              + dimensions.padding().left()
              + ancestorDimensions.border().left();
      float right = left + parentPaddingBoxWidth;
      if (style.left().isLength()) {
        left += getFloatLength(style.left(), parentPaddingBoxWidth);
      }
      if (style.right().isLength()) {
        right -= getFloatLength(style.right(), parentPaddingBoxWidth);
      }

      contentX = left;
      contentWidth = right - left;
    }

    contentWidth -= horizontalAdditions;

    dimensions.content().x(contentX);
    dimensions.content().width(contentWidth);

    float childrenHeight = childrenHeight(element, style, skipChildren, context);

    float contentY;
    float contentHeight;
    float borderBoxHeight;
    if (style.top().isAuto() && style.bottom().isAuto()) {
      float parentPaddingBoxHeight = parentDimensions.paddingBox().height();
      float parentOffset = parentDimensions.content().y();
      contentY = parentOffset + dimensions.border().top() + dimensions.padding().top();

      Float blockBottomY = context.lastBlockBottomY();
      if (blockBottomY != null) {
        contentY = blockBottomY + dimensions.border().top();
      }

      borderBoxHeight =
          getHeight(parentPaddingBoxHeight, childrenHeight + verticalAdditions, style);

    } else {
      float parentPaddingBoxHeight =
          ancestorDimensions.padding().top()
              + ancestorDimensions.padding().bottom()
              + ancestorDimensions.content().height();

      float parentOffset = ancestorDimensions.content().y();
      contentY = parentOffset + dimensions.border().top() + dimensions.padding().top();

      float bottom = contentY + parentPaddingBoxHeight;
      if (style.top().isLength()) {
        contentY +=
            getFloatLength(style.top(), parentPaddingBoxHeight)
                - ancestorDimensions.padding().top();
      }
      if (style.bottom().isLength()) {
        bottom =
            parentOffset
                + ancestorDimensions.padding().bottom()
                + ancestorDimensions.content().height()
                - (getFloatLength(style.bottom(), parentPaddingBoxHeight));
      }

      if (style.bottom().isLength() && style.top().isLength()) {
        if (style.height().isAuto()) borderBoxHeight = bottom - contentY;
        else
          borderBoxHeight =
              getHeight(parentPaddingBoxHeight, childrenHeight + verticalAdditions, style);
      } else {
        borderBoxHeight =
            getHeight(parentPaddingBoxHeight, childrenHeight + verticalAdditions, style);

        if (style.bottom().isLength()) {
          contentY =
              bottom - borderBoxHeight + dimensions.border().top() + dimensions.padding().top();
        }
      }
    }

    contentHeight = borderBoxHeight - verticalAdditions;
    dimensions.content().y(contentY);
    dimensions.content().height(contentHeight);
  }

  private void layoutStaticBlock(
      Element element,
      Dimensions parentDimensions,
      Dimensions dimensions,
      CalculatedStyle style,
      boolean skipChildren,
      LayoutContext context) {

    float contentX = getContentX(element);
    float contentY = getContentY(element);

    Float blockBottomY = context.lastBlockBottomY();
    if (blockBottomY != null) {
      contentY = blockBottomY + dimensions.border().top();
    }

    dimensions.contentPosition(contentX, contentY);

    float verticalAdditions =
        dimensions.border().top()
            + dimensions.border().bottom()
            + dimensions.padding().top()
            + dimensions.padding().bottom();
    float horizontalAdditions =
        dimensions.border().left()
            + dimensions.border().right()
            + dimensions.padding().left()
            + dimensions.padding().right();

    float contentWidth;
    if (element instanceof Frame frame) {
      contentWidth = frame.frameSize().x;
    } else {
      contentWidth = getWidth(parentDimensions.content().width(), style);
    }
    contentWidth -= horizontalAdditions;
    dimensions.content().width(contentWidth);

    float borderBoxHeight;
    if (element instanceof Frame frame) {
      if (!skipChildren) layoutService.layoutChildNodes(element, context);
      borderBoxHeight = frame.frameSize().y;
    } else {
      float childrenHeight = childrenHeight(element, style, skipChildren, context);
      borderBoxHeight =
          getHeight(parentDimensions.content().height(), childrenHeight + verticalAdditions, style);
    }
    float contentHeight = borderBoxHeight - verticalAdditions;
    dimensions.content().height(contentHeight);

    context.lastTextEndY(null);
    context.previousNode(element);
    context.lastBlockBottomY(dimensions.borderBox().y() + dimensions.borderBox().height());
  }

  private void layoutRelativeBlock(
      Element element,
      Dimensions parentDimensions,
      Dimensions dimensions,
      CalculatedStyle style,
      boolean skipChildren,
      LayoutContext context) {
    layoutStaticBlock(element, parentDimensions, dimensions, style, skipChildren, context);
    float x = dimensions.content().x();
    float y = dimensions.content().y();

    if (!style.left().isAuto()) {
      x += getFloatLength(style.left(), parentDimensions.content().width());
    } else if (!style.right().isAuto()) {
      x -= getFloatLength(style.right(), parentDimensions.content().width());
    }

    if (!style.top().isAuto()) {
      y += getFloatLength(style.top(), parentDimensions.content().height());
    } else if (!style.bottom().isAuto()) {
      y -= getFloatLength(style.bottom(), parentDimensions.content().height());
    }
    dimensions.contentPosition(x, y);
  }

  private Dimensions getParentDimensions(Element element, Element parent) {
    Dimensions parentDimensions;
    if (element instanceof Frame frame) {
      parentDimensions = new Dimensions();
      parentDimensions.contentSize(frame.frameSize().x, frame.frameSize().y);
    } else if (parent == null) {
      parentDimensions = new Dimensions();
      var frame = element.frame();
      parentDimensions.contentSize(frame.frameSize().x, frame.frameSize().y);
    } else {
      parentDimensions = parent.dimensions();
    }
    return parentDimensions;
  }

  private float childrenHeight(
      Element element, CalculatedStyle style, boolean skipChildren, LayoutContext context) {
    float childrenHeight = 0;
    Unit height = style.height();
    if (!skipChildren) layoutService.layoutChildNodes(element, context);
    if (style.display().equals(Display.BLOCK) && height.isAuto() && !skipChildren) {
      childrenHeight = getChildNodesHeight(element);
    }
    return childrenHeight;
  }

  private float getWidth(float parentWidth, CalculatedStyle style) {
    Optional<Float> width = getFloatLengthOptional(style.width(), parentWidth);
    Optional<Float> minWidth = getFloatLengthOptional(style.minWidth(), parentWidth);
    Optional<Float> maxWidth = getFloatLengthOptional(style.maxWidth(), parentWidth);

    float w = width.orElse(parentWidth);
    w = Math.max(w, minWidth.orElse(w));
    w = Math.min(w, maxWidth.orElse(w));
    return w;
  }

  /**
   * Returns the height of the element's content, i.e. the height of the element's content box.
   *
   * @param parentHeight the height of the element's containing block.
   * @param borderBoxHeight the height of the element's children with border and padding.
   * @param style the element's style.
   * @return the height of the element's content.
   */
  private float getHeight(float parentHeight, float borderBoxHeight, CalculatedStyle style) {
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
    // skip layout if element has no frame - that means that it is not attached to any
    // node tree (and tree root is frame).
    return element.frame() == null || (element.parent() == null && !(element instanceof Frame));
  }

  private void setBorders(Edges border, CalculatedStyle style) {
    applyPadding(style.borderLeftWidth(), style.borderLeftStyle(), border::left);
    applyPadding(style.borderRightWidth(), style.borderRightStyle(), border::right);
    applyPadding(style.borderTopWidth(), style.borderTopStyle(), border::top);
    applyPadding(style.borderBottomWidth(), style.borderBottomStyle(), border::bottom);
  }

  private void applyPadding(
      PixelLength borderWidth, BorderStyle borderStyle, Consumer<Float> borderConsumer) {
    if (borderWidth != null && !BorderStyle.NONE.equals(borderStyle)) {
      borderConsumer.accept(borderWidth.convert());
    }
  }
}
