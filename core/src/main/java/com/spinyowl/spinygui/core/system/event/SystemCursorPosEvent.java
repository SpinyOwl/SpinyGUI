package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Will be generated when the cursor is moved.
 */
@Data
public class SystemCursorPosEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  public final long window;

  /**
   * The new cursor x-coordinate, relative to the left edge of the content area.
   */
  public final double xpos;

  /**
   * The new cursor y-coordinate, relative to the top edge of the content area.
   */
  public final double ypos;

}
