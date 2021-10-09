package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Element;
import lombok.experimental.SuperBuilder;

/** Layout manager. Used to layout provided element of specified display type. */
@SuperBuilder
public abstract class Layout {

  /**
   * Used to lay out element and it's child nodes.
   *
   * @param element element to lay out.
   */
  public abstract void layout(Element element);
}
