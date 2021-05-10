package com.spinyowl.spinygui.core.system.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Will be generated when the framebuffer of the specified window is resized. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemFramebufferSizeEvent extends SystemEvent {

  /** The new width, in pixels, of the framebuffer. */
  private final int width;

  /** The new height, in pixels, of the framebuffer. */
  private final int height;

  @Builder
  protected SystemFramebufferSizeEvent(long window, int width, int height) {
    super(window);
    this.width = width;
    this.height = height;
  }
}
