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
public class CursorEnterEvent extends Event {
  private final boolean entered;
  private final Vector2fc cursorPosition;
  private final Vector2fc intersection;
}
