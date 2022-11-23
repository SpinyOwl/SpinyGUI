package com.spinyowl.spinygui.core.layout;

/** Defines branch for element node layout implementations. */
public interface ElementLayout extends Layout<LayoutElement> {
  /** {@inheritDoc} */
  void layout(LayoutElement layoutNode, LayoutContext context);
}
