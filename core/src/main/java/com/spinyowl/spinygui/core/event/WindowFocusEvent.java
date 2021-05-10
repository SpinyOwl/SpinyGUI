package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class WindowFocusEvent extends NodeEvent {

  @Builder
  public WindowFocusEvent(
      @NonNull EventTarget source,
      EventTarget target,
      double timestamp,
      EventTarget currentTarget) {
    super(source, target, timestamp, currentTarget);
  }
}
