package com.spinyowl.spinygui.core.converter.css.model.selector.combinator;

import com.spinyowl.spinygui.core.converter.css.model.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;

/**
 * Descendant Selector
 * <p>
 * The descendant selector matches all elements that are descendants of a specified element.
 * <p>
 * Example: 'first second'.
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
    Element parent = element.parent();
    while (parent != null) {
      if (first.test(parent)) {
        return true;
      }
      parent = parent.parent();
    }
    return false;
  }
}
