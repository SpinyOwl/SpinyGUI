package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;

/**
 * Event, that used only for invalidating tree, so it should be rendered again.
 */
public class InvalidateTreeEvent extends ElementEvent<Element> {

  public InvalidateTreeEvent() {
    super(null);
  }

}
