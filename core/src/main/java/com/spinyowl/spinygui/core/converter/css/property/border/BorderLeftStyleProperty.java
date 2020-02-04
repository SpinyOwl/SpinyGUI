package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import static com.spinyowl.spinygui.core.converter.css.property.border.BorderColorProperty.DEFAULT_VALUE;

public class BorderLeftStyleProperty extends Property {


    public BorderLeftStyleProperty() {
        super(Properties.BORDER_LEFT_STYLE, DEFAULT_VALUE, false, true);
    }

    public BorderLeftStyleProperty(String value) {
        super(Properties.BORDER_LEFT_STYLE, DEFAULT_VALUE, false, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        this.update(element, BorderStyle.of(BorderStyleProperty.DEFAULT_VALUE),
                (s, c) -> s.getBorder().getLeft().setStyle(c),
                s -> s.getBorder().getLeft().getStyle(),
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
