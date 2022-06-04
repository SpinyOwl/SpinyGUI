package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Node;

/** Layout manager. Used to layout provided element of specified display type. */
public interface Layout<T extends Node> {

  /**
   * Used to lay out element, and it's child nodes.
   *
   * @param element element to lay out.
   */
  void layout(T element, LayoutContext context);
}
