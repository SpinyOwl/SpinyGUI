package com.spinyowl.spinygui.core.converter.css.model.selector.combinator;

import com.spinyowl.spinygui.core.converter.css.model.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;

/**
 * General Sibling Selector (~)
 * <p>
 * The general sibling selector selects all elements that are siblings of a specified element.
 * <p>
 * Example: 'first ~ second'.
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
}
