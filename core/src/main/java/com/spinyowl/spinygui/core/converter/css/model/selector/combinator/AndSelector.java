package com.spinyowl.spinygui.core.converter.css.model.selector.combinator;

import com.spinyowl.spinygui.core.converter.css.model.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;

public class AndSelector extends CombinatorSelector {

  public AndSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element element) {
    return first.test(element) && second.test(element);
  }

}
