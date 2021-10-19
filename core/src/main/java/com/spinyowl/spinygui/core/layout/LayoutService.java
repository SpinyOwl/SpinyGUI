package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Frame;

/**
 * Layout service is an entry point to layout system. Used to layout provided element.
 */
public interface LayoutService {

  /**
   * Used to layout element tree.
   *
   * @param frame frame to lay out.
   * @return layout tree.
   */
  LayoutTree layout(Frame frame);
}
