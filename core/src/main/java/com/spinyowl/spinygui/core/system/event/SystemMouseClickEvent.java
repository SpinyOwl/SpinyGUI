package com.spinyowl.spinygui.core.system.event;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** Will be generated when a mouse button is pressed or released. */
@Data
@SuperBuilder(toBuilder = true)
public class SystemMouseClickEvent extends SystemEvent {

  private final SystemMouseButton button;

  /**
   * The key action. One of:
   *
   * <ul>
   *   <li>{@link SystemKeyAction#PRESS PRESS}
   *   <li>{@link SystemKeyAction#RELEASE RELEASE}
   *   <li>{@link SystemKeyAction#REPEAT REPEAT}
   * </ul>
   */
  private final SystemKeyAction action;

  /** Describes which modifier keys were held down. */
  @NonNull private final ImmutableSet<SystemKeyMod> mods;

  public ImmutableSet<KeyMod> mappedMods() {
    return mods.stream().map(SystemKeyMod::keyMod).collect(toImmutableSet());
  }
}
