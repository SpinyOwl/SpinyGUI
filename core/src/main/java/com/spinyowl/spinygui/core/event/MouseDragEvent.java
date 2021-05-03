package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Data
@SuperBuilder(toBuilder = true)
public class MouseDragEvent extends Event {
  private final Vector2fc delta;
}
