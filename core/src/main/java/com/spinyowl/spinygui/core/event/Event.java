package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
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
}
