package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Will be generated when the cursor is moved. */
@Data
@SuperBuilder
public class SystemCursorPosEvent extends SystemEvent {

  /** The new cursor x-coordinate, relative to the left edge of the content area. */
  private final float posX;

  /** The new cursor y-coordinate, relative to the top edge of the content area. */
  private final float posY;
}
