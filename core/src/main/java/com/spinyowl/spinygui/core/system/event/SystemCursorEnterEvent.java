package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Event that generated when the cursor enters or leaves the client area of the window.
 */
@Data
public class SystemCursorEnterEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  public final long window;

  /**
   * <b>{@code true}</b> if the cursor entered the window's content area, or <b>{@code false}</b> if it left it
   */
  public final boolean entered;

}
