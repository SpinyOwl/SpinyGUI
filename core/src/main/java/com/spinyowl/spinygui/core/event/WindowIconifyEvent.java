package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class WindowIconifyEvent extends Event {
  private final boolean iconified;

  @Builder
  public WindowIconifyEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      boolean iconified) {
    super(source, target, timestamp, currentTarget);
    this.iconified = iconified;
  }
}
