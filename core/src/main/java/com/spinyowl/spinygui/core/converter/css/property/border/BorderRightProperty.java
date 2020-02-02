package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.converter.css.property.border.BorderProperty.isValidBorderProperty;
import static com.spinyowl.spinygui.core.converter.css.property.border.BorderWidthProperty.MEDIUM_VALUE;

public class BorderRightProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);
    private ValueExtractor<Color>  colorValueExtractor  = ValueExtractors.getInstance().getValueExtractor(Color.class);

    public BorderRightProperty() {
        super(Properties.BORDER_RIGHT, BorderProperty.DEFAULT_VALUE, false, true);
    }

    public BorderRightProperty(String value) {
        super(Properties.BORDER_RIGHT, BorderProperty.DEFAULT_VALUE, false, true, value);
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
            nodeStyle.getBorder().getRight().setWidth(MEDIUM_VALUE);
            nodeStyle.getBorder().getRight().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
            nodeStyle.getBorder().getRight().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                nodeStyle.getBorder().getRight().setWidth(parentStyle.getBorder().getRight().getWidth());
                nodeStyle.getBorder().getRight().setStyle(parentStyle.getBorder().getRight().getStyle());
                nodeStyle.getBorder().getRight().setColor(parentStyle.getBorder().getRight().getColor());
            } else {
                nodeStyle.getBorder().getRight().setWidth(MEDIUM_VALUE);
                nodeStyle.getBorder().getRight().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
                nodeStyle.getBorder().getRight().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
            }
        } else {
            String[] values = value.split("\\s+");
            if (values.length == 1) {
                nodeStyle.getBorder().getRight().setStyle(BorderStyle.of(values[0]));
            } else if (values.length == 2) {
                if (BorderStyle.contains(values[0])) {
                    nodeStyle.getBorder().getRight().setStyle(BorderStyle.of(values[0]));
                    nodeStyle.getBorder().getRight().setColor(colorValueExtractor.extract(values[1]));
                } else if (BorderStyle.contains(values[1])) {
                    nodeStyle.getBorder().getRight().setWidth(lengthValueExtractor.extract(values[0]));
                    nodeStyle.getBorder().getRight().setStyle(BorderStyle.of(values[1]));
                }
            } else if (values.length == 3) {
                nodeStyle.getBorder().getRight().setWidth(lengthValueExtractor.extract(values[0]));
                nodeStyle.getBorder().getRight().setStyle(BorderStyle.of(values[1]));
                nodeStyle.getBorder().getRight().setColor(colorValueExtractor.extract(values[2]));
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
