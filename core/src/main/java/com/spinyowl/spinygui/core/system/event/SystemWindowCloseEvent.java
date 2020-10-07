package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Will be generated when the user attempts to close the specified window, for example by clicking
 * the close widget in the title bar.
 */
@Data
public class SystemWindowCloseEvent implements SystemEvent {

  /**
   * The window that the user attempted to close.
   */
  public final long window;

}
