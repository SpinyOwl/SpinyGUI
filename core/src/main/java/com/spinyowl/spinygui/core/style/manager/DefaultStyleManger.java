package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.converter.css.Declaration;
import com.spinyowl.spinygui.core.converter.css.RuleSet;
import com.spinyowl.spinygui.core.converter.css.StyleSheet;
import com.spinyowl.spinygui.core.node.Element;

/**
 * Default style manager implementation.
 */
public class DefaultStyleManger implements StyleManager {

  @Override
  public void recalculateStyles(Frame frame) {
    // TODO implement styling according to CSS Specificity

    for (Layer layer : frame.allLayers()) {
      for (StyleSheet styleSheet : frame.getStyleSheets()) {
        updateStylesFromStyleSheet(layer, styleSheet);
      }
    }

  }

  private void updateStylesFromStyleSheet(Element element, StyleSheet styleSheet) {
    for (RuleSet ruleSet : StyleSheet.searchRules(styleSheet, element)) {
      for (Declaration<?> p : ruleSet.getDeclarations()) {

        updateStylesFromStyleSheet(element, p);
      }
    }
    for (Element childElement : element.children()) {
      updateStylesFromStyleSheet(childElement, styleSheet);
    }

  }

  private void updateStylesFromStyleSheet(Element element, Declaration<?> declaration) {
    declaration.getProperty().computeAndApply(element, declaration.getValue());
  }
}
