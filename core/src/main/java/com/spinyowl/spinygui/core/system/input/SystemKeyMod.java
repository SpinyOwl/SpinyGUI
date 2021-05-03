package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.input.KeyMod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum SystemKeyMod {
  SHIFT(KeyMod.SHIFT),
  CONTROL(KeyMod.CONTROL),
  ALT(KeyMod.ALT),
  SUPER(KeyMod.SUPER),
  CAPS_LOCK(KeyMod.CAPS_LOCK),
  NUM_LOCK(KeyMod.NUM_LOCK);

  @NonNull private final KeyMod keyMod;
}
