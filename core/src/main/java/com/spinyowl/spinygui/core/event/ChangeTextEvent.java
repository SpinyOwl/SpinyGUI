package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ChangeTextEvent extends Event {
  private final String oldText;
  private final String newText;

  @Builder
  public ChangeTextEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      String oldText,
      String newText) {
    super(source, target, timestamp);
    this.oldText = oldText;
    this.newText = newText;
  }
}
