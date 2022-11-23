package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.ResolvedStyle;

public class PseudoElement extends Element {

  protected final Element pseudoElementHolder;
  protected final String name;

  public PseudoElement(String nodeName, Element pseudoElementHolder) {
    super(nodeName);
    this.name = nodeName;
    this.pseudoElementHolder = pseudoElementHolder;
  }

  @Override
  public ResolvedStyle resolvedStyle() {
    return pseudoElementHolder.resolvedStyle(name);
  }
}
