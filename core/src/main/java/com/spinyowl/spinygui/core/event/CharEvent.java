package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class CharEvent extends NodeEvent {

  @NonNull private final String input;

  @Builder
  public CharEvent(
      @NonNull EventTarget source, EventTarget target, double timestamp, @NonNull String input) {
    super(source, target, timestamp);
    this.input = input;
  }
}
