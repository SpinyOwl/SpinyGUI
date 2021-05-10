package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Created by ShchAlexander on 2/13/2017. */
@Getter
@ToString
@EqualsAndHashCode
public class KeyboardEvent extends Event {

  private final KeyAction action;
  private final KeyboardKey key;
  private final Set<KeyMod> mods;

  @Builder
  public KeyboardEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      EventTarget currentTarget,
      KeyAction action,
      KeyboardKey key,
      Set<KeyMod> mods) {
    super(source, target, timestamp, currentTarget);
    this.action = action;
    this.key = key;
    this.mods = mods;
  }
}
