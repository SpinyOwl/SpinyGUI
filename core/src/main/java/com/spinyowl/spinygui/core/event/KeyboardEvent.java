package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import java.util.Set;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Created by ShchAlexander on 2/13/2017. */
@Data
@SuperBuilder(toBuilder = true)
public class KeyboardEvent extends Event {

  private final KeyAction action;
  private final KeyboardKey key;
  private final Set<KeyMod> mods;
}
