package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.findPositionedAncestor;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.hasPosition;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.isPositioned;
import static com.spinyowl.spinygui.core.style.types.Position.ABSOLUTE;
import static com.spinyowl.spinygui.core.style.types.Position.STATIC;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static org.lwjgl.util.yoga.Yoga.YGAlignAuto;
import static org.lwjgl.util.yoga.Yoga.YGAlignBaseline;
import static org.lwjgl.util.yoga.Yoga.YGAlignCenter;
import static org.lwjgl.util.yoga.Yoga.YGAlignFlexEnd;
import static org.lwjgl.util.yoga.Yoga.YGAlignFlexStart;
import static org.lwjgl.util.yoga.Yoga.YGAlignStretch;
import static org.lwjgl.util.yoga.Yoga.YGDirectionLTR;
import static org.lwjgl.util.yoga.Yoga.YGDisplayFlex;
import static org.lwjgl.util.yoga.Yoga.YGEdgeBottom;
import static org.lwjgl.util.yoga.Yoga.YGEdgeLeft;
import static org.lwjgl.util.yoga.Yoga.YGEdgeRight;
import static org.lwjgl.util.yoga.Yoga.YGEdgeTop;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionColumn;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionColumnReverse;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionRow;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionRowReverse;
import static org.lwjgl.util.yoga.Yoga.YGJustifyCenter;
import static org.lwjgl.util.yoga.Yoga.YGJustifyFlexEnd;
import static org.lwjgl.util.yoga.Yoga.YGJustifyFlexStart;
import static org.lwjgl.util.yoga.Yoga.YGJustifySpaceAround;
import static org.lwjgl.util.yoga.Yoga.YGJustifySpaceBetween;
import static org.lwjgl.util.yoga.Yoga.YGJustifySpaceEvenly;
import static org.lwjgl.util.yoga.Yoga.YGNodeCalculateLayout;
import static org.lwjgl.util.yoga.Yoga.YGNodeFree;
import static org.lwjgl.util.yoga.Yoga.YGNodeInsertChild;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetBorder;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetLeft;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetMargin;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetPadding;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetTop;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeNew;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignItems;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignSelf;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetDisplay;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexGrow;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexShrink;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexWrap;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetJustifyContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPositionType;
import static org.lwjgl.util.yoga.Yoga.YGPositionTypeAbsolute;
import static org.lwjgl.util.yoga.Yoga.YGPositionTypeRelative;
import static org.lwjgl.util.yoga.Yoga.YGWrapNoWrap;
import static org.lwjgl.util.yoga.Yoga.YGWrapReverse;
import static org.lwjgl.util.yoga.Yoga.YGWrapWrap;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Edges;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Length.PercentLength;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import java.util.function.ObjIntConsumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.lwjgl.util.yoga.Yoga;

@Getter
@RequiredArgsConstructor
public class FlexLayout implements Layout<Element> {

  public static final float THRESHOLD = 0.0001f;
  @NonNull private final SystemEventProcessor systemEventProcessor;
  @NonNull private final EventProcessor eventProcessor;
  @NonNull private final TimeService timeService;
  @NonNull private final LayoutService layoutService;

