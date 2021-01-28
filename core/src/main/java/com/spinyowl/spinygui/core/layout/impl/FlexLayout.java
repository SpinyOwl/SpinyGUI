package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.node.style.types.length.Length.LType.PERCENT_TYPE;
import static com.spinyowl.spinygui.core.node.style.types.length.Length.LType.PIXEL_TYPE;
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
import static org.lwjgl.util.yoga.Yoga.YGNodeFree;
import static org.lwjgl.util.yoga.Yoga.YGNodeInsertChild;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetLeft;
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
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetJustifyContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPositionType;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetWidth;
import static org.lwjgl.util.yoga.Yoga.YGPositionTypeAbsolute;
import static org.lwjgl.util.yoga.Yoga.YGPositionTypeRelative;
import static org.lwjgl.util.yoga.Yoga.YGWrapNoWrap;
import static org.lwjgl.util.yoga.Yoga.YGWrapReverse;
import static org.lwjgl.util.yoga.Yoga.YGWrapWrap;
import static org.lwjgl.util.yoga.Yoga.nYGNodeCalculateLayout;
import com.spinyowl.spinygui.core.event.ChangePositionEvent;
import com.spinyowl.spinygui.core.event.ChangeSizeEvent;
import com.spinyowl.spinygui.core.event.InvalidateTreeEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.Margin;
import com.spinyowl.spinygui.core.node.style.types.Padding;
import com.spinyowl.spinygui.core.node.style.types.Position;
import com.spinyowl.spinygui.core.node.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.node.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.node.style.types.flex.Flex;
import com.spinyowl.spinygui.core.node.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.node.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.node.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.node.style.types.length.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2f;
import org.lwjgl.util.yoga.Yoga;

/**
 * @author Aliaksandr_Shcherbin.
 */
@Getter
@RequiredArgsConstructor
public class FlexLayout implements Layout {

  public static final float THRESHOLD = 0.0001f;
  private final EventProcessor eventProcessor;

  /**
   * Used to lay out child components for parent component.
   *
   * @param parent component to lay out.
   */
  @Override
  public void layout(Element parent) {
    // initialize
    long rootNode = YGNodeNew();
    prepareParentNode(parent, rootNode);
    YGNodeStyleSetDisplay(rootNode, YGDisplayFlex);

    List<Long> childNodes = new ArrayList<>();
    List<Element> components = parent.children().stream()
        .filter(Node::visible).collect(Collectors.toList());
    for (Element component : components) {
      long childNode = YGNodeNew();
      prepareNode(component, childNode);
      YGNodeInsertChild(rootNode, childNode, childNodes.size());
      childNodes.add(childNode);
    }

    // calculate
    nYGNodeCalculateLayout(rootNode, parent.size().x(), parent.size().y(), YGDirectionLTR);

    boolean invalidateTree = false;
    // apply to components
    for (int i = 0; i < components.size(); i++) {
      Node childComponent = components.get(i);
      Long yogaNode = childNodes.get(i);

      Vector2f newPos = new Vector2f(YGNodeLayoutGetLeft(yogaNode),
          YGNodeLayoutGetTop(yogaNode));
      Vector2f oldPos = new Vector2f(childComponent.position());
      childComponent.position(newPos);

      Vector2f newSize = new Vector2f(YGNodeLayoutGetWidth(yogaNode),
          YGNodeLayoutGetHeight(yogaNode));
      Vector2f oldSize = new Vector2f(childComponent.size());
      childComponent.size(newSize);

      invalidateTree =
          invalidateTree || generateEvents(childComponent, newPos, oldPos, newSize, oldSize);
    }

    if (invalidateTree) {
      eventProcessor.pushEvent(InvalidateTreeEvent.create());
    }

    // free mem
    for (Long childNode : childNodes) {
      YGNodeFree(childNode);
    }

    YGNodeFree(rootNode);
  }

  private boolean generateEvents(Node childComponent, Vector2f newPos, Vector2f oldPos,
      Vector2f newSize, Vector2f oldSize) {
    boolean invalidateTree = false;
    if (childComponent instanceof Element) {
      Element e = (Element) childComponent;
      if (!oldPos.equals(newPos, THRESHOLD)) {
        eventProcessor.pushEvent(ChangePositionEvent.of(e, oldPos, newPos));
        invalidateTree = true;
      }
      if (!oldSize.equals(newSize, THRESHOLD)) {
        eventProcessor.pushEvent(ChangeSizeEvent.of(e, oldSize, newSize));
        invalidateTree = true;
      }
    }
    return invalidateTree;
  }

  private void prepareParentNode(Element parent, long rootNode) {
    prepareNode(parent, rootNode);
    YGNodeStyleSetWidth(rootNode, parent.size().x());
    YGNodeStyleSetHeight(rootNode, parent.size().y());
  }

