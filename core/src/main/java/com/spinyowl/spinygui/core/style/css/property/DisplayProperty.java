package com.spinyowl.spinygui.core.style.css.property;

import com.spinyowl.spinygui.core.node.base.Container;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Display;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DisplayProperty extends Property {

    public DisplayProperty() {
        super(Properties.DISPLAY, Display.BLOCK.getName(), true, true);
    }

    public DisplayProperty(String value) {
        super(Properties.DISPLAY, Display.BLOCK.getName(), true, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        NodeStyle nodeStyle = element.getCalculatedStyle();

        if (value != null) {
            if (INITIAL.equals(value)) {
                nodeStyle.setDisplay(Display.BLOCK);
            } else if (INHERIT.equals(value)) {
                NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
                if (parentStyle != null) {
                    Display pd = parentStyle.getDisplay();
                    nodeStyle.setDisplay(pd == null ? Display.BLOCK : pd);
                } else {
                    nodeStyle.setDisplay(Display.BLOCK);
                }
            } else if (Display.contains(value)) {
                nodeStyle.setDisplay(Display.of(value));
            }
        }
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || Display.contains(value);
    }

    @Override
    public Set<String> allowedValues() {
        var values = Display.values().stream().map(Display::getName).collect(Collectors.toCollection(HashSet::new));
        values.add(INITIAL);
        values.add(INHERIT);
        return values;
    }
}
