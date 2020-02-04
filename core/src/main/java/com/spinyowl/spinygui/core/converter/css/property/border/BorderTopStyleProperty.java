package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import static com.spinyowl.spinygui.core.converter.css.property.border.BorderColorProperty.DEFAULT_VALUE;

public class BorderTopStyleProperty extends Property {


    public BorderTopStyleProperty() {
        super(Properties.BORDER_TOP_STYLE, DEFAULT_VALUE, false, true);
    }

    public BorderTopStyleProperty(String value) {
        super(Properties.BORDER_TOP_STYLE, DEFAULT_VALUE, false, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        this.update(element, BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE),
                (s, c) -> s.getBorder().getTop().setStyle(c),
                s -> s.getBorder().getTop().getStyle(),
                BorderStyle::of);
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || BorderStyle.contains(value);
    }
}
