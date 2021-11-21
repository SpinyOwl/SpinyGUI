package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class WindowIconifyEvent extends Event {
  private final boolean iconified;
}
