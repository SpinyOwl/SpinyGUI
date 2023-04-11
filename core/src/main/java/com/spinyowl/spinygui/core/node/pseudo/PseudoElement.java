package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import java.util.function.Function;

public class PseudoElement extends Element {

  protected final String name;
  protected final Function<String, ResolvedStyle> stylesAccessor;

  public PseudoElement(String nodeName, Function<String, ResolvedStyle> stylesAccessor) {
    super(nodeName);
    this.name = nodeName;
    this.stylesAccessor = stylesAccessor;
  }

  @Override
  public ResolvedStyle resolvedStyle() {
    return stylesAccessor.apply(this.nodeName());
  }
}
