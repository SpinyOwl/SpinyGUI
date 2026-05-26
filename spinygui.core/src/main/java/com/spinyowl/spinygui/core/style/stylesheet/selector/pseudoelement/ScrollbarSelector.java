package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement;

import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoElementSelector;

public class ScrollbarSelector implements PseudoElementSelector {

  @Override
  public Specificity specificity() {
    return Specificity.TYPE;
  }

  @Override
  public String toString() {
    return "::scrollbar";
  }
}
