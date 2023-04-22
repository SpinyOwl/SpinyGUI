package com.spinyowl.spinygui.core.layout.mode;

import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutNode;

/** Defines branch for text node layout implementations. */
public interface TextLayout extends Layout {
  void layout(LayoutNode layoutNode, LayoutContext context);
}
