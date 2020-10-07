package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Will be generated when the specified window is iconified or restored.
 */
@Data
public class SystemWindowIconifyEvent implements SystemEvent {

  /**
   * The window that was iconified or restored.
   */
  public final long window;

  /**
   * {@code true} if the window was iconified, or {@code false} if it was restored
   */
  public final boolean iconified;

}
