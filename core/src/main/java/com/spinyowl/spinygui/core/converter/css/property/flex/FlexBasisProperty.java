package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.length.Auto;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_BASIS;

public class FlexBasisProperty extends Property {
    private ValueExtractor<Unit> unitValueExtractor = ValueExtractors.getInstance().getValueExtractor(Unit.class);

    public FlexBasisProperty() {
        super(FLEX_BASIS, "auto", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, Auto.AUTO, (s, v) -> s.getFlex().setFlexBasis(v),
                s -> s.getFlex().getFlexBasis(), unitValueExtractor::extract);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || "auto".equalsIgnoreCase(value) || unitValueExtractor.isValid(value);
    }
}
