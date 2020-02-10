package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.node.base.Element;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_GROW;

public class FlexGrowProperty extends Property {
    private ValueExtractor<Integer> integerValueExtractor = ValueExtractors.getInstance().getValueExtractor(Integer.class);

    public FlexGrowProperty() {
        super(FLEX_GROW, "0", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, 0, (s, v) -> s.getFlex().setFlexGrow(v), s -> s.getFlex().getFlexGrow(), integerValueExtractor::extract);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || integerValueExtractor.isValid(value);
    }
}
