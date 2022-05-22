package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.findPositionedAncestor;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.getChildNodesHeight;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.setPadding;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLength;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLengthOptional;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.layout.Box;
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

    Box parentBox = getParentDimensions(element, element.parent());

    Box box = element.box();
    CalculatedStyle style = element.calculatedStyle();

    // calculate borders
    setBorders(box.border(), style);

    // calculate paddings
    setPadding(parentBox.content().width(), parentBox.content().height(), style, box.padding());

    // calculate content position
    Position elementPosition = element.calculatedStyle().position();
    if (Position.STATIC.equals(elementPosition)) {
      layoutStaticBlock(element, parentBox, box, style, skipChildren, context);
    } else if (Position.ABSOLUTE.equals(elementPosition)) {
      Element ancestor = findPositionedAncestor(element);
      layoutAbsoluteBlock(element, parentBox, ancestor.box(), box, style, skipChildren, context);
    } else if (Position.RELATIVE.equals(elementPosition)) {
      layoutRelativeBlock(element, parentBox, box, style, skipChildren, context);
    }
  }

  private void layoutAbsoluteBlock(
      Element element,
      Box parentBox,
      Box ancestorBox,
      Box box,
      CalculatedStyle style,
      boolean skipChildren,
      LayoutContext context) {

    float verticalAdditions =
        box.border().top() + box.padding().top() + box.border().bottom() + box.padding().bottom();
    float horizontalAdditions =
        box.border().left() + box.border().right() + box.padding().left() + box.padding().right();

    float contentX;
    float contentWidth;

    float childrenHeight = childrenHeight(element, style, skipChildren, context);

    // calculate content x position and width
    if (style.left().isAuto() && style.right().isAuto()) {

      float parentOffset = parentBox.content().x();
      contentX = parentOffset + box.border().left() + box.padding().left();

      float parentPaddingBoxWidth =
          Math.max(
              parentBox.paddingBox().width(),
              ancestorBox.paddingBox().width() - parentOffset + ancestorBox.border().right());

      contentWidth = getWidth(parentPaddingBoxWidth, style);

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
        contentWidth = getWidth(parentPaddingBoxWidth, style);
        if (style.left().isLength()) {
          contentX = left;
        } else {
          contentX = right - contentWidth;
        }
      }
    }

    contentWidth -= horizontalAdditions;

    box.content().x(contentX);
    box.content().width(contentWidth);

    float contentY;
    float contentHeight;
    float borderBoxHeight;
    if (style.top().isAuto() && style.bottom().isAuto()) {
      float parentPaddingBoxHeight = parentBox.paddingBox().height();
      float parentOffset = parentBox.content().y();
      contentY = parentOffset + box.border().top() + box.padding().top();

      Float blockBottomY = context.lastBlockBottomY();
      if (blockBottomY != null) {
        contentY = blockBottomY + box.border().top();
      }

      borderBoxHeight =
          getHeight(parentPaddingBoxHeight, childrenHeight + verticalAdditions, style);

    } else {
      float parentPaddingBoxHeight =
          ancestorBox.padding().top()
              + ancestorBox.padding().bottom()
              + ancestorBox.content().height();

      float parentOffset = ancestorBox.content().y();
      contentY = parentOffset + box.border().top() + box.padding().top();

      float bottom = contentY + parentPaddingBoxHeight;
      if (style.top().isLength()) {
        contentY +=
            getFloatLength(style.top(), parentPaddingBoxHeight) - ancestorBox.padding().top();
      }
      if (style.bottom().isLength()) {
        bottom =
            parentOffset
                + ancestorBox.padding().bottom()
                + ancestorBox.content().height()
                - (getFloatLength(style.bottom(), parentPaddingBoxHeight));
      }

      if (style.bottom().isLength() && style.top().isLength()) {
        if (style.height().isAuto())
          borderBoxHeight = bottom - contentY + box.padding().top() + box.border().top();
        else
          borderBoxHeight =
              getHeight(parentPaddingBoxHeight, childrenHeight + verticalAdditions, style);
      } else {
        borderBoxHeight =
            getHeight(parentPaddingBoxHeight, childrenHeight + verticalAdditions, style);

        if (style.bottom().isLength()) {
          contentY = bottom - borderBoxHeight + box.border().top() + box.padding().top();
        }
      }
    }

    contentHeight = borderBoxHeight - verticalAdditions;
    box.content().y(contentY);
    box.content().height(contentHeight);
  }

  private void layoutStaticBlock(
      Element element,
      Box parentBox,
      Box box,
      CalculatedStyle style,
      boolean skipChildren,
      LayoutContext context) {

    float contentX =
        Math.max(parentBox.padding().left(), element.box().margin().left())
            + element.box().border().left()
            + element.box().padding().left();
    float contentY =
        Math.max(parentBox.padding().top(), element.box().margin().top())
            + element.box().border().top()
            + element.box().padding().top();

    Float blockBottomY = context.lastBlockBottomY();
    if (blockBottomY != null) {
      contentY = blockBottomY + box.border().top();
    }

    box.contentPosition(contentX, contentY);

    float verticalAdditions =
        box.border().top() + box.border().bottom() + box.padding().top() + box.padding().bottom();
    float horizontalAdditions =
        box.border().left() + box.border().right() + box.padding().left() + box.padding().right();

    float contentWidth;
    if (element instanceof Frame frame) {
      contentWidth = frame.frameSize().x;
    } else {
      contentWidth = getWidth(parentBox.content().width(), style);
    }
    contentWidth -= horizontalAdditions;
    box.content().width(contentWidth);

    float borderBoxHeight;
    if (element instanceof Frame frame) {
      if (!skipChildren) layoutService.layoutChildNodes(element, context);
      borderBoxHeight = frame.frameSize().y;
    } else {
      float childrenHeight = childrenHeight(element, style, skipChildren, context);
      borderBoxHeight =
          getHeight(parentBox.content().height(), childrenHeight + verticalAdditions, style);
    }
    float contentHeight = borderBoxHeight - verticalAdditions;
    box.content().height(contentHeight);

    context.lastTextEndY(null);
    context.previousNode(element);
    context.lastBlockBottomY(box.borderBox().y() + box.borderBox().height());
  }

  private void layoutRelativeBlock(
      Element element,
      Box parentBox,
      Box box,
      CalculatedStyle style,
      boolean skipChildren,
      LayoutContext context) {
    layoutStaticBlock(element, parentBox, box, style, skipChildren, context);
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
    }
    return parentBox;
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
