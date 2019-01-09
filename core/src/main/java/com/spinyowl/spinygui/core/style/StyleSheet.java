package com.spinyowl.spinygui.core.style;

import java.util.List;

public class StyleSheet {
    private List<RuleSet> ruleSets;

    public StyleSheet(List<RuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<RuleSet> getRuleSets() {
        return ruleSets;
    }
}
