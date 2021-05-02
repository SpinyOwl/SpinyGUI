package com.spinyowl.spinygui.core.system.event;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.MouseButton;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** Will be generated when a mouse button is pressed or released. */
@Data
@SuperBuilder
public class SystemMouseClickEvent extends SystemEvent {

  private final MouseButton button;

  /**
   * The key action. One of:
   *
   * <ul>
   *   <li>{@link KeyAction#PRESS PRESS}
   *   <li>{@link KeyAction#RELEASE RELEASE}
   *   <li>{@link KeyAction#REPEAT REPEAT}
   * </ul>
   */
  private final KeyAction action;

  /** Describes which modifier keys were held down. */
  @NonNull private final ImmutableSet<KeyMod> mods;
}