  /**
   * Used to lay out child elements for provided element.
   *
   * <p>Currently sizes calculated as if all elements has 'box-sizing: border-box'.
   *
   * @param parent element to lay out.
   */
  @Override
  public void layout(Element parent, LayoutContext context) {
    // initialize
    var rootNode = YGNodeNew();
    prepareNode(parent, rootNode);
    YGNodeStyleSetDisplay(rootNode, YGDisplayFlex);

    layoutService.layoutChildNodes(parent, context);

    Element positionedParent = isPositioned(parent) ? parent : findPositionedAncestor(parent);

    var childNodes = new ArrayList<Long>();
    var children =
        parent.children().stream().filter(node -> shouldPersist(node, positionedParent)).toList();
    for (var child : children) {
      var childNode = YGNodeNew();
      prepareNode(child, childNode);
      YGNodeInsertChild(rootNode, childNode, childNodes.size());
      childNodes.add(childNode);
    }

    Rect parentBorderBox = parent.box().borderBox();
    // calculate
    YGNodeCalculateLayout(
        rootNode, parentBorderBox.width(), parentBorderBox.height(), YGDirectionLTR);

    // apply to children
    for (var i = 0; i < children.size(); i++) {
      var child = children.get(i);
      var yogaNode = childNodes.get(i);

      Box box = child.box();
      Edges padding = box.padding();
      padding.left(YGNodeLayoutGetPadding(yogaNode, YGEdgeLeft));
      padding.right(YGNodeLayoutGetPadding(yogaNode, YGEdgeRight));
      padding.top(YGNodeLayoutGetPadding(yogaNode, YGEdgeTop));
      padding.bottom(YGNodeLayoutGetPadding(yogaNode, YGEdgeBottom));

      Edges margin = box.margin();
      margin.left(YGNodeLayoutGetMargin(yogaNode, YGEdgeLeft));
      margin.right(YGNodeLayoutGetMargin(yogaNode, YGEdgeRight));
      margin.top(YGNodeLayoutGetMargin(yogaNode, YGEdgeTop));
      margin.bottom(YGNodeLayoutGetMargin(yogaNode, YGEdgeBottom));

      Edges border = box.border();
      border.left(YGNodeLayoutGetBorder(yogaNode, YGEdgeLeft));
      border.right(YGNodeLayoutGetBorder(yogaNode, YGEdgeRight));
      border.top(YGNodeLayoutGetBorder(yogaNode, YGEdgeTop));
      border.bottom(YGNodeLayoutGetBorder(yogaNode, YGEdgeBottom));

      float width =
          YGNodeLayoutGetWidth(yogaNode)
              - padding.left()
              - padding.right()
              - border.left()
              - border.right();
      float height =
          YGNodeLayoutGetHeight(yogaNode)
              - padding.top()
              - padding.bottom()
              - border.top()
              - border.bottom();
      height =
          child.resolvedStyle().height().isAuto()
              ? Math.max(box.content().height(), height)
              : height;
      box.contentSize(width, height);
      float x = YGNodeLayoutGetLeft(yogaNode) + padding.left() + border.left();
      float y = YGNodeLayoutGetTop(yogaNode) + padding.top() + border.top();
      box.contentPosition(x, y);
    }

    // free mem
    for (var childNode : childNodes) {
      YGNodeFree(childNode);
    }

    YGNodeFree(rootNode);
  }

  private boolean shouldPersist(Element node, Element positionedParent) {
    return visible(node) && (!hasPosition(node, ABSOLUTE) || node.parent() == positionedParent);
  }

  /**
   * Used to prepare root node.
   *
   * @param element parent element associated to root node.
   * @param node root yoga node.
   */
  private static void prepareNode(Element element, long node) {
    var style = element.resolvedStyle();
    setFlexDirection(node, style.flexDirection());
    setJustifyContent(node, style.justifyContent());
    setAlignItems(node, style.alignItems());
    setAlignSelf(node, style.alignSelf());

    setMinWidth(node, style);
    setMaxWidth(node, style);
    setWidth(node, style);

    setMinHeight(node, style);
    setMaxHeight(node, style);
    setHeight(node, style);

    Position position = element.resolvedStyle().position();
    if (!STATIC.equals(position)) {
      setPosition(node, style.top(), YGEdgeTop);
      setPosition(node, style.bottom(), YGEdgeBottom);
      setPosition(node, style.right(), YGEdgeRight);
      setPosition(node, style.left(), YGEdgeLeft);

      YGNodeStyleSetPositionType(
          node,
          ABSOLUTE.equals(style.position()) ? YGPositionTypeAbsolute : YGPositionTypeRelative);
    }

    setBorder(node, style);

    setFlexBasis(node, style.flexBasis());

    setPadding(node, style);
    setMargin(node, style);

    setFlexWrap(node, style.flexWrap());

    YGNodeStyleSetFlexGrow(node, style.flexGrow());
    YGNodeStyleSetFlexShrink(node, style.flexShrink());
  }

  private static void setMinHeight(long node, ResolvedStyle style) {
    setLength(
        style.minHeight(),
        node,
        Yoga::YGNodeStyleSetMinHeight,
        Yoga::YGNodeStyleSetMinHeightPercent);
  }

  private static void setMaxWidth(long node, ResolvedStyle style) {
    setLength(
        style.maxWidth(), node, Yoga::YGNodeStyleSetMaxWidth, Yoga::YGNodeStyleSetMaxWidthPercent);
  }

  private static void setMaxHeight(long node, ResolvedStyle style) {
    setLength(
        style.maxHeight(),
        node,
        Yoga::YGNodeStyleSetMaxHeight,
        Yoga::YGNodeStyleSetMaxHeightPercent);
  }

  private static void setPosition(long node, Unit distance, int edge) {
    setUnit(
        distance, node, edge, Yoga::YGNodeStyleSetPosition, Yoga::YGNodeStyleSetPositionPercent);
  }

  private static void setFlexBasis(long node, Unit flexBasis) {
    setUnit(
        flexBasis,
        node,
        Yoga::YGNodeStyleSetFlexBasisAuto,
        Yoga::YGNodeStyleSetFlexBasis,
        Yoga::YGNodeStyleSetFlexBasisPercent);
  }

