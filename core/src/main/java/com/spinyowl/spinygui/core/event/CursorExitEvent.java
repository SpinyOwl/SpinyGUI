package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Data
@SuperBuilder
public class CursorExitEvent extends Event {
  @NonNull private final Vector2fc cursorPosition;
  @NonNull private final Vector2fc intersection;
}
