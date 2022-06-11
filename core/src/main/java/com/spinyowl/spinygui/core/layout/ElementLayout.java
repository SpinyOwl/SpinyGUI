package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Element;

/** Defines branch for element node layout implementations. */
public interface ElementLayout extends Layout<Element> {
  /** {@inheritDoc} */
  void layout(Element element, LayoutContext context);
}
