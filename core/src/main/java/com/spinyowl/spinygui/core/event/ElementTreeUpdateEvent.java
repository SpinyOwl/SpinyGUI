package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;

/**
 * Event, that used only for invalidating tree, so it should be recalculated.
 */
public class ElementTreeUpdateEvent extends NodeEvent<Element> {

  public ElementTreeUpdateEvent() {
    super(null);
  }

}