  private static void setWidth(long node, ResolvedStyle style) {
    setUnit(
        style.width(),
        node,
        Yoga::YGNodeStyleSetWidthAuto,
        Yoga::YGNodeStyleSetWidth,
        Yoga::YGNodeStyleSetWidthPercent);
  }

  private static void setJustifyContent(long node, JustifyContent justifyContent) {
    if (justifyContent == null || justifyContent == JustifyContent.FLEX_START) {
      YGNodeStyleSetJustifyContent(node, YGJustifyFlexStart);
    } else if (justifyContent == JustifyContent.CENTER) {
      YGNodeStyleSetJustifyContent(node, YGJustifyCenter);
    } else if (justifyContent == JustifyContent.FLEX_END) {
      YGNodeStyleSetJustifyContent(node, YGJustifyFlexEnd);
    } else if (justifyContent == JustifyContent.SPACE_AROUND) {
      YGNodeStyleSetJustifyContent(node, YGJustifySpaceAround);
    } else if (justifyContent == JustifyContent.SPACE_BETWEEN) {
      YGNodeStyleSetJustifyContent(node, YGJustifySpaceBetween);
    } else if (justifyContent == JustifyContent.SPACE_EVENLY) {
      YGNodeStyleSetJustifyContent(node, YGJustifySpaceEvenly);
    }
  }

  private static void setFlexDirection(long rootNode, FlexDirection flexDirection) {
    if (flexDirection == FlexDirection.ROW) {
      YGNodeStyleSetFlexDirection(rootNode, YGFlexDirectionRow);
    } else if (flexDirection == FlexDirection.COLUMN) {
      YGNodeStyleSetFlexDirection(rootNode, YGFlexDirectionColumn);
    } else if (flexDirection == FlexDirection.ROW_REVERSE) {
      YGNodeStyleSetFlexDirection(rootNode, YGFlexDirectionRowReverse);
    } else if (flexDirection == FlexDirection.COLUMN_REVERSE) {
      YGNodeStyleSetFlexDirection(rootNode, YGFlexDirectionColumnReverse);
    }
  }

  private static void setFlexWrap(long node, FlexWrap flexWrap) {
    if (flexWrap == null || flexWrap == FlexWrap.NOWRAP) {
      YGNodeStyleSetFlexWrap(node, YGWrapNoWrap);
    } else if (flexWrap == FlexWrap.WRAP) {
      YGNodeStyleSetFlexWrap(node, YGWrapWrap);
    } else if (flexWrap == FlexWrap.WRAP_REVERSE) {
      YGNodeStyleSetFlexWrap(node, YGWrapReverse);
    }
  }

  private static void setAlignItems(long node, AlignItems alignItems) {

    if (alignItems == AlignItems.FLEX_END) {
      YGNodeStyleSetAlignItems(node, YGAlignFlexEnd);
    } else if (alignItems == AlignItems.CENTER) {
      YGNodeStyleSetAlignItems(node, YGAlignCenter);
    } else if (alignItems == AlignItems.FLEX_START) {
      YGNodeStyleSetAlignItems(node, YGAlignFlexStart);
    } else if (alignItems == AlignItems.STRETCH) {
      YGNodeStyleSetAlignItems(node, YGAlignStretch);
    } else if (alignItems == AlignItems.BASELINE) {
      YGNodeStyleSetAlignItems(node, YGAlignBaseline);
    } else if (alignItems == AlignItems.AUTO) {
      YGNodeStyleSetAlignItems(node, YGAlignAuto);
    }
  }

  private static void setAlignSelf(long node, AlignSelf alignItems) {
    if (alignItems == AlignSelf.FLEX_END) {
      YGNodeStyleSetAlignSelf(node, YGAlignFlexEnd);
    } else if (alignItems == AlignSelf.CENTER) {
      YGNodeStyleSetAlignSelf(node, YGAlignCenter);
    } else if (alignItems == AlignSelf.FLEX_START) {
      YGNodeStyleSetAlignSelf(node, YGAlignFlexStart);
    } else if (alignItems == AlignSelf.STRETCH) {
      YGNodeStyleSetAlignSelf(node, YGAlignStretch);
    } else if (alignItems == AlignSelf.BASELINE) {
      YGNodeStyleSetAlignSelf(node, YGAlignBaseline);
    } else if (alignItems == AlignSelf.AUTO) {
      YGNodeStyleSetAlignSelf(node, YGAlignAuto);
    }
  }

  private static void setHeight(long node, ResolvedStyle style) {
    setUnit(
        style.height(),
        node,
        Yoga::YGNodeStyleSetHeightAuto,
        Yoga::YGNodeStyleSetHeight,
        Yoga::YGNodeStyleSetHeightPercent);
  }

