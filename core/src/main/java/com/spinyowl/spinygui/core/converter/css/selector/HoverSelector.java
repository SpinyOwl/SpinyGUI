package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.converter.css.parser.annotations.PseudoSelector;
import com.spinyowl.spinygui.core.node.Element;
import lombok.ToString;

@ToString
@PseudoSelector(":hover")
public class HoverSelector implements StyleSelector {

  @Override
  public boolean test(Element element) {
    return element.hovered();
  }

}
