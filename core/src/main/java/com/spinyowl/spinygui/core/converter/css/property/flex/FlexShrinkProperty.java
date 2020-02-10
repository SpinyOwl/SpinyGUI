package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.node.base.Element;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_SHRINK;

public class FlexShrinkProperty extends Property {
    private ValueExtractor<Integer> integerValueExtractor = ValueExtractors.getInstance().getValueExtractor(Integer.class);

    public FlexShrinkProperty() {
        super(FLEX_SHRINK, "0", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, 0, (s, v) -> s.getFlex().setFlexShrink(v), s -> s.getFlex().getFlexShrink(), integerValueExtractor::extract);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || integerValueExtractor.isValid(value);
    }
}
