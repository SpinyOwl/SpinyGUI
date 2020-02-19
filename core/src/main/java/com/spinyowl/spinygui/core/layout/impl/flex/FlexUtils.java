package com.spinyowl.spinygui.core.layout.impl.flex;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Margin;
import com.spinyowl.spinygui.core.style.types.Padding;
import com.spinyowl.spinygui.core.style.types.flex.*;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import org.lwjgl.util.yoga.Yoga;

import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import java.util.function.ObjIntConsumer;

import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PERCENT;
import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PIXEL;
import static org.lwjgl.util.yoga.Yoga.*;

/**
 * @author Aliaksandr_Shcherbin.
 */
final class FlexUtils {

    private FlexUtils() {
    }

    public static void setJustifyContent(long node, JustifyContent justifyContent, Element element) {
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

    public static void setFlexWrap(long node, FlexWrap flexWrap, Element element) {
        if (flexWrap == null || flexWrap == FlexWrap.NOWRAP) {
            YGNodeStyleSetFlexWrap(node, YGWrapNoWrap);
        } else if (flexWrap == FlexWrap.WRAP) {
            YGNodeStyleSetFlexWrap(node, YGWrapWrap);
        } else if (flexWrap == FlexWrap.WRAP_REVERSE) {
            YGNodeStyleSetFlexWrap(node, YGWrapReverse);
        }
    }

    public static void setAlignItems(long node, AlignItems alignItems, Element component) {

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

    public static void setAlignSelf(long node, AlignSelf alignItems, Element component) {
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
        Padding padding = style.getPadding();
        setLength(padding.getLeft(), node, YGEdgeLeft, Yoga::YGNodeStyleSetPadding, Yoga::YGNodeStyleSetPaddingPercent);
        setLength(padding.getTop(), node, YGEdgeTop, Yoga::YGNodeStyleSetPadding, Yoga::YGNodeStyleSetPaddingPercent);
        setLength(padding.getRight(), node, YGEdgeRight, Yoga::YGNodeStyleSetPadding, Yoga::YGNodeStyleSetPaddingPercent);
        setLength(padding.getBottom(), node, YGEdgeBottom, Yoga::YGNodeStyleSetPadding, Yoga::YGNodeStyleSetPaddingPercent);
    }

    public static void setMargin(long node, NodeStyle style) {
        Margin margin = style.getMargin();
        setUnit(margin.getLeft(), node, YGEdgeLeft,
                Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin, Yoga::YGNodeStyleSetMarginPercent);
        setUnit(margin.getTop(), node, YGEdgeTop,
                Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin, Yoga::YGNodeStyleSetMarginPercent);
        setUnit(margin.getRight(), node, YGEdgeRight,
                Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin, Yoga::YGNodeStyleSetMarginPercent);
        setUnit(margin.getBottom(), node, YGEdgeBottom,
                Yoga::YGNodeStyleSetMarginAuto, Yoga::YGNodeStyleSetMargin, Yoga::YGNodeStyleSetMarginPercent);
    }

    public static void setUnit(Unit unit, long node,
                               LongConsumer autoConsumer,
                               ObjIntConsumer<Long> pixelConsumer,
                               BiConsumer<Long, Float> percentConsumer) {
        if (unit != null) {
            if (unit.isAuto()) {
                autoConsumer.accept(node);
            } else {
                Length l = unit.asLength();
                if (PIXEL.equals(l.type())) {
                    pixelConsumer.accept(node, (Integer) l.get());
                } else if (PERCENT.equals(l.type())) {
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
                Length l = unit.asLength();
                if (PIXEL.equals(l.type())) {
                    pixelConsumer.accept(node, side, (Integer) l.get());
                } else if (PERCENT.equals(l.type())) {
                    percentConsumer.accept(node, side, (Float) l.get());
                }
            }
        }
    }

    public static void setLength(Length length, long node,
                                 ObjIntConsumer<Long> pixelConsumer,
                                 BiConsumer<Long, Float> percentConsumer) {
        if (length != null) {
            if (PIXEL.equals(length.type())) {
                pixelConsumer.accept(node, (Integer) length.get());
            } else if (PERCENT.equals(length.type())) {
                percentConsumer.accept(node, (Float) length.get());
            }
        }
    }

    public static void setLength(Length length, long node, int side,
                                 TriConsumer<Long, Integer, Integer> pixelConsumer,
                                 TriConsumer<Long, Integer, Float> percentConsumer) {
        if (length != null) {
            if (PIXEL.equals(length.type())) {
                pixelConsumer.accept(node, side, (Integer) length.get());
            } else if (PERCENT.equals(length.type())) {
                percentConsumer.accept(node, side, (Float) length.get());
            }
        }
    }

    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}
