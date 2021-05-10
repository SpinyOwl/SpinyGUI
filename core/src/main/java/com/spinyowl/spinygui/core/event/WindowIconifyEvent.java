package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class WindowIconifyEvent extends NodeEvent {
  private final boolean iconified;

  @Builder
  public WindowIconifyEvent(
      @NonNull EventTarget source,
      EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      boolean iconified) {
    super(source, target, timestamp, currentTarget);
    this.iconified = iconified;
  }
}
