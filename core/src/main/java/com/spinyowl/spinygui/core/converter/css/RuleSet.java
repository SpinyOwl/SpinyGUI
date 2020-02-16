package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.converter.css.selector.StyleSelector;
import java.util.List;

public class RuleSet {

    private List<StyleSelector> selectors;
    private List<Rule> rules;

    public RuleSet(List<StyleSelector> selectors, List<Rule> rules) {
        this.selectors = selectors;
        this.rules = rules;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<StyleSelector> getSelectors() {
        return selectors;
    }
}
