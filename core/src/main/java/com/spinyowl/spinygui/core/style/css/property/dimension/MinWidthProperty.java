package com.spinyowl.spinygui.core.style.css.property.dimension;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MinWidthProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public MinWidthProperty() {
        super(Properties.MIN_WIDTH, "0px", false, true);
    }

    public MinWidthProperty(String value) {
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
        if (value.equals("none")) {
            nodeStyle.setMinWidth(null);
        } else {
            nodeStyle.setMinWidth(lengthValueExtractor.extract(value));
        }
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || value.equals("none") || lengthValueExtractor.isValid(value);
    }
}
