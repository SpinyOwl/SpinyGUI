package com.spinyowl.spinygui.core.converter.css.model;

import com.spinyowl.spinygui.core.converter.css.selector.StyleSelector;
import java.util.List;

public class RuleSet {

  private List<StyleSelector> selectors;
  private List<Declaration> declarations;

  public RuleSet(List<StyleSelector> selectors, List<Declaration> declarations) {
    this.selectors = selectors;
    this.declarations = declarations;
  }

  public List<Declaration> getDeclarations() {
    return declarations;
  }

  public List<StyleSelector> getSelectors() {
    return selectors;
  }
}
