package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Defines tree of system events. */
@Data
@SuperBuilder
public class SystemEvent {

  /** The frame that received the event. */
  private final Frame frame;
}
