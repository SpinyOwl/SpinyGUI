package com.spinyowl.spinygui.core.converter.css.model.selector;

import com.spinyowl.spinygui.core.node.Element;

/**
 * Style selector interface.<br> Style selectors are patterns used to select the elements you want
 * to style.
 */
public interface Selector {

  /**
   * Returns true if provided node could be selected using this selector.
   *
   * @param element node to test.
   * @return true if provided node could be selected using this selector.
   */
  boolean test(Element element);
}
