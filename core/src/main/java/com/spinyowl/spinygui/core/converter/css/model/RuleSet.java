package com.spinyowl.spinygui.core.converter.css.model;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import java.util.List;

public class RuleSet {

  private List<Selector> selectors;
  private List<Declaration> declarations;

  public RuleSet(List<Selector> selectors, List<Declaration> declarations) {
    this.selectors = selectors;
    this.declarations = declarations;
  }

  public List<Declaration> getDeclarations() {
    return declarations;
  }

  public List<Selector> getSelectors() {
    return selectors;
  }
}
