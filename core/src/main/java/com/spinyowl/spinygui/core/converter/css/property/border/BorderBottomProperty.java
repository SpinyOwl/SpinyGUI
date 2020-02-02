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

public class BorderBottomProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);
    private ValueExtractor<Color>  colorValueExtractor  = ValueExtractors.getInstance().getValueExtractor(Color.class);

    public BorderBottomProperty() {
        super(Properties.BORDER_BOTTOM, BorderProperty.DEFAULT_VALUE, false, true);
    }

    public BorderBottomProperty(String value) {
        super(Properties.BORDER_BOTTOM, BorderProperty.DEFAULT_VALUE, false, true, value);
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
            nodeStyle.getBorder().getBottom().setWidth(MEDIUM_VALUE);
            nodeStyle.getBorder().getBottom().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
            nodeStyle.getBorder().getBottom().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                nodeStyle.getBorder().getBottom().setWidth(parentStyle.getBorder().getBottom().getWidth());
                nodeStyle.getBorder().getBottom().setStyle(parentStyle.getBorder().getBottom().getStyle());
                nodeStyle.getBorder().getBottom().setColor(parentStyle.getBorder().getBottom().getColor());
            } else {
                nodeStyle.getBorder().getBottom().setWidth(MEDIUM_VALUE);
                nodeStyle.getBorder().getBottom().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
                nodeStyle.getBorder().getBottom().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
            }
        } else {
            String[] values = value.split("\\s+");
            if (values.length == 1) {
                nodeStyle.getBorder().getBottom().setStyle(BorderStyle.of(values[0]));
            } else if (values.length == 2) {
                if (BorderStyle.contains(values[0])) {
                    nodeStyle.getBorder().getBottom().setStyle(BorderStyle.of(values[0]));
                    nodeStyle.getBorder().getBottom().setColor(colorValueExtractor.extract(values[1]));
                } else if (BorderStyle.contains(values[1])) {
                    nodeStyle.getBorder().getBottom().setWidth(lengthValueExtractor.extract(values[0]));
                    nodeStyle.getBorder().getBottom().setStyle(BorderStyle.of(values[1]));
                }
            } else if (values.length == 3) {
                nodeStyle.getBorder().getBottom().setWidth(lengthValueExtractor.extract(values[0]));
                nodeStyle.getBorder().getBottom().setStyle(BorderStyle.of(values[1]));
                nodeStyle.getBorder().getBottom().setColor(colorValueExtractor.extract(values[2]));
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
