package com.spinyowl.spinygui.core.style.css.handler;

import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.PropertyHandler;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class ColorPropertyHandler implements PropertyHandler {

    private ValueExtractor<Color> colorValueExtractor = ValueExtractors.getInstance().getValueExtractor(Color.class);

    @Override
    public void updateNodeStyle(NodeStyle nodeStyle, Property property) {
        String value = property.getValue();
        if (value != null && colorValueExtractor.isValid(value)) {
            Color color = colorValueExtractor.extract(value);
            if (color != null) {
                nodeStyle.setColor(color);
            }
        }
    }
}
