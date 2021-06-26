package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Will be generated when the specified window moves. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemWindowPosEvent extends SystemEvent {

  /**
   * The new x-coordinate, in screen coordinates, of the upper-left corner of the content area of
   * the window.
   */
  private final int posX;

  /**
   * The new y-coordinate, in screen coordinates, of the upper-left corner of the content area of
   * the window.
   */
  private final int posY;

  @Builder
  protected SystemWindowPosEvent(Frame frame, int posX, int posY) {
    super(frame);
    this.posX = posX;
    this.posY = posY;
  }
}
