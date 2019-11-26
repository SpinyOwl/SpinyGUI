package com.spinyowl.spinygui.core.style.css.property.padding;

import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingRightProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public PaddingRightProperty() {
        super(Properties.PADDING_RIGHT, null, false, true);
    }

    public PaddingRightProperty(String value) {
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
        nodeStyle.setPaddingRight(lengthValueExtractor.extract(value));
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return lengthValueExtractor.isValid(value);
    }
}
