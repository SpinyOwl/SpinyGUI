package com.spinyowl.spinygui.core.event;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.MouseButton;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.joml.Vector2fc;

@Getter
@ToString
@EqualsAndHashCode
public class MouseClickEvent extends NodeEvent {
  @NonNull private final KeyAction action;
  @NonNull private final MouseButton mouseButton;
  @NonNull private final Vector2fc position;
  @NonNull private final Vector2fc absolutePosition;
  @NonNull private final ImmutableSet<KeyMod> mods;

  @Builder
  public MouseClickEvent(
      @NonNull EventTarget source,
      EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      @NonNull KeyAction action,
      @NonNull MouseButton mouseButton,
      @NonNull Vector2fc position,
      @NonNull Vector2fc absolutePosition,
      @NonNull ImmutableSet<KeyMod> mods) {
    super(source, target, timestamp, currentTarget);
    this.action = action;
    this.mouseButton = mouseButton;
    this.position = position;
    this.absolutePosition = absolutePosition;
    this.mods = mods;
  }
}
