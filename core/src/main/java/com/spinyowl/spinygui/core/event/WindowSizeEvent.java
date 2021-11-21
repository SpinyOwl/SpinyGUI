package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class WindowSizeEvent extends Event {
  private final int width;
  private final int height;
}
