package com.spinyowl.spinygui.core.layout;

/** Defines branch for text node layout implementations. */
public interface TextLayout extends Layout<LayoutNode> {
  void layout(LayoutNode layoutNode, LayoutContext context);
}
