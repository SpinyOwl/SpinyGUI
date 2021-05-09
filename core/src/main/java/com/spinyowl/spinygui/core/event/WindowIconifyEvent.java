package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class WindowIconifyEvent extends Event {
  private final boolean iconified;
}
