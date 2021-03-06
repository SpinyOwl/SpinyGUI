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
public class ChangeSizeEvent extends Event {

  @NonNull private final Vector2f oldSize;
  @NonNull private final Vector2f newSize;

  @Builder
  public ChangeSizeEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      @NonNull Vector2f oldSize,
      @NonNull Vector2f newSize) {
    super(source, target, timestamp);
    this.oldSize = oldSize;
    this.newSize = newSize;
  }

  public static ChangeSizeEvent of(
      EventTarget source, EventTarget target, Vector2f oldSize, Vector2f newSize) {
    return ChangeSizeEvent.builder()
        .source(source)
        .target(target)
        .oldSize(oldSize)
        .newSize(newSize)
        .build();
  }
}
