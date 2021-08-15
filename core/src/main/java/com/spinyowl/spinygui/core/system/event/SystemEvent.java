package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Marker interface that defines tree of system events. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemEvent {

  /** The frame that received the event. */
  private final Frame frame;

  protected SystemEvent(Frame frame) {
    this.frame = frame;
  }
}
