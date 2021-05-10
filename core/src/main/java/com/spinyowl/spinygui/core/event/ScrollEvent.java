package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ScrollEvent extends NodeEvent {
  private final float offsetX;
  private final float offsetY;

  @Builder
  public ScrollEvent(
      @NonNull EventTarget source,
      EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      float offsetX,
      float offsetY) {
    super(source, target, timestamp, currentTarget);
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }
}
