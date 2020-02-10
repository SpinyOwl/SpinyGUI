package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;

import java.util.Arrays;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_CONTENT;

public class AlignContentProperty extends Property {

    public AlignContentProperty() {
        super(ALIGN_CONTENT, "stretch", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, AlignContent.STRETCH, (s, v) -> s.getFlex().setAlignContent(v), s -> s.getFlex().getAlignContent(), AlignContent::valueOf);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || Arrays.stream(AlignContent.values()).anyMatch(ac -> ac.name().equalsIgnoreCase(value));
    }
}
