package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

import static com.spinyowl.spinygui.core.converter.css.property.border.BorderColorProperty.DEFAULT_VALUE;

public class BorderRightColorProperty extends Property {

    private ValueExtractor<Color> colorValueExtractor = ValueExtractors.getInstance().getValueExtractor(Color.class);

    public BorderRightColorProperty() {
        super(Properties.BORDER_RIGHT_COLOR, DEFAULT_VALUE, false, true);
    }

    public BorderRightColorProperty(String value) {
        super(Properties.BORDER_RIGHT_COLOR, DEFAULT_VALUE, false, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        this.update(element, Color.getColorByName(DEFAULT_VALUE),
                (s, c) -> s.getBorder().getRight().setColor(c),
                s -> s.getBorder().getRight().getColor(),
                colorValueExtractor::extract);
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || colorValueExtractor.isValid(value);
    }
}
