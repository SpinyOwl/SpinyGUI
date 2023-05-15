package com.spinyowl.spinygui.core.layout.mode;

import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Edges;
import com.spinyowl.spinygui.core.node.pseudo.PseudoElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Length.PercentLength;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LayoutUtils {

  private static final List<Overflow> SCROLLABLE_OVERFLOW_VALUES =
      List.of(Overflow.AUTO, Overflow.SCROLL);

  public static void setPadding(
      float parentWidth, float parentHeight, ResolvedStyle style, Edges padding) {
    applyPadding(parentWidth, style.paddingLeft(), padding::left);
    applyPadding(parentWidth, style.paddingRight(), padding::right);
    applyPadding(parentHeight, style.paddingTop(), padding::top);
    applyPadding(parentHeight, style.paddingBottom(), padding::bottom);
  }

  public static void setBorders(ResolvedStyle style, Edges border) {
    applyBorder(style.borderLeftWidth(), style.borderLeftStyle(), border::left);
    applyBorder(style.borderRightWidth(), style.borderRightStyle(), border::right);
    applyBorder(style.borderTopWidth(), style.borderTopStyle(), border::top);
    applyBorder(style.borderBottomWidth(), style.borderBottomStyle(), border::bottom);
  }

  private static void applyPadding(
      float base, Length<?> paddingLength, Consumer<Float> paddingConsumer) {
    if (paddingLength == null) {
      paddingConsumer.accept(0f);
    } else {
      var length = paddingLength.asLength();
      if (length instanceof PixelLength) {
        paddingConsumer.accept(length.convert());
      } else if (length instanceof PercentLength) {
        paddingConsumer.accept(length.convert(base));
      }
    }
  }

  private static void applyBorder(
      PixelLength borderWidth, BorderStyle borderStyle, Consumer<Float> borderConsumer) {
    if (borderWidth != null && !BorderStyle.NONE.equals(borderStyle)) {
      borderConsumer.accept(borderWidth.convert());
    }
  }

  public static float getChildNodesHeight(LayoutNode layoutNode) {
    List<Node> nodes =
        layoutNode.children().stream()
            .map(LayoutNode::node)
            .filter(LayoutUtils::affectsSize)
            .toList();
    return !nodes.isEmpty()
        ? nodes.stream()
                .map(node -> node.box().borderBox().y() + node.box().borderBox().height())
                .reduce(0F, Float::max)
            - nodes.get(0).box().borderBox().y()
        : 0;
  }

  public static float getChildNodesWidth(LayoutNode layoutNode) {
    List<Node> nodes =
        layoutNode.children().stream()
            .map(LayoutNode::node)
            .filter(LayoutUtils::affectsSize)
            .toList();
    return !nodes.isEmpty()
        ? nodes.stream()
                .map(node -> node.box().borderBox().x() + node.box().borderBox().width())
                .reduce(0F, Float::max)
            - nodes.get(0).box().borderBox().x()
        : 0;
  }

  private static boolean affectsSize(Node obj) {
    if (obj instanceof Text) return true;
    if (obj instanceof Element el) {
      if (el.resolvedStyle().display().equals(Display.NONE)) return false;
      Position position = el.resolvedStyle().position();
      return position.equals(Position.STATIC) || position.equals(Position.RELATIVE);
    }
    return true;
  }

  public static boolean isPositioned(Element element) {
    return element.resolvedStyle().position().positioned() || element instanceof Frame;
  }

  public static boolean hasPosition(final Element element, final Position position) {
    return element.resolvedStyle().position().equals(position);
  }

  public static Element findPositionedAncestor(Element element) {
    if (element == null) return null;
    if (element == element.frame()) return element.frame();

    Element parent = element.parent();
    if (parent == null) return null;
    if (isPositioned(parent)) return parent;

    return findPositionedAncestor(parent);
  }

  public static Box getParentBox(Node node) {
    Element parent = node.offsetParent();
    if (node instanceof Frame frame) {
      Box parentBox = new Box();
      parentBox.contentSize(frame.frameSize().x, frame.frameSize().y);
      return parentBox;
    }

    if (parent == null) {
      if (node instanceof PseudoElement pseudoElement) {
        return pseudoElement.pseudoParent().box();
      } else {
        Box parentBox = new Box();
        var frame = node.frame();
        parentBox.contentSize(frame.frameSize().x, frame.frameSize().y);
        return parentBox;
      }
    }

    if (parent.resolvedStyle().display().equals(Display.INLINE)) {
      return getParentBox(parent);
    }
    return parent.box();
  }

  //  public static float getVerticalScrollbarWidth(Element element) {
  //    float width = 0;
  //    if (SCROLLABLE_OVERFLOW_VALUES.contains(element.resolvedStyle().overflowY())) {
  //      width = element.verticalScrollbar().box().borderBox().width();
  //    }
  //    return width;
  //  }
  //
  //  public static float getHorizontalScrollbarHeight(Element element) {
  //    float height = 0;
  //    if (SCROLLABLE_OVERFLOW_VALUES.contains(element.resolvedStyle().overflowX())) {
  //      height = element.horizontalScrollbar().box().borderBox().height();
  //    }
  //    return height;
  //  }
}
