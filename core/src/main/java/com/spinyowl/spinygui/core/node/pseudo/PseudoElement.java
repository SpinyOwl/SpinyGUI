package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(exclude = "pseudoParent")
public class PseudoElement extends Element {

  protected final String name;
  protected final Element pseudoParent;

  public PseudoElement(String nodeName, Element pseudoParent) {
    super(nodeName);
    this.name = nodeName;
    this.pseudoParent = pseudoParent;
  }

  @Override
  public ResolvedStyle resolvedStyle() {
    return pseudoParent.resolvedStyle(name);
  }
}
