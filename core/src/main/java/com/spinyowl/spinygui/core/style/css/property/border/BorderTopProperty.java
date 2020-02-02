package com.spinyowl.spinygui.core.style.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.css.property.border.BorderProperty.isValidBorderProperty;
import static com.spinyowl.spinygui.core.style.css.property.border.BorderWidthProperty.MEDIUM_VALUE;

public class BorderTopProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);
    private ValueExtractor<Color>  colorValueExtractor  = ValueExtractors.getInstance().getValueExtractor(Color.class);

    public BorderTopProperty() {
        super(Properties.BORDER_TOP, BorderProperty.DEFAULT_VALUE, false, true);
    }

    public BorderTopProperty(String value) {
        super(Properties.BORDER_TOP, BorderProperty.DEFAULT_VALUE, false, true, value);
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
            nodeStyle.getBorder().getTop().setWidth(MEDIUM_VALUE);
            nodeStyle.getBorder().getTop().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
            nodeStyle.getBorder().getTop().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                nodeStyle.getBorder().getTop().setWidth(parentStyle.getBorder().getTop().getWidth());
                nodeStyle.getBorder().getTop().setStyle(parentStyle.getBorder().getTop().getStyle());
                nodeStyle.getBorder().getTop().setColor(parentStyle.getBorder().getTop().getColor());
            } else {
                nodeStyle.getBorder().getTop().setWidth(MEDIUM_VALUE);
                nodeStyle.getBorder().getTop().setStyle(BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE));
                nodeStyle.getBorder().getTop().setColor(Color.getColorByName(BorderColorProperty.DEFAULT_VALUE));
            }
        } else {
            String[] values = value.split("\\s+");
            if (values.length == 1) {
                nodeStyle.getBorder().getTop().setStyle(BorderStyle.of(values[0]));
            } else if (values.length == 2) {
                if (BorderStyle.contains(values[0])) {
                    nodeStyle.getBorder().getTop().setStyle(BorderStyle.of(values[0]));
                    nodeStyle.getBorder().getTop().setColor(colorValueExtractor.extract(values[1]));
                } else if (BorderStyle.contains(values[1])) {
                    nodeStyle.getBorder().getTop().setWidth(lengthValueExtractor.extract(values[0]));
                    nodeStyle.getBorder().getTop().setStyle(BorderStyle.of(values[1]));
                }
            } else if (values.length == 3) {
                nodeStyle.getBorder().getTop().setWidth(lengthValueExtractor.extract(values[0]));
                nodeStyle.getBorder().getTop().setStyle(BorderStyle.of(values[1]));
                nodeStyle.getBorder().getTop().setColor(colorValueExtractor.extract(values[2]));
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
