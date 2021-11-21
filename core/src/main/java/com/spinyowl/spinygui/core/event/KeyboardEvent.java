package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import java.util.Set;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class KeyboardEvent extends Event {
  @NonNull private final KeyAction action;
  @NonNull private final KeyboardKey key;
  @NonNull private final Set<KeyMod> mods;
}
