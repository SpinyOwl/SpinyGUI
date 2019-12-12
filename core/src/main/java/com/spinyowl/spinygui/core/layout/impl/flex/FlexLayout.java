package com.spinyowl.spinygui.core.layout.impl.flex;

import com.spinyowl.spinygui.core.event.ChangePositionEvent;
import com.spinyowl.spinygui.core.event.ChangeSizeEvent;
import com.spinyowl.spinygui.core.event.ElementTreeUpdateEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessorProvider;
import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import org.joml.Vector2f;
import org.lwjgl.util.yoga.Yoga;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.spinyowl.spinygui.core.layout.impl.flex.FlexUtils.*;

/**
 * @author Aliaksandr_Shcherbin.
 */
public class FlexLayout implements Layout {
public static final float THRESHOLD = 0.0001f;

    /**
     * Used to lay out child components for parent component.
     *
     * @param parent component to lay out.
     */
    @Override
    public void layout(Element parent) {
        // initialize
        long rootNode = Yoga.YGNodeNew();
        prepareParentNode(parent, rootNode);
        Yoga.YGNodeStyleSetDisplay(rootNode, Yoga.YGDisplayFlex);

        List<Long> childNodes = new ArrayList<>();
        List<Element> components = parent.getChildElements().stream()
                .filter(Node::isVisible).collect(Collectors.toList());
        for (Element component : components) {
            long childNode = Yoga.YGNodeNew();
            prepareNode(component, childNode);
            Yoga.YGNodeInsertChild(rootNode, childNode, childNodes.size());
            childNodes.add(childNode);
        }

        // calculate
        Yoga.nYGNodeCalculateLayout(rootNode, parent.getSize().x(), parent.getSize().y(), Yoga.YGDirectionLTR);

        // apply to components
        for (int i = 0; i < components.size(); i++) {
            Node childComponent = components.get(i);
            Long yogaNode = childNodes.get(i);

            Vector2f newPos = new Vector2f(Yoga.YGNodeLayoutGetLeft(yogaNode), Yoga.YGNodeLayoutGetTop(yogaNode));
            Vector2f oldPos = new Vector2f(childComponent.getPosition());
            childComponent.setPosition(newPos);

            Vector2f newSize = new Vector2f(Yoga.YGNodeLayoutGetWidth(yogaNode), Yoga.YGNodeLayoutGetHeight(yogaNode));
            Vector2f oldSize = new Vector2f(childComponent.getSize());
            childComponent.setSize(newSize);

            generateEvents(childComponent, newPos, oldPos, newSize, oldSize);
        }

        // free mem
        for (Long childNode : childNodes) {
            Yoga.YGNodeFree(childNode);
        }

        Yoga.YGNodeFree(rootNode);
    }

    private void generateEvents(Node childComponent, Vector2f newPos, Vector2f oldPos, Vector2f newSize, Vector2f oldSize) {
        if (childComponent instanceof Element) {
            Element e = (Element) childComponent;
            boolean invalidateTree = false;
            if (!oldPos.equals(newPos, THRESHOLD)) {
                EventProcessorProvider.getInstance().pushEvent(new ChangePositionEvent<>(e, oldPos, newPos));
                invalidateTree = true;
            }
            if (!oldSize.equals(newSize, THRESHOLD)) {
                EventProcessorProvider.getInstance().pushEvent(new ChangeSizeEvent<>(e, oldSize, newSize));
                invalidateTree = true;
            }
            if (invalidateTree) {
                EventProcessorProvider.getInstance().pushEvent(new ElementTreeUpdateEvent());
            }
        }
    }

    private void prepareParentNode(Element parent, long rootNode) {
        prepareNode(parent, rootNode);
        Yoga.YGNodeStyleSetWidth(rootNode, parent.getSize().x());
        Yoga.YGNodeStyleSetHeight(rootNode, parent.getSize().y());
    }

    /**
     * Used to prepare root node.
     *
     * @param component parent component associated to root node.
     * @param node      root yoga node.
     */
    private void prepareNode(Element component, long node) {
        NodeStyle style = component.getStyle();
        setFlexDirection(node, style.getFlex().getFlexDirection());
        setJustifyContent(node, style.getFlex().getJustifyContent(), component);
        setAlignItems(node, style.getFlex().getAlignItems(), component);
        setAlignSelf(node, style.getFlex().getAlignSelf(), component);

        setMinWidth(node, style);
        setMinHeight(node, style);

        setMaxWidth(node, style);
        setMaxHeight(node, style);

        setWidth(node, style);
        setHeight(node, style);

        setPosition(node, style.getTop(), Yoga.YGEdgeTop);
        setPosition(node, style.getBottom(), Yoga.YGEdgeBottom);
        setPosition(node, style.getRight(), Yoga.YGEdgeRight);
        setPosition(node, style.getLeft(), Yoga.YGEdgeLeft);

        Yoga.YGNodeStyleSetFlexBasis(node, style.getFlex().getFlexBasis());

        setPadding(node, style);
        setMargin(node, style);

        Yoga.YGNodeStyleSetPositionType(node, style.getPosition() == Position.RELATIVE ? Yoga.YGPositionTypeRelative : Yoga.YGPositionTypeAbsolute);

        Yoga.YGNodeStyleSetFlexGrow(node, style.getFlex().getFlexGrow());
        Yoga.YGNodeStyleSetFlexShrink(node, style.getFlex().getFlexShrink());
    }

