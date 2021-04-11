package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Will be generated when the specified window moves. */
@Data
@SuperBuilder
public class SystemWindowPosEvent extends SystemEvent {

  /**
   * The new x-coordinate, in screen coordinates, of the upper-left corner of the content area of
   * the window.
   */
  private final int posX;

  /**
   * The new y-coordinate, in screen coordinates, of the upper-left corner of the content area of
   * the window.
   */
  private final int posY;
}
