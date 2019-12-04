package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.StyleSheet;

/**
 * Default style manager implementation.
 */
public class DefaultStyleManger implements StyleManager {

    @Override
    public void recalculateStyles(Frame frame) {
        frame.getAllLayers()
                .forEach(layer -> frame.getStyleSheets()
                        .forEach(styleSheet -> recalculateStyles(layer, styleSheet)));
    }

    public void recalculateStyles(Element element, StyleSheet styleSheet) {
        StyleSheet.searchRules(styleSheet, element)
                .forEach(ruleSet -> ruleSet.getProperties()
                        .forEach(p -> recalculateStyles(element, p)));
        element.getChildElements()
                .forEach(childElement -> recalculateStyles(childElement, styleSheet));

    }

    @Override
    public void recalculateStyles(Element element, Property property) {
        property.updateElementStyle(element);
    }
}
