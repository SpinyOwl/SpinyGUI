package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Event {

  /** Element which cause event generation. */
  @NonNull private final EventTarget source;
  /** Target element to which the event was originally dispatched. */
  @NonNull private final EventTarget target;
  /** Timestamp of event. */
  private final double timestamp;

  /**
   * Currently registered target for the event. This is the object to which the event is currently
   * slated to be sent. It's possible this has been changed along the way through retargeting.
   */
  private EventTarget currentTarget;

  protected Event(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      EventTarget currentTarget) {
    this.source = source;
    this.target = target;
    this.timestamp = timestamp;
    this.currentTarget = currentTarget;
  }

  protected Event(@NonNull EventTarget source, @NonNull EventTarget target, double timestamp) {
    this.source = source;
    this.target = target;
    this.timestamp = timestamp;
  }
}
