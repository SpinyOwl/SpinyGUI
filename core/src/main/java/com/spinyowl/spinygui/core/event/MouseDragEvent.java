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
public class MouseDragEvent extends Event {
  private final Vector2fc delta;

  @Builder
  public MouseDragEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      Vector2fc delta) {
    super(source, target, timestamp, currentTarget);
    this.delta = delta;
  }
}
