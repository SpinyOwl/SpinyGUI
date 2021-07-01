package com.spinyowl.spinygui.core.style.stylesheet.selector.combinator;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;

/**
 * Adjacent Sibling Selector (+)
 *
 * <p>The adjacent sibling selector is used to select an element that is directly after another
 * specific element.
 *
 * <p>Sibling elements must have the same parent element, and "adjacent" means "immediately
 * following".
 *
 * <p>Example: 'first + second'.
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
    var previous = element.previousElementSibling();
    return previous != null && first.test(previous);
  }

  @Override
  public String toString() {
    return first + " + " + second;
  }
}
