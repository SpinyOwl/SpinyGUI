package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2f;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class ChangeSizeEvent extends Event {
  @NonNull private final Vector2f oldSize;
  @NonNull private final Vector2f newSize;
}
