package com.spinyowl.spinygui.core.style.css;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.css.selector.StyleSelector;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class StyleSheet {
    private List<RuleSet> ruleSets;

    public StyleSheet(List<RuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    /**
     * Used to search elements in node tree that are correspond to specified rule set.
     *
     * @param ruleSet     rule set to search elements.
     * @param elementTree element tree to search elements with rules.
     * @return set of nodes that are correspond to specified rule set.
     */
    public static Set<Element> searchElements(RuleSet ruleSet, Element elementTree) {
        Objects.requireNonNull(ruleSet);
        Objects.requireNonNull(elementTree);
        var nodes = new HashSet<Element>();
        var selectors = ruleSet.getSelectors();
        selectors.forEach(selector -> inspectElementTree(elementTree, selector, nodes));
        return nodes;
    }

    /**
     * Used to inspect node tree using specified selector.
     *
     * @param elementTree element tree to search elements.
     * @param selector    selector that used to search nodes.
     * @param elements    element set to store result.
     */
    private static void inspectElementTree(Element elementTree, StyleSelector selector, HashSet<Element> elements) {
        Objects.requireNonNull(elementTree);
        Objects.requireNonNull(selector);

        if (selector.test(elementTree)) {
            elements.add(elementTree);
        }

        elementTree.getChildNodes().stream()
                .filter(n -> n instanceof Element).map(n -> (Element) n)
                .forEach(c -> inspectElementTree(c, selector, elements));
    }

    public List<RuleSet> getRuleSets() {
        return ruleSets;
    }


}
