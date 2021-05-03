package com.spinyowl.spinygui.core.system.event;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** Event on Unicode character input regardless of what modifier keys are used. */
@Data
@SuperBuilder(toBuilder = true)
public class SystemCharModsEvent extends SystemEvent {

  /** The Unicode code point of the character. */
  private final int codepoint;

  /** Describes which modifier keys were held down. */
  @NonNull private final ImmutableSet<SystemKeyMod> mods;

  public ImmutableSet<KeyMod> mappedMods() {
    return mods.stream().map(SystemKeyMod::keyMod).collect(toImmutableSet());
  }
}
