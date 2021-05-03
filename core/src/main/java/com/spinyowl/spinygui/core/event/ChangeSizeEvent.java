package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2f;

@Data
@SuperBuilder(toBuilder = true)
public class ChangeSizeEvent extends Event {

  @NonNull private final Vector2f oldSize;
  @NonNull private final Vector2f newSize;

  public static ChangeSizeEvent of(EventTarget target, Vector2f oldSize, Vector2f newSize) {
    return ChangeSizeEvent.builder().target(target).oldSize(oldSize).newSize(newSize).build();
  }
}