  /**
   * Used to prepare root node.
   *
   * @param component parent component associated to root node.
   * @param node      root yoga node.
   */
  private void prepareNode(Element component, long node) {
    NodeStyle style = component.style();
    Flex flex = style.flex();
    setFlexDirection(node, flex.flexDirection());
    setJustifyContent(node, flex.justifyContent());
    setAlignItems(node, flex.alignItems());
    setAlignSelf(node, flex.alignSelf());

    setLength(style.minWidth(), node, Yoga::YGNodeStyleSetMinWidth,
        Yoga::YGNodeStyleSetMinWidthPercent);
    setLength(style.minHeight(), node, Yoga::YGNodeStyleSetMinHeight,
        Yoga::YGNodeStyleSetMinHeightPercent);

    setLength(style.maxWidth(), node, Yoga::YGNodeStyleSetMaxWidth,
        Yoga::YGNodeStyleSetMaxWidthPercent);
    setLength(style.maxHeight(), node, Yoga::YGNodeStyleSetMaxHeight,
        Yoga::YGNodeStyleSetMaxHeightPercent);

    setUnit(style.width(), node, Yoga::YGNodeStyleSetWidthAuto, Yoga::YGNodeStyleSetWidth,
        Yoga::YGNodeStyleSetWidthPercent);
    setUnit(style.height(), node, Yoga::YGNodeStyleSetHeightAuto, Yoga::YGNodeStyleSetHeight,
        Yoga::YGNodeStyleSetHeightPercent);

    setUnit(style.top(), node, YGEdgeTop, Yoga::YGNodeStyleSetPosition,
        Yoga::YGNodeStyleSetPositionPercent);
    setUnit(style.bottom(), node, YGEdgeBottom, Yoga::YGNodeStyleSetPosition,
        Yoga::YGNodeStyleSetPositionPercent);
    setUnit(style.right(), node, YGEdgeRight, Yoga::YGNodeStyleSetPosition,
        Yoga::YGNodeStyleSetPositionPercent);
    setUnit(style.left(), node, YGEdgeLeft, Yoga::YGNodeStyleSetPosition,
        Yoga::YGNodeStyleSetPositionPercent);

    setUnit(flex.flexBasis(), node,
        Yoga::YGNodeStyleSetFlexBasisAuto, Yoga::YGNodeStyleSetFlexBasis,
        Yoga::YGNodeStyleSetFlexBasisPercent);

    setPadding(node, style);
    setMargin(node, style);

    setFlexWrap(node, flex.flexWrap());
    YGNodeStyleSetPositionType(node,
        style.position() == Position.RELATIVE ? YGPositionTypeRelative
            : YGPositionTypeAbsolute);

    YGNodeStyleSetFlexGrow(node, flex.flexGrow());
    YGNodeStyleSetFlexShrink(node, flex.flexShrink());
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

  private static void setPadding(long node, NodeStyle style) {
    Padding padding = style.padding();
    setLength(padding.left(), node, YGEdgeLeft, Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
    setLength(padding.top(), node, YGEdgeTop, Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
    setLength(padding.right(), node, YGEdgeRight, Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
    setLength(padding.bottom(), node, YGEdgeBottom, Yoga::YGNodeStyleSetPadding,
        Yoga::YGNodeStyleSetPaddingPercent);
  }

  private static void setMargin(long node, NodeStyle style) {
    Margin margin = style.margin();
    setUnit(margin.left(), node, YGEdgeLeft,
        Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
    setUnit(margin.top(), node, YGEdgeTop,
        Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
    setUnit(margin.right(), node, YGEdgeRight,
        Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
    setUnit(margin.bottom(), node, YGEdgeBottom,
        Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin,
        Yoga::YGNodeStyleSetMarginPercent);
  }

  private static void setUnit(Unit unit, long node,
      LongConsumer autoConsumer,
      ObjIntConsumer<Long> pixelConsumer,
      BiConsumer<Long, Float> percentConsumer) {
    if (unit != null) {
      if (unit.isAuto()) {
        autoConsumer.accept(node);
      } else {
        Length<?> l = unit.asLength();
        if (PIXEL_TYPE.equals(l.type())) {
          pixelConsumer.accept(node, (Integer) l.get());
        } else if (PERCENT_TYPE.equals(l.type())) {
          percentConsumer.accept(node, (Float) l.get());
        }
      }
    }
  }

  private static void setUnit(Unit unit, long node, int side,
      ObjIntConsumer<Long> autoConsumer,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (unit != null) {
      if (unit.isAuto()) {
        autoConsumer.accept(node, side);
      } else {
        Length<?> l = unit.asLength();
        if (PIXEL_TYPE.equals(l.type())) {
          pixelConsumer.accept(node, side, (Integer) l.get());
        } else if (PERCENT_TYPE.equals(l.type())) {
          percentConsumer.accept(node, side, (Float) l.get());
        }
      }
    }
  }

  private static void setUnit(Unit unit, long node, int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (unit != null && !unit.isAuto()) {
      Length<?> l = unit.asLength();
      if (PIXEL_TYPE.equals(l.type())) {
        pixelConsumer.accept(node, side, (Integer) l.get());
      } else if (PERCENT_TYPE.equals(l.type())) {
        percentConsumer.accept(node, side, (Float) l.get());
      }
    }
  }

  private static void setLength(Length<?> length, long node,
      ObjIntConsumer<Long> pixelConsumer,
      BiConsumer<Long, Float> percentConsumer) {
    if (length != null) {
      if (PIXEL_TYPE.equals(length.type())) {
        pixelConsumer.accept(node, (Integer) length.get());
      } else if (PERCENT_TYPE.equals(length.type())) {
        percentConsumer.accept(node, (Float) length.get());
      }
    }
  }

  private static void setLength(Length<?> length, long node, int side,
      TriConsumer<Long, Integer, Integer> pixelConsumer,
      TriConsumer<Long, Integer, Float> percentConsumer) {
    if (length != null) {
      if (PIXEL_TYPE.equals(length.type())) {
        pixelConsumer.accept(node, side, (Integer) length.get());
      } else if (PERCENT_TYPE.equals(length.type())) {
        percentConsumer.accept(node, side, (Float) length.get());
      }
    }
  }

  private interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);
  }
}
