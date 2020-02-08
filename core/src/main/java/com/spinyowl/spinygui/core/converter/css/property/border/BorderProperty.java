package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.converter.css.property.border.BorderWidthProperty.MEDIUM_VALUE;

public class BorderProperty extends Property {

    public static final String DEFAULT_VALUE = "medium none transparent";

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);
    private ValueExtractor<Color>  colorValueExtractor  = ValueExtractors.getInstance().getValueExtractor(Color.class);

    public BorderProperty() {
        super(Properties.BORDER, DEFAULT_VALUE, false, true);
    }

    public BorderProperty(String value) {
        super(Properties.BORDER, DEFAULT_VALUE, false, true, value);
    }

    public static boolean isValidBorderProperty(String value, ValueExtractor<Length> lengthValueExtractor, ValueExtractor<Color> colorValueExtractor) {
        String[] values = value.split("\\s+");

        if (values.length == 0 || values.length > 3) {
            return false;
        }

        if (values.length == 1) {
            return BorderStyle.contains(values[0]);
        } else if (values.length == 2) {
            return BorderWidthProperty.isValidBorderWidthValue(values[0], lengthValueExtractor)
                    && BorderStyle.contains(values[1]) ||
                    BorderStyle.contains(values[0]) && colorValueExtractor.isValid(values[1]);
        } else {
            return BorderWidthProperty.isValidBorderWidthValue(values[0], lengthValueExtractor)
                    && BorderStyle.contains(values[1]) && colorValueExtractor.isValid(values[2]);
        }
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
            nodeStyle.getBorder().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
            nodeStyle.getBorder().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                nodeStyle.getBorder().setWidth(parentStyle.getBorder());
                nodeStyle.getBorder().setStyle(parentStyle.getBorder());
                nodeStyle.getBorder().setColor(parentStyle.getBorder());
            } else {
                nodeStyle.getBorder().setWidth(MEDIUM_VALUE);
                nodeStyle.getBorder().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
                nodeStyle.getBorder().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
            }
        } else {
            String[] values = value.split("\\s+");
            if (values.length == 1) {
                nodeStyle.getBorder().setStyle(BorderStyle.of(values[0]));
            } else if (values.length == 2) {
                if (BorderStyle.contains(values[0])) {
                    nodeStyle.getBorder().setStyle(BorderStyle.of(values[0]));
                    nodeStyle.getBorder().setColor(colorValueExtractor.extract(values[1]));
                } else if (BorderStyle.contains(values[1])) {
                    nodeStyle.getBorder().setWidth(lengthValueExtractor.extract(values[0]));
                    nodeStyle.getBorder().setStyle(BorderStyle.of(values[1]));
                }
            } else if (values.length == 3) {
                nodeStyle.getBorder().setWidth(lengthValueExtractor.extract(values[0]));
                nodeStyle.getBorder().setStyle(BorderStyle.of(values[1]));
                nodeStyle.getBorder().setColor(colorValueExtractor.extract(values[2]));
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
        return super.isValid() && isValidBorderProperty(value, lengthValueExtractor, colorValueExtractor);
    }
}
