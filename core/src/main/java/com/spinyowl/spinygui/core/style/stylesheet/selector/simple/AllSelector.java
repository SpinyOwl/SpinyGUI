package com.spinyowl.spinygui.core.style.stylesheet.selector.simple;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;

public class AllSelector implements Selector {

  /**
   * Returns true if provided node could be selected using this selector.
   *
   * @param element node to test.
   * @return true if provided node could be selected using this selector.
   */
  @Override
  public boolean test(Element element) {
    return true;
  }

  @Override
  public Specificity specificity() {
    return Specificity.ZERO;
  }

  @Override
  public String toString() {
    return "*";
  }
}