    private void setPosition(long node, Length distance, int edge) {
        if (distance != null) {
            if (Length.Type.PIXEL.equals(distance.type())) {
                Yoga.YGNodeStyleSetPosition(node, edge, Length.Type.PIXEL.type().cast(distance.get()));
            } else if (Length.Type.PERCENT.equals(distance.type())) {
                Yoga.YGNodeStyleSetPositionPercent(node, edge, Length.Type.PERCENT.type().cast(distance.get()));
            }
        }
    }

    private void setHeight(long node, NodeStyle style) {
        Unit height = style.getHeight();
        if (height != null) {
            if (height.isAuto()) {
                Yoga.YGNodeStyleSetHeightAuto(node);
            } else {
                Length length = height.asLength();
                if (Length.Type.PIXEL.equals(length.type())) {
                    Yoga.YGNodeStyleSetHeight(node, Length.Type.PIXEL.type().cast(length.get()));
                } else if (Length.Type.PERCENT.equals(length.type())) {
                    Yoga.YGNodeStyleSetHeightPercent(node, Length.Type.PERCENT.type().cast(length.get()));
                }
            }
        }
    }

    private void setWidth(long node, NodeStyle style) {
        Unit width = style.getWidth();
        if (width != null) {
            if (width.isAuto()) {
                Yoga.YGNodeStyleSetWidthAuto(node);
            } else {
                Length length = width.asLength();
                if (Length.Type.PIXEL.equals(length.type())) {
                    Yoga.YGNodeStyleSetWidth(node, Length.Type.PIXEL.type().cast(length.get()));
                } else if (Length.Type.PERCENT.equals(length.type())) {
                    Yoga.YGNodeStyleSetWidthPercent(node, Length.Type.PERCENT.type().cast(length.get()));
                }
            }
        }
    }

    private void setMaxHeight(long node, NodeStyle style) {
        Length maxHeight = style.getMaxHeight();
        if (maxHeight != null) {
            if (Length.Type.PIXEL.equals(maxHeight.type())) {
                Yoga.YGNodeStyleSetMaxHeight(node, Length.Type.PIXEL.type().cast(maxHeight.get()));
            } else if (Length.Type.PERCENT.equals(maxHeight.type())) {
                Yoga.YGNodeStyleSetMaxHeightPercent(node, Length.Type.PERCENT.type().cast(maxHeight.get()));
            }
        }
    }

    private void setMaxWidth(long node, NodeStyle style) {
        Length maxWidth = style.getMaxWidth();
        if (maxWidth != null) {
            if (Length.Type.PIXEL.equals(maxWidth.type())) {
                Yoga.YGNodeStyleSetMaxWidth(node, Length.Type.PIXEL.type().cast(maxWidth.get()));
            } else if (Length.Type.PERCENT.equals(maxWidth.type())) {
                Yoga.YGNodeStyleSetMaxWidthPercent(node, Length.Type.PERCENT.type().cast(maxWidth.get()));
            }
        }
    }

    private void setMinHeight(long node, NodeStyle style) {
        Length minHeight = style.getMinHeight();
        if (minHeight != null) {
            if (Length.Type.PIXEL.equals(minHeight.type())) {
                Yoga.YGNodeStyleSetMinHeight(node, Length.Type.PIXEL.type().cast(minHeight.get()));
            } else if (Length.Type.PERCENT.equals(minHeight.type())) {
                Yoga.YGNodeStyleSetMinHeightPercent(node, Length.Type.PERCENT.type().cast(minHeight.get()));
            }
        }
    }

    private void setMinWidth(long node, NodeStyle style) {
        Length minWidth = style.getMinWidth();
        if (minWidth != null) {
            if (Length.Type.PIXEL.equals(minWidth.type())) {
                Yoga.YGNodeStyleSetMinWidth(node, Length.Type.PIXEL.type().cast(minWidth.get()));
            } else if (Length.Type.PERCENT.equals(minWidth.type())) {
                Yoga.YGNodeStyleSetMinWidthPercent(node, Length.Type.PERCENT.type().cast(minWidth.get()));
            }
        }
    }
}
