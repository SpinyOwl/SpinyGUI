package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Data;

@Data
public class ChildSelector implements StyleSelector {

  private final StyleSelector first;
  private final StyleSelector second;

  @Override
  public boolean test(Element element) {
    boolean componentTest = second.test(element);
    if (!componentTest) {
      return false;
    }
    Element parent = element.parent();
    while (parent != null) {
      if (first.test(parent)) {
        return true;
      }
      parent = parent.parent();
    }
    return false;
  }
}
