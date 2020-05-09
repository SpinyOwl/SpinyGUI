package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Data;

@Data
public class TypeSelector implements StyleSelector {

  private final String nodeName;

  @Override
  public boolean test(Element node) {
    return node.nodeName().equals(nodeName);
  }

}
