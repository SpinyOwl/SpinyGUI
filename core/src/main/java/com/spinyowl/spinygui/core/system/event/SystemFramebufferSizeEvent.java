package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Will be generated when the framebuffer of the specified window is resized.
 */
@Data
public class SystemFramebufferSizeEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  private final long window;

  /**
   * The new width, in pixels, of the framebuffer.
   */
  private final int width;

  /**
   * The new height, in pixels, of the framebuffer.
   */
  private final int height;

}
