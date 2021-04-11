package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.input.KeyMod;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** Event on Unicode character input regardless of what modifier keys are used. */
@Data
@SuperBuilder
public class SystemCharModsEvent extends SystemEvent {

  /** The Unicode code point of the character. */
  private final int codepoint;

  /** Describes which modifier keys were held down. */
  @NonNull
  @Getter(AccessLevel.NONE)
  private final List<KeyMod> mods;

  public List<KeyMod> mods() {
    return List.copyOf(mods);
  }
}
