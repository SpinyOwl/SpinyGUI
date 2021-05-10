package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class WindowCloseEvent extends Event {

  @Builder
  public WindowCloseEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      EventTarget currentTarget) {
    super(source, target, timestamp, currentTarget);
  }
}
