package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Will be generated when a scrolling device is used, such as a mouse wheel or scrolling area of a
 * touchpad.
 */
@Getter
@ToString
@EqualsAndHashCode
public class SystemScrollEvent extends SystemEvent {

  /** The scroll offset along the x-axis. */
  private final float offsetX;

  /** The scroll offset along the y-axis. */
  private final float offsetY;

  @Builder
  protected SystemScrollEvent(Frame frame, float offsetX, float offsetY) {
    super(frame);
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }
}
