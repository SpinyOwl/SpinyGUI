package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.DefaultFrame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.converter.css.model.Declaration;
import com.spinyowl.spinygui.core.converter.css.model.RuleSet;
import com.spinyowl.spinygui.core.converter.css.model.StyleSheet;
import com.spinyowl.spinygui.core.node.Element;

/**
 * Default style manager implementation.
 */
public class DefaultStyleManger implements StyleManager {

  @Override
  public void recalculateStyles(DefaultFrame frame) {
    // TODO implement styling according to CSS Specificity

    for (Layer layer : frame.layers()) {
      for (StyleSheet styleSheet : frame.styleSheets()) {
        updateStylesFromStyleSheet(layer, styleSheet);
      }
    }

  }

  private void updateStylesFromStyleSheet(Element element, StyleSheet styleSheet) {
    for (RuleSet ruleSet : styleSheet.searchSpecificRules(element)) {
      for (Declaration<?> p : ruleSet.declarations()) {

        updateStylesFromStyleSheet(element, p);
      }
    }
    for (Element childElement : element.children()) {
      updateStylesFromStyleSheet(childElement, styleSheet);
    }

  }

  private void updateStylesFromStyleSheet(Element element, Declaration<?> declaration) {
    declaration.property().computeAndApply(element, declaration.value());
  }
}
