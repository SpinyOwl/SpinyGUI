package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;

import java.util.Arrays;

import static com.spinyowl.spinygui.core.converter.css.Properties.JUSTIFY_CONTENT;

public class JustifyContentProperty extends Property {

    public JustifyContentProperty() {
        super(JUSTIFY_CONTENT, "flex-start", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, JustifyContent.FLEX_START, (s, v) -> s.getFlex().setJustifyContent(v), s -> s.getFlex().getJustifyContent(), JustifyContent::valueOf);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || Arrays.stream(JustifyContent.values()).anyMatch(ac -> ac.name().equalsIgnoreCase(value));
    }
}
