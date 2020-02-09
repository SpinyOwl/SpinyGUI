package com.spinyowl.spinygui.core.converter.css.property.dimension;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MaxHeightProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public MaxHeightProperty() {
        super(Properties.MAX_HEIGHT, "none", false, true);
    }

    public MaxHeightProperty(String value) {
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
        update(element, (Length) null, NodeStyle::setMaxHeight, NodeStyle::getMaxHeight,
                (v) -> "none".equalsIgnoreCase(value) ? null : lengthValueExtractor.extract(value));
      }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || "none".equalsIgnoreCase(value) || lengthValueExtractor.isValid(value);
    }
}
