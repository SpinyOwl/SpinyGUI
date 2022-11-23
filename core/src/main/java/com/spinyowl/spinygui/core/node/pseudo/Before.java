package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;

public class Before extends PseudoElement {

  public static final String NAME = "::before";

  public Before(Element scrollbarHolder) {
    super(NAME, scrollbarHolder);
  }
}
