package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudo_class;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoClassSelector;
import lombok.ToString;

@ToString
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
