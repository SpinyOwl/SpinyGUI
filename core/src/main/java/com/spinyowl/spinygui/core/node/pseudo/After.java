package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;

public class After extends PseudoElement {

  public static final String NAME = "::after";

  public After(Element scrollbarHolder) {
    super(NAME, scrollbarHolder::resolvedStyle);
  }
}
