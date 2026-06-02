package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoClassSelector;

public class FocusSelector implements PseudoClassSelector {

  @Override
  public boolean test(Element element) {
    return element.focused();
  }

  @Override
  public String toString() {
    return ":focus";
  }
}
