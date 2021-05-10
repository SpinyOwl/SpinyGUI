package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class NodeEvent extends Event {

  /** Element which cause event generation. */
  @NonNull private final EventTarget source;
  /** Target element to which the event was originally dispatched. */
  private final EventTarget target;

  /**
   * Currently registered target for the event. This is the object to which the event is currently
   * slated to be sent. It's possible this has been changed along the way through retargeting.
   */
  private EventTarget currentTarget;

  protected NodeEvent(
      @NonNull EventTarget source,
      EventTarget target,
      double timestamp,
      EventTarget currentTarget) {
    super(timestamp);
    this.source = source;
    this.target = target;
    this.currentTarget = currentTarget;
  }

  protected NodeEvent(@NonNull EventTarget source, EventTarget target, double timestamp) {
    super(timestamp);
    this.source = source;
    this.target = target;
  }
}
