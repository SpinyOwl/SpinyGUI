package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.DefaultFrame;
import com.spinyowl.spinygui.core.converter.css.model.Declaration;
import com.spinyowl.spinygui.core.converter.css.model.StyleSheet;
import com.spinyowl.spinygui.core.node.Element;

/**
 * Default style manager implementation.
 */
public class DefaultStyleManger implements StyleManager {

  @Override
  public void recalculateStyles(DefaultFrame frame) {
    frame.layers().forEach(layer -> frame.styleSheets()
        .forEach(styleSheet -> updateStylesFromStyleSheet(layer, styleSheet)));
  }

  private void updateStylesFromStyleSheet(Element element, StyleSheet styleSheet) {
    styleSheet.searchSpecificRules(element).forEach(
        ruleSet -> ruleSet.declarations().forEach(p -> updateStylesFromStyleSheet(element, p)));
    element.children()
        .forEach(childElement -> updateStylesFromStyleSheet(childElement, styleSheet));

  }

  private void updateStylesFromStyleSheet(Element element, Declaration<?> declaration) {
    declaration.property().computeAndApply(element, declaration.value());
  }
}
