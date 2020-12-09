package com.spinyowl.spinygui.core.converter.css.model.selector.combinator;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;
import java.util.List;

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
public class AdjacentSiblingSelector extends AbstractSiblingSelector {

  public AdjacentSiblingSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element element) {
    List<Element> siblings = allRowIfSecondIsAccepted(element);
    int elementIndex = siblings.indexOf(element);
    if (elementIndex > 0) {
      return first.test(siblings.get(elementIndex - 1));
    }
    return false;
  }

}
