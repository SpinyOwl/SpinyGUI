package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Event that generated when the cursor enters or leaves the client area of the window. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemCursorEnterEvent extends SystemEvent {

  /**
   * <b>{@code true}</b> if the cursor entered the window's content area, or <b>{@code false}</b> if
   * it left it
   */
  private final boolean entered;

  @Builder
  protected SystemCursorEnterEvent(Frame frame, boolean entered) {
    super(frame);
    this.entered = entered;
  }
}
