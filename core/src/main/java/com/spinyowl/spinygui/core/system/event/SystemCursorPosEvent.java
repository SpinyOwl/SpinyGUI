package com.spinyowl.spinygui.core.system.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Will be generated when the cursor is moved. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemCursorPosEvent extends SystemEvent {

  /** The new cursor x-coordinate, relative to the left edge of the content area. */
  private final float posX;

  /** The new cursor y-coordinate, relative to the top edge of the content area. */
  private final float posY;

  @Builder
  protected SystemCursorPosEvent(long window, float posX, float posY) {
    super(window);
    this.posX = posX;
    this.posY = posY;
  }
}
