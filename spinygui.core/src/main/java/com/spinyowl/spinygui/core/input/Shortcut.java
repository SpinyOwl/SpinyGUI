package com.spinyowl.spinygui.core.input;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Shortcut {

  @NonNull private KeyCode key;
  @NonNull private Set<KeyMod> mods;

  public boolean check(@NonNull KeyCode key, @NonNull KeyMod... mods) {
    return check(key, Set.of(mods));
  }

  public boolean check(@NonNull KeyCode key, @NonNull Set<KeyMod> mods) {
    return this.key.equals(key) && mods.size() == this.mods.size() && this.mods.containsAll(mods);
  }
}
