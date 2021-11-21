package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Will be generated when the specified window is iconified or restored. */
@Data
@SuperBuilder
public class SystemWindowIconifyEvent extends SystemEvent {
  /** {@code true} if the window was iconified, or {@code false} if it was restored */
  private final boolean iconified;
}
