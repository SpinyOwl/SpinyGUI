package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.converter.css.selector.StyleSelector;

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
