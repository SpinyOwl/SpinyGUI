package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2f;

@Data
@SuperBuilder
public class ChangeSizeEvent extends Event {
  @NonNull private final Vector2f oldSize;
  @NonNull private final Vector2f newSize;
}
