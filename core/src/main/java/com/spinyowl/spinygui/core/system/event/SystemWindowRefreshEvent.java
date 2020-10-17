package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Will be generated when the client area of the specified window needs to be redrawn, for example
 * if the window has been exposed after having been covered by another window.
 */
@Data
public class SystemWindowRefreshEvent implements SystemEvent {

  /**
   * The window whose content needs to be refreshed/
   */
  private final long window;
}
