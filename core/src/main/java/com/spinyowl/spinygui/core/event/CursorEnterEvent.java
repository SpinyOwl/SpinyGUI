package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Data
@SuperBuilder
public class CursorEnterEvent extends Event {
  private final boolean entered;
  private final Vector2fc cursorPosition;
  private final Vector2fc intersection;
}
