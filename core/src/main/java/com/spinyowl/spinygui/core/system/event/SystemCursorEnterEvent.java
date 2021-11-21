package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Event that generated when the cursor enters or leaves the client area of the window. */
@Data
@SuperBuilder
public class SystemCursorEnterEvent extends SystemEvent {
  /**
   * <b>{@code true}</b> if the cursor entered the window's content area, or <b>{@code false}</b> if
   * it left it
   */
  private final boolean entered;
}
