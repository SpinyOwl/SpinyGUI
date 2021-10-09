package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.node.Element;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class NoneLayout extends Layout {

  /**
   * Used to lay out element and it's child nodes.
   *
   * @param element element to lay out.
   */
  @Override
  public void layout(Element element) {
    // do nothing.
  }
}
