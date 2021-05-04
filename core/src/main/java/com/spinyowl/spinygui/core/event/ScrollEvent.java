package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class ScrollEvent extends Event {
  private final float offsetX;
  private final float offsetY;
}
