package com.spinyowl.spinygui.core.system.event;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Will be generated when the cursor is moved.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class SystemCursorPosEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  private final long window;

  /**
   * The new cursor x-coordinate, relative to the left edge of the content area.
   */
  private final float posX;

  /**
   * The new cursor y-coordinate, relative to the top edge of the content area.
   */
  private final float posY;

}
