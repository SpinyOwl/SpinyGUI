package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class MouseDragEvent extends Event {
  @NonNull private final Vector2fc delta;
}
