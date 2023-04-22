package com.spinyowl.spinygui.core.layout;

/** Used to build layout tree from provided styled node. */
public interface LayoutTreeBuilder {

  /**
   * Used to build layout tree from provided styled node.
   *
   * @param root styled node to build layout tree from.
   * @return layout tree root.
   */
  LayoutNode build(StyledNode root);
}
