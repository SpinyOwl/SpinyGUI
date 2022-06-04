package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.layout.Layout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.node.Element;

public class NoneLayout implements Layout<Element> {

  /**
   * Used to lay out element, and it's child nodes.
   *
   * @param element element to lay out.
   */
  @Override
  public void layout(Element element, LayoutContext context) {
    // do nothing.
  }
}
