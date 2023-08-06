package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoClassSelector;

public class HoverSelector implements PseudoClassSelector {

  @Override
  public boolean test(Element element) {
    return element.hovered();
  }

  @Override
  public String toString() {
    return ":hover";
  }
}
