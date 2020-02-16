package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.converter.css.Rule;
import com.spinyowl.spinygui.core.converter.css.RuleSet;
import com.spinyowl.spinygui.core.converter.css.StyleSheet;
import com.spinyowl.spinygui.core.node.base.Element;

/**
 * Default style manager implementation.
 */
public class DefaultStyleManger implements StyleManager {

    @Override
    public void recalculateStyles(Frame frame) {
        for (Layer layer : frame.getAllLayers()) {
            for (StyleSheet styleSheet : frame.getStyleSheets()) {
                updateStylesFromStyleSheet(layer, styleSheet);
            }
        }

        // TODO get styles from stylesheet and update elements node styles
    }

    private void updateStylesFromStyleSheet(Element element, StyleSheet styleSheet) {
        for (RuleSet ruleSet : StyleSheet.searchRules(styleSheet, element)) {
            for (Rule<?> p : ruleSet.getRules()) {

                updateStylesFromStyleSheet(element, p);
            }
        }
        for (Element childElement : element.getChildElements()) {
            updateStylesFromStyleSheet(childElement, styleSheet);
        }

    }

    private void updateStylesFromStyleSheet(Element element, Rule<?> rule) {
        rule.getProperty().computeAndApply(element, rule.getValue());
    }
}
