package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.KeyMod;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** Will be generated when a key is pressed, repeated or released. */
@Data
@SuperBuilder
public class SystemKeyEvent extends SystemEvent {

  /** The keyboard key that was pressed or released. */
  private final KeyCode key;

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
  private final KeyAction action;

  /** Describes which modifier keys were held down. */
  @NonNull
  @Getter(AccessLevel.NONE)
  private final List<KeyMod> mods;

  public List<KeyMod> mods() {
    return List.copyOf(mods);
  }
}
