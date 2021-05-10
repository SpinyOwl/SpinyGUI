package com.spinyowl.spinygui.core.event;

import lombok.Data;

@Data
public abstract class Event {

  /** Timestamp of event. */
  private final double timestamp;
}
