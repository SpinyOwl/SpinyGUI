package com.spinyowl.spinygui.core.style.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.css.property.border.BorderWidthProperty.*;

public class BorderLeftWidthProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public BorderLeftWidthProperty() {
        super(Properties.BORDER_LEFT_WIDTH, MEDIUM, false, true);
    }

    public BorderLeftWidthProperty(String value) {
        super(Properties.BORDER_LEFT_WIDTH, MEDIUM, false, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        this.update(element, MEDIUM_VALUE,
                (s, v) -> s.getBorder().getLeft().setWidth(v),
                s -> s.getBorder().getLeft().getWidth(),
                (v) -> BorderWidthProperty.getLength(v, lengthValueExtractor));
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() ||
                isValidBorderWidthValue(value, lengthValueExtractor);
    }
}
