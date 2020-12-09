package com.spinyowl.spinygui.core.converter.css.model.selector.pseudo_class;

import com.spinyowl.spinygui.core.converter.css.model.selector.PseudoClassSelector;
import com.spinyowl.spinygui.core.node.Element;
import lombok.ToString;

@ToString
public class HoverSelector implements PseudoClassSelector {

  @Override
  public boolean test(Element element) {
    return element.hovered();
  }

}
