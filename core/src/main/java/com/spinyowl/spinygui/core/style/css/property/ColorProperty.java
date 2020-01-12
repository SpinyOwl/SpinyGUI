package com.spinyowl.spinygui.core.style.css.property;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class ColorProperty extends Property {

    private ValueExtractor<Color> colorValueExtractor = ValueExtractors.getInstance().getValueExtractor(Color.class);

    public ColorProperty() {
        super(Properties.COLOR, null, true, true);
    }

    public ColorProperty(String value) {
        this();
        setValue(value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        NodeStyle nodeStyle = element.getCalculatedStyle();
        if (INITIAL.equals(value) || INHERIT.equals(value)) {
            // todo: add implementation for initial and inherit values
            return;
        }
        if (value != null && colorValueExtractor.isValid(value)) {
            Color color = colorValueExtractor.extract(value);
            if (color != null) {
                nodeStyle.setColor(color);
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
        return super.isValid() || colorValueExtractor.isValid(getValue());
    }
}
