package com.spinyowl.spinygui.core.style.stylesheet.selector.combinator;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;

/**
 * General Sibling Selector (~)
 *
 * <p>The general sibling selector selects all elements that are siblings of a specified element.
 *
 * <p>Example: 'first ~ second'.
 */
public class GeneralSiblingSelector extends CombinatorSelector {

  public GeneralSiblingSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element element) {
    if (!second.test(element)) {
      return false;
    }

    Element previous;
    while ((previous = element.previousElementSibling()) != null) {
      if (first.test(previous)) {
        return true;
      }
    }
    return false;
  }
  @Override
  public String toString() {
    return first + " ~ " + second;
  }
}
