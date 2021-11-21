package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Data
@SuperBuilder
public class MouseDragEvent extends Event {
  @NonNull private final Vector2fc delta;
}
