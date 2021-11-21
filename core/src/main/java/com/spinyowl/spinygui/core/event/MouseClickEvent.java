package com.spinyowl.spinygui.core.event;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.MouseButton;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.joml.Vector2fc;

@Data
@SuperBuilder
public class MouseClickEvent extends Event {
  @NonNull private final KeyAction action;
  @NonNull private final MouseButton mouseButton;
  @NonNull private final Vector2fc position;
  @NonNull private final Vector2fc absolutePosition;
  @NonNull private final ImmutableSet<KeyMod> mods;
}
