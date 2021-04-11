package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Created by Shcherbin Alexander on 6/10/2016. */
@Data
@SuperBuilder
public class SystemWindowFocusEvent extends SystemEvent {

  /** {@code true} if the window was focused, or {@code false} if it was defocused */
  private final boolean focused;
}
