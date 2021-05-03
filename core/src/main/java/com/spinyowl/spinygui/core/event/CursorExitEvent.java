package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Data
@SuperBuilder(toBuilder = true)
public class CursorExitEvent extends Event {
  private final Vector2fc cursorPosition;
  private final Vector2fc intersection;
}
