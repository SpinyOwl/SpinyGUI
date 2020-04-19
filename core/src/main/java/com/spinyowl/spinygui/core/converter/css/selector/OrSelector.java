package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Data;

@Data
public class OrSelector implements StyleSelector {

  private final StyleSelector first;
  private final StyleSelector second;

  @Override
  public boolean test(Element t) {
    return first.test(t) || second.test(t);
  }

}
