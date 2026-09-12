package com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.stylesheet.selector.PseudoClassSelector;

/** Matches selected checkbox and radio inputs. */
public class CheckedSelector implements PseudoClassSelector {
  @Override
  public boolean test(Element element) {
    return element instanceof InputElement input && input.checked();
  }

  @Override
  public String toString() {
    return ":checked";
  }
}
