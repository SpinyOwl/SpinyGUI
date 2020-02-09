package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_SELF;

public class AlignSelfProperty extends Property {

    public AlignSelfProperty() {
        super(ALIGN_SELF, "auto", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, AlignSelf.AUTO, (s, v) -> s.getFlex().setAlignSelf(v), s -> s.getFlex().getAlignSelf(), AlignSelf::valueOf);
    }
}
