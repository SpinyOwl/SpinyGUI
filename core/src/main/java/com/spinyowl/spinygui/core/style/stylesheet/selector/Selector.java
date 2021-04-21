package com.spinyowl.spinygui.core.style.stylesheet.selector;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Specificity;

/**
 * Style selector interface.<br>
 * Style selectors are patterns used to select the elements you want to style.
 */
public interface Selector {

  /**
   * Returns true if provided node could be selected using this selector.
   *
   * @param element node to test.
   * @return true if provided node could be selected using this selector.
   */
  boolean test(Element element);

  Specificity specificity();

  static Selector mostSpecific(Selector left, Selector right) {
    if (left == null) {
      return right;
    }
    if (right == null) {
      return left;
    }
    return left.specificity().compareTo(right.specificity()) > 0 ? left : right;
  }
}
