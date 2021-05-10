package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class WindowSizeEvent extends Event {
  private final int width;
  private final int height;

  @Builder
  public WindowSizeEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      int width,
      int height) {
    super(source, target, timestamp, currentTarget);
    this.width = width;
    this.height = height;
  }
}
