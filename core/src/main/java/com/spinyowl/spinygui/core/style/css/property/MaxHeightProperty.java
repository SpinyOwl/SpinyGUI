package com.spinyowl.spinygui.core.style.css.property;

import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MaxHeightProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public MaxHeightProperty() {
        super(Properties.HEIGHT, "none", false, true);
    }

    public MaxHeightProperty(String value) {
        this();
        setValue(value);
    }

    /**
     * Used to update node style with this property.
     *
     * @param nodeStyle node style to update.
     */
    @Override
    protected void updateNodeStyle(NodeStyle nodeStyle) {
        if (value.equalsIgnoreCase("none")) {
            nodeStyle.setMaxHeight(null);
        } else {
            nodeStyle.setMaxHeight(lengthValueExtractor.extract(value));
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
