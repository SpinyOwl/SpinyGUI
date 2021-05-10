package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class WindowPosEvent extends NodeEvent {
  private final int posX;
  private final int posY;

  @Builder
  public WindowPosEvent(
      @NonNull EventTarget source,
      EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      int posX,
      int posY) {
    super(source, target, timestamp, currentTarget);
    this.posX = posX;
    this.posY = posY;
  }
}
