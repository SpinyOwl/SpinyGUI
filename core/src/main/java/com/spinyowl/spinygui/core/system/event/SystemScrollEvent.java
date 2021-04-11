package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Will be generated when a scrolling device is used, such as a mouse wheel or scrolling area of a
 * touchpad.
 */
@Data
@SuperBuilder
public class SystemScrollEvent extends SystemEvent {

  /** The scroll offset along the x-axis. */
  private final float offsetX;

  /** The scroll offset along the y-axis. */
  private final float offsetY;
}
