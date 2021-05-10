package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Event, that used only for invalidating tree, so it should be rendered again. */
@Getter
@ToString
@EqualsAndHashCode
public class InvalidateTreeEvent extends Event {

  public InvalidateTreeEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      EventTarget currentTarget) {
    super(source, target, timestamp, currentTarget);
  }

  public InvalidateTreeEvent(
      @NonNull EventTarget source, @NonNull EventTarget target, double timestamp) {
    super(source, target, timestamp);
  }
}
