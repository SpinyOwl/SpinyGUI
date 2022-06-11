package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Text;

/** Defines branch for text node layout implementations. */
public interface TextLayout extends Layout<Text> {
  void layout(Text element, LayoutContext context);
}
