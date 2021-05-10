package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Event, that used only for invalidating tree, so it should be rendered again. */
@Getter
@ToString
@EqualsAndHashCode
public class InvalidateTreeEvent extends Event {

  @Builder
  public InvalidateTreeEvent(double timestamp) {
    super(timestamp);
  }
}
