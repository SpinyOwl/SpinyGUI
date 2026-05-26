package com.spinyowl.spinygui.core.style.stylesheet.selector;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Specificity;

/**
 * Style selector interface.<br>
 * Style selectors are patterns used to select the elements you want to style.
 */
public interface Selector extends Comparable<Selector> {

  /**
   * Returns true if provided node could be selected using this selector.
   *
   * @param element node to test.
   * @return true if provided node could be selected using this selector.
   */
  boolean test(Element element);

  Specificity specificity();

  default Selector last() {
    return this;
  }

  @Override
  default int compareTo(Selector o) {
    return this.specificity().compareTo(o.specificity());
  }

  /** Used to determine if selector applies to pseudo element. */
  default boolean appliesToPseudoElement() {
    return false;
  }
}
