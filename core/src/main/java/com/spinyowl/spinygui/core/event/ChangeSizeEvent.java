package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2f;

@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class ChangeSizeEvent extends Event {

  @NonNull
  private final Vector2f oldSize;
  @NonNull
  private final Vector2f newSize;

  public static ChangeSizeEvent of(EventTarget target, Vector2f oldSize, Vector2f newSize) {
    return ChangeSizeEvent.builder().target(target).oldSize(oldSize).newSize(newSize).build();
  }

}