package com.spinyowl.spinygui.core.style.stylesheet.selector.combinator;

import com.spinyowl.spinygui.core.style.stylesheet.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;

/**
 * Child Selector (>)
 * <p>
 * The child selector selects all elements that are the children of a specified element.
 * <p>
 * Example: 'first > second'.
 */
public class ChildSelector extends CombinatorSelector {

  public ChildSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element t) {
    return t.parent() != null && second.test(t) && first.test(t.parent());
  }

}
