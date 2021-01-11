package com.spinyowl.spinygui.core.converter.css.model.selector.combinator;

import com.spinyowl.spinygui.core.converter.css.model.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;

/**
 * Adjacent Sibling Selector (+)
 * <p>
 * The adjacent sibling selector is used to select an element that is directly after another
 * specific element.
 * <p>
 * Sibling elements must have the same parent element, and "adjacent" means "immediately
 * following".
 * <p>
 * Example: 'first + second'.
 */
public class AdjacentSiblingSelector extends CombinatorSelector {

  public AdjacentSiblingSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element element) {
    if (!second.test(element)) {
      return false;
    }
    Element previous = element.previousElementSibling();
    return previous != null && first.test(previous);
  }

}
