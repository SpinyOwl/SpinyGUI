package com.spinyowl.spinygui.core.style.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderStyleProperty extends Property {

    public static final String DEFAULT_VALUE = "solid";

    public BorderStyleProperty() {
        super(Properties.BORDER_STYLE, DEFAULT_VALUE, false, true);
    }

    public BorderStyleProperty(String value) {
        super(Properties.BORDER_STYLE, DEFAULT_VALUE, false, true, value);
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
            nodeStyle.getBorder().setStyle(BorderStyle.of(DEFAULT_VALUE));
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                nodeStyle.getBorder().setStyle(parentStyle.getBorder());
            } else {
                nodeStyle.getBorder().setStyle(BorderStyle.of(DEFAULT_VALUE));
            }
        } else {
            String[] values = value.split("\\s+");
            if (values.length == 1) {
                nodeStyle.getBorder().setStyle(
                        BorderStyle.of(values[0]));
            } else if (values.length == 2) {
                nodeStyle.getBorder().setStyle(
                        BorderStyle.of(values[0]),
                        BorderStyle.of(values[1]));
            } else if (values.length == 3) {
                nodeStyle.getBorder().setStyle(
                        BorderStyle.of(values[0]),
                        BorderStyle.of(values[1]),
                        BorderStyle.of(values[2])
                );
            } else if (values.length == 4) {
                nodeStyle.getBorder().setStyle(
                        BorderStyle.of(values[0]),
                        BorderStyle.of(values[1]),
                        BorderStyle.of(values[2]),
                        BorderStyle.of(values[3])
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
        for (String value : values) {
            if (!BorderStyle.contains(value)) {
                return false;
            }
        }

        return true;
    }
}
