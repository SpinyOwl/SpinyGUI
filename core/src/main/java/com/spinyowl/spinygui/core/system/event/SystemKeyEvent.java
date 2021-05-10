package com.spinyowl.spinygui.core.system.event;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Will be generated when a key is pressed, repeated or released. */
@Getter
@ToString
@EqualsAndHashCode
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
   *   <li>{@link SystemKeyAction#PRESS PRESS}
   *   <li>{@link SystemKeyAction#RELEASE RELEASE}
   *   <li>{@link SystemKeyAction#REPEAT REPEAT}
   * </ul>
   */
  @NonNull private final SystemKeyAction action;

  /** Describes which modifier keys were held down. */
  @NonNull private final ImmutableSet<SystemKeyMod> mods;

  @Builder
  protected SystemKeyEvent(
      long window,
      int keyCode,
      int scancode,
      @NonNull SystemKeyAction action,
      @NonNull ImmutableSet<SystemKeyMod> mods) {
    super(window);
    this.keyCode = keyCode;
    this.scancode = scancode;
    this.action = action;
    this.mods = mods;
  }

  public ImmutableSet<KeyMod> mappedMods() {
    return mods.stream().map(SystemKeyMod::keyMod).collect(toImmutableSet());
  }
}
