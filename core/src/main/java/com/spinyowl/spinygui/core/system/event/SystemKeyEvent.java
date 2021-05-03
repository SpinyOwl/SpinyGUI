package com.spinyowl.spinygui.core.system.event;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** Will be generated when a key is pressed, repeated or released. */
@Data
@SuperBuilder(toBuilder = true)
public class SystemKeyEvent extends SystemEvent {

  /** The keyboard key that was pressed or released. */
  private final int keyCode;

  /**
   * The system-specific scancode of the key.
   *
   * <p>The scancode is unique for every key, regardless of whether it has a key token. Scancodes
   * are platform-specific but consistent over time, so keys will have different scancodes depending
   * on the platform but they are safe to save to disk.
   */
  private final int scancode;

  /**
   * The key action. One of:
   *
   * <ul>
   *   <li>{@link KeyAction#PRESS PRESS}
   *   <li>{@link KeyAction#RELEASE RELEASE}
   *   <li>{@link KeyAction#REPEAT REPEAT}
   * </ul>
   */
  @NonNull private final KeyAction action;

  /** Describes which modifier keys were held down. */
  @NonNull private final ImmutableSet<KeyMod> mods;
}
