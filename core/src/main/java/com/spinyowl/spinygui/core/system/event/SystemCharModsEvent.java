package com.spinyowl.spinygui.core.system.event;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Event on Unicode character input regardless of what modifier keys are used. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemCharModsEvent extends SystemEvent {

  /** The Unicode code point of the character. */
  private final int codepoint;

  /** Describes which modifier keys were held down. */
  @NonNull private final ImmutableSet<SystemKeyMod> mods;

  @Builder
  protected SystemCharModsEvent(
      Frame frame, int codepoint, @NonNull ImmutableSet<SystemKeyMod> mods) {
    super(frame);
    this.codepoint = codepoint;
    this.mods = mods;
  }

  public ImmutableSet<KeyMod> mappedMods() {
    return mods.stream().map(SystemKeyMod::keyMod).collect(toImmutableSet());
  }
}
