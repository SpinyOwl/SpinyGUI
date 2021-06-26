package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Created by Shcherbin Alexander on 6/10/2016. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemWindowFocusEvent extends SystemEvent {

  /** {@code true} if the window was focused, or {@code false} if it was defocused */
  private final boolean focused;

  @Builder
  protected SystemWindowFocusEvent(Frame frame, boolean focused) {
    super(frame);
    this.focused = focused;
  }
}
