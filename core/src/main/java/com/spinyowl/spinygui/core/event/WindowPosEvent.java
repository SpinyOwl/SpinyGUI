package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class WindowPosEvent extends Event {
  private final int posX;
  private final int posY;
}
