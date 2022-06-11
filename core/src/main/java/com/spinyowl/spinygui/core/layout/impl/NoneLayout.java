package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.node.Element;

public class NoneLayout implements ElementLayout {

  /** {@inheritDoc} */
  @Override
  public void layout(Element element, LayoutContext context) {
    // do nothing, as there is no need to do anything with this or child nodes, it is excluded.
  }
}
