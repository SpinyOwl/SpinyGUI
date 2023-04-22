package com.spinyowl.spinygui.core.style.stylesheet.selector.simple;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Specificity;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** The element selector selects elements based on the Element's nodeName. */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ElementSelector implements Selector {

  @NonNull private final String nodeName;

  @Override
  public boolean test(Element node) {
    return nodeName.equals(node.nodeName());
  }

  @Override
  public Specificity specificity() {
    return Specificity.TYPE;
  }

  @Override
  public String toString() {
    return nodeName;
  }
}
