package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.PropertyHandler;
import com.spinyowl.spinygui.core.style.css.RuleSet;
import com.spinyowl.spinygui.core.style.css.StyleSheet;

import java.util.List;

/**
 * Default style manager implementation.
 */
public class DefaultStyleManger implements StyleManager {

    @Override
    public void recalculateStyles(Frame frame) {
        List<StyleSheet> styleSheets = frame.getStyleSheets();
        for (Layer layer : frame.getAllLayers()) {
            for (StyleSheet styleSheet : styleSheets) {
                recalculateStyles(layer, styleSheet);
            }
        }
    }

    public void recalculateStyles(Element element, StyleSheet styleSheet) {
        List<RuleSet> ruleSets = styleSheet.getRuleSets();
        ruleSets
                .forEach(ruleSet -> StyleSheet.searchElements(ruleSet, element)
                        .forEach(el -> ruleSet.getProperties()
                                .forEach(p -> recalculateStyles(el, p))));
    }

    @Override
    public void recalculateStyles(Element element, Property property) {
        PropertyHandler propertyHandler = property.getPropertyHandler();
        if (propertyHandler != null) {
            propertyHandler.updateNodeStyle(element.getCalculatedStyle(), property);
        }
    }
}
