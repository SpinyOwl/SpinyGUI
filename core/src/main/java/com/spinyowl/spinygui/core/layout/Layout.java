package com.spinyowl.spinygui.core.layout;

/** Layout manager. Used to layout provided element of specified display type. */
public interface Layout<T extends LayoutNode> {

  /**
   * Used to lay out element, and it's child nodes.
   *
   * @param layoutNode element to lay out.
   */
  void layout(T layoutNode, LayoutContext context);
}
