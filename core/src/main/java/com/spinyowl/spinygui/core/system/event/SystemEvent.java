package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Marker interface that defines tree of system events. */
@Data
@SuperBuilder
public abstract class SystemEvent {

  /** The window that received the event. */
  private final long window;
}
