package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2f;

@Data
@SuperBuilder
public class ChangePositionEvent extends Event {
  @NonNull private final Vector2f oldPos;
  @NonNull private final Vector2f newPos;
}
