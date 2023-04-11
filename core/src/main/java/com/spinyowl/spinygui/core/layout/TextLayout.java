package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.system.tree.LayoutContext;
import com.spinyowl.spinygui.core.system.tree.LayoutNode;

/** Defines branch for text node layout implementations. */
public interface TextLayout extends Layout {
  void layout(LayoutNode layoutNode, LayoutContext context);
}
