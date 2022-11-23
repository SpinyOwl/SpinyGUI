package com.spinyowl.spinygui.core.style.stylesheet.selector;

import com.spinyowl.spinygui.core.style.stylesheet.Specificity;

/** Interface for pseudo-element selectors, which used to define a special inner part of element. */
public interface PseudoElementSelector extends Selector {

  @Override
  default Specificity specificity() {
    return Specificity.TYPE;
  }
}
