package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.pseudo.Before;
import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoElementSelector;

public class BeforeSelector implements PseudoElementSelector {
  @Override
  public boolean test(Element element) {
    return element instanceof Before;
  }

  @Override
  public Specificity specificity() {
    return Specificity.TYPE;
  }

  @Override
  public String toString() {
    return "::before";
  }
}
