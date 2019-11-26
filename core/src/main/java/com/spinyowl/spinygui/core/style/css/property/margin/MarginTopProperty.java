package com.spinyowl.spinygui.core.style.css.property.margin;

import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginTopProperty extends Property {

    private ValueExtractor<Unit> unitValueExtractor = ValueExtractors.getInstance().getValueExtractor(Unit.class);

    public MarginTopProperty() {
        super(Properties.MARGIN_TOP, null, false, true);
    }

    public MarginTopProperty(String value) {
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
        nodeStyle.setMarginTop(unitValueExtractor.extract(value));
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return unitValueExtractor.isValid(value);
    }
}
