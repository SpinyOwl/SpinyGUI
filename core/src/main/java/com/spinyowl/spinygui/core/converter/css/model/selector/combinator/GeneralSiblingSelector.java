package com.spinyowl.spinygui.core.converter.css.model.selector.combinator;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;
import java.util.List;

/**
 * General Sibling Selector (~)
 * <p>
 * The general sibling selector selects all elements that are siblings of a specified element.
 * <p>
 * Example: 'first ~ second'.
 */
public class GeneralSiblingSelector extends AbstractSiblingSelector {

  public GeneralSiblingSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element element) {
    List<Element> siblings = allRowIfSecondIsAccepted(element);

    int elementIndex = siblings.indexOf(element);
    if (elementIndex <= 0) {
      return false;
    }

    siblings = siblings.subList(0, elementIndex);
    for (int siblingsSize = siblings.size(), i = siblingsSize - 1; i >= 0; i--) {
      Element sibling = siblings.get(i);
      if (first.test(sibling)) {
        return true;
      }
    }
    return false;
  }
}
