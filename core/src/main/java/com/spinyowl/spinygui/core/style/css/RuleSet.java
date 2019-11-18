package com.spinyowl.spinygui.core.style.css;

//import com.spinyowl.spinygui.core.style.css.property.Property;
import com.spinyowl.spinygui.core.style.css.n.Property;
import com.spinyowl.spinygui.core.style.css.selector.StyleSelector;

import java.util.List;

public class RuleSet {
    private List<StyleSelector> selectors;
    private List<Property> properties;

    public RuleSet(List<StyleSelector> selectors, List<Property> properties) {
        this.selectors = selectors;
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<StyleSelector> getSelectors() {
        return selectors;
    }
}
