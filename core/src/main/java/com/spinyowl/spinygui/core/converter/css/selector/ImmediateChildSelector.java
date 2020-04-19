package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Data;

/**
 * Returns true if node could be selected with selector 'first > second'
 */
@Data
public class ImmediateChildSelector implements StyleSelector {

  private final StyleSelector first;
  private final StyleSelector second;

  @Override
  public boolean test(Element t) {
    return t.parent() != null && second.test(t) && first.test(t.parent());
  }

}
