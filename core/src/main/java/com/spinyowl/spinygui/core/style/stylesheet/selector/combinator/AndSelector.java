package com.spinyowl.spinygui.core.style.stylesheet.selector.combinator;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;

public class AndSelector extends CombinatorSelector {

  public AndSelector(Selector first, Selector second) {
    super(first, second);
  }

  @Override
  public boolean test(Element element) {
    return first.test(element) && second.test(element);
  }
}
