package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Will be generated when the specified window is resized.
 */
@Data
public class SystemWindowSizeEvent implements SystemEvent {

  /**
   * The window that was resized.
   */
  private final long window;

  /**
   * The new width, in screen coordinates, of the window.
   */
  private final int width;

  /**
   * The new height, in screen coordinates, of the window.
   */
  private final int height;

}
