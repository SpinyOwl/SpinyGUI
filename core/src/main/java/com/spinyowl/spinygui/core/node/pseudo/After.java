package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;

public class After extends PseudoElement {

  public static final String NAME = "::after";

  public After(String content, Element pseudoParent) {
    super(NAME, pseudoParent);
    this.addChild(new Text(content));
  }
}
