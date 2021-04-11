package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Will be generated when the specified window is resized. */
@Data
@SuperBuilder
public class SystemWindowSizeEvent extends SystemEvent {

  /** The new width, in screen coordinates, of the window. */
  private final int width;

  /** The new height, in screen coordinates, of the window. */
  private final int height;
}
