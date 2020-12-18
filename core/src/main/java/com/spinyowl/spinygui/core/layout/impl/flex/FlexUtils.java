package com.spinyowl.spinygui.core.layout.impl.flex;

import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PERCENT_TYPE;
import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PIXEL_TYPE;
import static org.lwjgl.util.yoga.Yoga.YGAlignAuto;
import static org.lwjgl.util.yoga.Yoga.YGAlignBaseline;
import static org.lwjgl.util.yoga.Yoga.YGAlignCenter;
import static org.lwjgl.util.yoga.Yoga.YGAlignFlexEnd;
import static org.lwjgl.util.yoga.Yoga.YGAlignFlexStart;
import static org.lwjgl.util.yoga.Yoga.YGAlignStretch;
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
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignItems;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignSelf;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexWrap;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetJustifyContent;
import static org.lwjgl.util.yoga.Yoga.YGWrapNoWrap;
import static org.lwjgl.util.yoga.Yoga.YGWrapReverse;
import static org.lwjgl.util.yoga.Yoga.YGWrapWrap;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Margin;
import com.spinyowl.spinygui.core.style.types.Padding;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import java.util.function.ObjIntConsumer;
import org.lwjgl.util.yoga.Yoga;

/**
 * @author Aliaksandr_Shcherbin.
 */
final class FlexUtils {

  private FlexUtils() {
  }

  public static void setJustifyContent(long node, JustifyContent justifyContent) {
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

  public static void setFlexDirection(long rootNode, FlexDirection flexDirection) {
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

  public static void setFlexWrap(long node, FlexWrap flexWrap) {
    if (flexWrap == null || flexWrap == FlexWrap.NOWRAP) {
      YGNodeStyleSetFlexWrap(node, YGWrapNoWrap);
    } else if (flexWrap == FlexWrap.WRAP) {
      YGNodeStyleSetFlexWrap(node, YGWrapWrap);
    } else if (flexWrap == FlexWrap.WRAP_REVERSE) {
      YGNodeStyleSetFlexWrap(node, YGWrapReverse);
    }
  }

  public static void setAlignItems(long node, AlignItems alignItems) {

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

  public static void setAlignSelf(long node, AlignSelf alignItems) {
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

  public static void setPadding(long node, NodeStyle style) {
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

  public static void setMargin(long node, NodeStyle style) {
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

  public static void setUnit(Unit unit, long node,
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

  public static void setUnit(Unit unit, long node, int side,
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

  public static void setUnit(Unit unit, long node, int side,
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

  public static void setLength(Length<?> length, long node,
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

  public static void setLength(Length<?> length, long node, int side,
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

  public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);
  }
}
