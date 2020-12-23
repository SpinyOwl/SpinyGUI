package com.spinyowl.spinygui.core.converter.css.model.selector.simple;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Specificity;
import com.spinyowl.spinygui.core.node.Element;
import lombok.Data;

/**
 * The element selector selects elements based on the {@link Element#nodeName()}.
 */
@Data
public class ElementSelector implements Selector {

  private final String nodeName;

  @Override
  public boolean test(Element node) {
    return node.nodeName().equals(nodeName);
  }

  @Override
  public Specificity specificity() {
    return Specificity.of(1, 0, 0);
  }
}
