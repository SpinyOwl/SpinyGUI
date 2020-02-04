package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import static com.spinyowl.spinygui.core.converter.css.property.border.BorderStyleProperty.DEFAULT_VALUE;

public class BorderBottomStyleProperty extends Property {

    public BorderBottomStyleProperty() {
        super(Properties.BORDER_BOTTOM_STYLE, DEFAULT_VALUE, false, true);
    }

    public BorderBottomStyleProperty(String value) {
        super(Properties.BORDER_BOTTOM_STYLE, DEFAULT_VALUE, false, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        this.update(element, BorderStyle.of(DEFAULT_VALUE),
                (s, c) -> s.getBorder().getBottom().setStyle(c),
                s -> s.getBorder().getBottom().getStyle(),
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
