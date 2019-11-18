package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.css.RuleSet;
import com.spinyowl.spinygui.core.style.css.StyleSheet;
import com.spinyowl.spinygui.core.style.css.n.Property;

import java.util.List;
import java.util.Set;

/**
 * Default style manager implementation.
 */
public class DefaultStyleManger implements StyleManager {

    @Override
    public void recalculateStyles(Frame frame) {
        for (Layer layer : frame.getAllLayers()) {
            for (StyleSheet styleSheet : frame.getStyleSheets()) {
                recalculateStyle(layer, styleSheet);
            }
        }
    }

    private void recalculateStyle(Layer layer, StyleSheet styleSheet) {
        List<RuleSet> ruleSets = styleSheet.getRuleSets();
        for (RuleSet ruleSet : ruleSets) {
            Set<Element> nodes = StyleSheet.searchElements(ruleSet, layer);
            for (Element node : nodes) {
                for (Property property : ruleSet.getProperties()) {
//                    property.getValue().updateNodeStyle(node.getCalculatedStyle());
                }
            }
        }
    }
}
