package com.spinyowl.spinygui.core.converter.css.property.background;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.Color;

public class BackgroundColorProperty extends Property {

    private ValueExtractor<Color> colorValueExtractor = ValueExtractors.getInstance().getValueExtractor(Color.class);

    public BackgroundColorProperty() {
        super(Properties.BACKGROUND_COLOR, "transparent", false, true);
    }

    public BackgroundColorProperty(String value) {
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
        this.update(element, Color.TRANSPARENT,
                (s, c) -> s.getBackground().setColor(c),
                s -> s.getBackground().getColor(),
                colorValueExtractor::extract);
    }


    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || colorValueExtractor.isValid(getValue());
    }
}
