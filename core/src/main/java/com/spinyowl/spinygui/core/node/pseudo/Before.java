package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;

public class Before extends PseudoElement {

  public static final String NAME = "::before";

  public Before(String content, Element pseudoParent) {
    super(NAME, pseudoParent);
    this.addChild(new Text(content));
  }
}
