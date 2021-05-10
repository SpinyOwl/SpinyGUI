package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.joml.Vector2fc;

@Getter
@ToString
@EqualsAndHashCode
public class CursorExitEvent extends Event {
  private final Vector2fc cursorPosition;
  private final Vector2fc intersection;

  @Builder
  public CursorExitEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      Vector2fc cursorPosition,
      Vector2fc intersection) {
    super(source, target, timestamp);
    this.cursorPosition = cursorPosition;
    this.intersection = intersection;
  }
}
