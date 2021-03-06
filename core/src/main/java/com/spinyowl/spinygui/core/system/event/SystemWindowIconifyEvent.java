package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Will be generated when the specified window is iconified or restored. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemWindowIconifyEvent extends SystemEvent {

  /** {@code true} if the window was iconified, or {@code false} if it was restored */
  private final boolean iconified;

  @Builder
  protected SystemWindowIconifyEvent(Frame frame, boolean iconified) {
    super(frame);
    this.iconified = iconified;
  }
}
