package com.spinyowl.spinygui.core.style.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderWidthProperty extends Property {
    public static final String THIN   = "thin";
    public static final String MEDIUM = "medium";
    public static final String THICK  = "thick";

    public static final Length THIN_VALUE   = Length.pixel(2);
    public static final Length MEDIUM_VALUE = Length.pixel(4);
    public static final Length THICK_VALUE  = Length.pixel(6);

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public BorderWidthProperty() {
        super(Properties.BORDER_LEFT_WIDTH, MEDIUM, false, true);
    }

    public BorderWidthProperty(String value) {
        super(Properties.BORDER_LEFT_WIDTH, MEDIUM, false, true, value);
    }

    static Length getLength(String value, ValueExtractor<Length> lengthValueExtractor) {
        Length v = null;
        if (THIN.equals(value)) {
            v = THIN_VALUE;
        } else if (MEDIUM.equals(value)) {
            v = MEDIUM_VALUE;
        } else if (THICK.equals(value)) {
            v = THICK_VALUE;
        } else {
            v = lengthValueExtractor.extract(value);
        }
        return v;
    }

    static boolean isValidBorderWidthValue(String borderWidth, ValueExtractor<Length> lengthValueExtractor) {
        return THIN.equalsIgnoreCase(borderWidth) ||
                MEDIUM.equalsIgnoreCase(borderWidth) ||
                THICK.equalsIgnoreCase(borderWidth) ||
                lengthValueExtractor.isValid(borderWidth);
    }

    static void updateTopBorderWidthFromParent(NodeStyle nodeStyle, NodeStyle parentStyle) {
        Length parentTopWidth = parentStyle.getBorder().getTop().getWidth();
        nodeStyle.getBorder().getTop().setWidth(parentTopWidth == null ? MEDIUM_VALUE : parentTopWidth);
    }

    static void updateBottomBorderWidthFromParent(NodeStyle nodeStyle, NodeStyle parentStyle) {
        Length parentBottomWidth = parentStyle.getBorder().getBottom().getWidth();
        nodeStyle.getBorder().getBottom().setWidth(parentBottomWidth == null ? MEDIUM_VALUE : parentBottomWidth);
    }

    static void updateRightBorderWidthFromParent(NodeStyle nodeStyle, NodeStyle parentStyle) {
        Length parentRightWidth = parentStyle.getBorder().getRight().getWidth();
        nodeStyle.getBorder().getRight().setWidth(parentRightWidth == null ? MEDIUM_VALUE : parentRightWidth);
    }

    static void updateLeftBorderWidthFromParent(NodeStyle nodeStyle, NodeStyle parentStyle) {
        Length parentLeftWidth = parentStyle.getBorder().getLeft().getWidth();
        nodeStyle.getBorder().getLeft().setWidth(parentLeftWidth == null ? MEDIUM_VALUE : parentLeftWidth);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        NodeStyle nodeStyle = element.getCalculatedStyle();
        if (INITIAL.equals(value)) {
            setOne(nodeStyle, MEDIUM_VALUE);
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                updateLeftBorderWidthFromParent(nodeStyle, parentStyle);
                updateRightBorderWidthFromParent(nodeStyle, parentStyle);
                updateTopBorderWidthFromParent(nodeStyle, parentStyle);
                updateBottomBorderWidthFromParent(nodeStyle, parentStyle);
            } else {
                setOne(nodeStyle, MEDIUM_VALUE);
            }
        } else {
            String[] values = value.split("\\s+");
            switch (values.length) {
                case 1: {
                    setOne(nodeStyle, getLength(values[0], lengthValueExtractor));
                    return;
                }
                case 2: {
                    setTwo(nodeStyle, values);
                    return;
                }
                case 3: {
                    setThree(nodeStyle, values);
                    return;
                }
                case 4: {
                    setFour(nodeStyle, values);
                    return;
                }
            }
        }
    }

    private void setFour(NodeStyle nodeStyle, String[] values) {
        Length t = getLength(values[0], lengthValueExtractor);
        Length r = getLength(values[1], lengthValueExtractor);
        Length b = getLength(values[2], lengthValueExtractor);
        Length l = getLength(values[3], lengthValueExtractor);
        nodeStyle.getBorder().getLeft().setWidth(l);
        nodeStyle.getBorder().getRight().setWidth(r);
        nodeStyle.getBorder().getTop().setWidth(t);
        nodeStyle.getBorder().getBottom().setWidth(b);
    }

    private void setThree(NodeStyle nodeStyle, String[] values) {
        Length t = getLength(values[0], lengthValueExtractor);
        Length lr = getLength(values[1], lengthValueExtractor);
        Length b = getLength(values[2], lengthValueExtractor);
        nodeStyle.getBorder().getLeft().setWidth(lr);
        nodeStyle.getBorder().getRight().setWidth(lr);
        nodeStyle.getBorder().getTop().setWidth(t);
        nodeStyle.getBorder().getBottom().setWidth(b);
    }

    private void setTwo(NodeStyle nodeStyle, String[] values) {
        Length tb = getLength(values[0], lengthValueExtractor);
        Length lr = getLength(values[1], lengthValueExtractor);
        nodeStyle.getBorder().getLeft().setWidth(lr);
        nodeStyle.getBorder().getRight().setWidth(lr);
        nodeStyle.getBorder().getTop().setWidth(tb);
        nodeStyle.getBorder().getBottom().setWidth(tb);
    }

    private void setOne(NodeStyle nodeStyle, Length value) {
        nodeStyle.getBorder().getLeft().setWidth(value);
        nodeStyle.getBorder().getRight().setWidth(value);
        nodeStyle.getBorder().getTop().setWidth(value);
        nodeStyle.getBorder().getBottom().setWidth(value);
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        if (super.isValid()) {
            return true;
        }
        String[] values = value.split("\\s+");
        if (values.length <= 0 || values.length > 4) {
            return false;
        }
        for (String borderWidth : values) {
            if (!isValidBorderWidthValue(borderWidth, lengthValueExtractor)) {
                return false;
            }
        }

        return true;
    }
}
