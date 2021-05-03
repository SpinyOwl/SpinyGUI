package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Will be generated when the framebuffer of the specified window is resized. */
@Data
@SuperBuilder(toBuilder = true)
public class SystemFramebufferSizeEvent extends SystemEvent {

  /** The new width, in pixels, of the framebuffer. */
  private final int width;

  /** The new height, in pixels, of the framebuffer. */
  private final int height;
}
