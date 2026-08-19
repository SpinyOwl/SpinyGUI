package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoClassSelector;

/** Matches supported form controls that currently have the boolean {@code disabled} attribute. */
public class DisabledSelector implements PseudoClassSelector {

  @Override
  public boolean test(Element element) {
    return element.disabled();
  }

  @Override
  public String toString() {
    return ":disabled";
  }
}
