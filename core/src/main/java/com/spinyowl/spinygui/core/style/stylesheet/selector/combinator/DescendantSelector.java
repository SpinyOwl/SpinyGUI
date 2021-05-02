package com.spinyowl.spinygui.core.style.stylesheet.selector.combinator;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;

/**
 * Descendant Selector
 *
 * <p>The descendant selector matches all elements that are descendants of a specified element.
 *
 * <p>Example: 'first second'.
 */
public class DescendantSelector extends CombinatorSelector {

  public DescendantSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element element) {
    boolean componentTest = second.test(element);
    if (!componentTest) {
      return false;
    }
    var parent = element.parent();
    while (parent != null) {
      if (first.test(parent)) {
        return true;
      }
      parent = parent.parent();
    }
    return false;
  }
}
