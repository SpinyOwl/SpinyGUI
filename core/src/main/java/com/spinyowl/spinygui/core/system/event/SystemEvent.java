package com.spinyowl.spinygui.core.system.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Marker interface that defines tree of system events. */
@Getter
@ToString
@EqualsAndHashCode
public abstract class SystemEvent {

  /** The window that received the event. */
  private final long window;

  protected SystemEvent(long window) {
    this.window = window;
  }
}