  private static void setMinWidth(long node, ResolvedStyle style) {
    setLength(
        style.minWidth(), node, Yoga::YGNodeStyleSetMinWidth, Yoga::YGNodeStyleSetMinWidthPercent);
  }

  private static void setPadding(long node, ResolvedStyle style) {
    setLength(
        style.paddingLeft(),
        node,
        YGEdgeLeft,
        Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
    setLength(
        style.paddingTop(),
        node,
        YGEdgeTop,
        Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
    setLength(
        style.paddingRight(),
        node,
        YGEdgeRight,
        Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
    setLength(
        style.paddingBottom(),
        node,
        YGEdgeBottom,
        Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
  }

  private static void setBorder(long node, ResolvedStyle style) {
    if (!BorderStyle.NONE.equals(style.borderLeftStyle()))
      setLength(style.borderLeftWidth(), node, YGEdgeLeft, Yoga::YGNodeStyleSetBorder);
    if (!BorderStyle.NONE.equals(style.borderTopStyle()))
      setLength(style.borderTopWidth(), node, YGEdgeTop, Yoga::YGNodeStyleSetBorder);
    if (!BorderStyle.NONE.equals(style.borderRightStyle()))
      setLength(style.borderRightWidth(), node, YGEdgeRight, Yoga::YGNodeStyleSetBorder);
    if (!BorderStyle.NONE.equals(style.borderBottomStyle()))
      setLength(style.borderBottomWidth(), node, YGEdgeBottom, Yoga::YGNodeStyleSetBorder);
  }

  private static void setMargin(long node, ResolvedStyle style) {
    setUnit(
        style.marginLeft(),
        node,
        YGEdgeLeft,
        Yoga::YGNodeStyleSetMarginAuto,
        Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
    setUnit(
        style.marginTop(),
        node,
        YGEdgeTop,
        Yoga::YGNodeStyleSetMarginAuto,
        Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
    setUnit(
        style.marginRight(),
        node,
        YGEdgeRight,
        Yoga::YGNodeStyleSetMarginAuto,
        Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
    setUnit(
        style.marginBottom(),
        node,
        YGEdgeBottom,
        Yoga::YGNodeStyleSetMarginAuto,
        Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
  }

  public static void setLength(
      Length<?> l,
      long node,
      ObjIntConsumer<Long> pixelConsumer,
      BiConsumer<Long, Float> percentConsumer) {
    if (l != null) {
      if (l instanceof PixelLength) {
        pixelConsumer.accept(node, (Integer) l.value());
      } else if (l instanceof PercentLength) {
        percentConsumer.accept(node, (Float) l.value());
      }
    }
  }

  public static void setLength(
      Length<?> l,
      long node,
      int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (l != null) {
      if (l instanceof PixelLength) {
        pixelConsumer.accept(node, side, (Integer) l.value());
      } else if (l instanceof PercentLength) {
        percentConsumer.accept(node, side, (Float) l.value());
      }
    }
  }

  public static void setLength(
      Length<?> l, long node, int side, TriConsumer<Long, Integer, Integer> pixelConsumer) {
    if (l instanceof PixelLength) {
      pixelConsumer.accept(node, side, (Integer) l.value());
    }
  }

  public static void setUnit(
      Unit unit,
      long node,
      int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (unit != null && !unit.isAuto()) {
      applyPixelOrPercentToSide(unit, node, side, pixelConsumer, percentConsumer);
    }
  }

  public static void setUnit(
      Unit unit,
      long node,
      LongConsumer autoConsumer,
      ObjIntConsumer<Long> pixelConsumer,
      BiConsumer<Long, Float> percentConsumer) {
    if (unit != null) {
      if (unit.isAuto()) {
        autoConsumer.accept(node);
      } else {
        Length<?> l = unit.asLength();
        if (l instanceof PixelLength) {
          pixelConsumer.accept(node, (Integer) l.value());
        } else if (l instanceof PercentLength) {
          percentConsumer.accept(node, (Float) l.value());
        }
      }
    }
  }

  public static void applyPixelOrPercentToSide(
      Unit unit,
      long node,
      int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    Length<?> l = unit.asLength();
    if (l instanceof PixelLength) {
      pixelConsumer.accept(node, side, (Integer) l.value());
    } else if (l instanceof PercentLength) {
      percentConsumer.accept(node, side, (Float) l.value());
    }
  }

  public static void setUnit(
      Unit unit,
      long node,
      int side,
      ObjIntConsumer<Long> autoConsumer,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (unit != null) {
      if (unit.isAuto()) {
        autoConsumer.accept(node, side);
      } else {
        applyPixelOrPercentToSide(unit, node, side, pixelConsumer, percentConsumer);
      }
    }
  }

  @FunctionalInterface
  public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
  }
}
