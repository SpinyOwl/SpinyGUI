package com.spinyowl.spinygui.core.style.css.property.dimension;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MaxWidthProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public MaxWidthProperty() {
        super(Properties.WIDTH, "none", false, true);
    }

    public MaxWidthProperty(String value) {
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
        if (INITIAL.equalsIgnoreCase(value) || INHERIT.equalsIgnoreCase(value)) {
            // todo: add implementation for initial and inherit values
            return;
        }
        if (value.equalsIgnoreCase("none")) {
            nodeStyle.setMaxWidth(null);
        } else {
            nodeStyle.setMaxWidth(lengthValueExtractor.extract(value));
        }
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || value.equalsIgnoreCase("none") || lengthValueExtractor.isValid(value);
    }
}
