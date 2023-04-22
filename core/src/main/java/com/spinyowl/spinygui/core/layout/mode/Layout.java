package com.spinyowl.spinygui.core.layout.mode;

import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutNode;

/** Layout manager. Used to layout provided element of specified display type. */
public interface Layout {

  /**
   * Used to lay out element, and it's child nodes.
   *
   * @param layoutNode element to lay out.
   */
  void layout(LayoutNode layoutNode, LayoutContext context);
}
