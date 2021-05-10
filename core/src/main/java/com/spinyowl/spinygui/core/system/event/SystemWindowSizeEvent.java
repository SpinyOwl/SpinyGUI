package com.spinyowl.spinygui.core.system.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Will be generated when the specified window is resized. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemWindowSizeEvent extends SystemEvent {

  /** The new width, in screen coordinates, of the window. */
  private final int width;

  /** The new height, in screen coordinates, of the window. */
  private final int height;

  @Builder
  protected SystemWindowSizeEvent(long window, int width, int height) {
    super(window);
    this.width = width;
    this.height = height;
  }
}
