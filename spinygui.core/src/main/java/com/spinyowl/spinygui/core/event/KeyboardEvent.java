package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class KeyboardEvent extends Event {
  @NonNull private final KeyAction action;
  @NonNull private final KeyboardKey key;
  @NonNull private final Set<KeyMod> mods;
}
