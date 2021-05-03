package com.spinyowl.spinygui.core.event;

import lombok.experimental.SuperBuilder;

/** Event, that used only for invalidating tree, so it should be rendered again. */
@SuperBuilder(toBuilder = true)
public class InvalidateTreeEvent extends Event {

  public static InvalidateTreeEvent create() {
    return InvalidateTreeEvent.builder().build();
  }
}
