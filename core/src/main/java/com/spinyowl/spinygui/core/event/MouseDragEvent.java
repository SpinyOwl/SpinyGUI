package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class MouseDragEvent extends Event {
  private final Vector2fc delta;
}
