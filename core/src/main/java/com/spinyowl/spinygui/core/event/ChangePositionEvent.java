package com.spinyowl.spinygui.core.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.joml.Vector2f;

@Getter
@ToString
@EqualsAndHashCode
public class ChangePositionEvent extends NodeEvent {

  @NonNull private final Vector2f oldPos;
  @NonNull private final Vector2f newPos;

  @Builder
  public ChangePositionEvent(
      @NonNull EventTarget source,
      EventTarget target,
      double timestamp,
      @NonNull Vector2f oldPos,
      @NonNull Vector2f newPos) {
    super(source, target, timestamp);
    this.oldPos = oldPos;
    this.newPos = newPos;
  }

  public static ChangePositionEvent of(EventTarget target, Vector2f oldPos, Vector2f newPos) {
    return ChangePositionEvent.builder().target(target).oldPos(oldPos).newPos(newPos).build();
  }
}
