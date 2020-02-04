package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.converter.css.RuleSet;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.StyleSheet;

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
            for (Property p : ruleSet.getProperties()) {
                updateStylesFromStyleSheet(element, p);
            }
        }
        for (Element childElement : element.getChildElements()) {
            updateStylesFromStyleSheet(childElement, styleSheet);
        }

    }

    private void updateStylesFromStyleSheet(Element element, Property property) {
        property.updateElementStyle(element);
    }
}
