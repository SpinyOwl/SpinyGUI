package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
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
        super(Properties.BORDER_WIDTH, MEDIUM, false, true);
    }

    public BorderWidthProperty(String value) {
        super(Properties.BORDER_WIDTH, MEDIUM, false, true, value);
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
        return THIN.equals(borderWidth) ||
                MEDIUM.equals(borderWidth) ||
                THICK.equals(borderWidth) ||
                lengthValueExtractor.isValid(borderWidth);
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
            nodeStyle.getBorder().setWidth(MEDIUM_VALUE);
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                nodeStyle.getBorder().setWidth(parentStyle.getBorder());
            } else {
                nodeStyle.getBorder().setWidth(MEDIUM_VALUE);
            }
        } else {
            String[] values = value.split("\\s+");
            if (values.length == 1) {
                nodeStyle.getBorder().setWidth(
                        getLength(values[0], lengthValueExtractor)
                );
            } else if (values.length == 2) {
                nodeStyle.getBorder().setWidth(
                        getLength(values[0], lengthValueExtractor),
                        getLength(values[1], lengthValueExtractor)
                );
            } else if (values.length == 3) {
                nodeStyle.getBorder().setWidth(
                        getLength(values[0], lengthValueExtractor),
                        getLength(values[1], lengthValueExtractor),
                        getLength(values[2], lengthValueExtractor)
                );
            } else if (values.length == 4) {
                nodeStyle.getBorder().setWidth(
                        getLength(values[0], lengthValueExtractor),
                        getLength(values[1], lengthValueExtractor),
                        getLength(values[2], lengthValueExtractor),
                        getLength(values[3], lengthValueExtractor)
                );
            }
        }
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
        if (values.length == 0 || values.length > 4) {
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
