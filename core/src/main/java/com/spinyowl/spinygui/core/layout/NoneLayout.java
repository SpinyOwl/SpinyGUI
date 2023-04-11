package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.system.tree.LayoutContext;
import com.spinyowl.spinygui.core.system.tree.LayoutNode;

public class NoneLayout implements Layout {

  /** {@inheritDoc} */
  @Override
  public void layout(LayoutNode element, LayoutContext context) {
    // do nothing, as there is no need to do anything with this or child nodes, it is excluded.
  }
}
