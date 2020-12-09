package com.spinyowl.spinygui.core.converter.css.model.selector.combinator;

import com.spinyowl.spinygui.core.converter.css.model.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;
import java.util.List;

abstract class AbstractSiblingSelector extends CombinatorSelector {

  protected AbstractSiblingSelector(Selector first, Selector second) {
    super(first, second);
  }

  protected List<Element> allRowIfSecondIsAccepted(Element element) {
    boolean secondTest = second.test(element);
    if (!secondTest) {
      return List.of();
    }

    Element parent = element.parent();
    if (parent == null) {
      return List.of();
    }

    return parent.children();
  }
}
