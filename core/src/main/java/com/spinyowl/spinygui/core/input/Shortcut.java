package com.spinyowl.spinygui.core.input;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Shortcut {

  private static final Map<String, Shortcut> shortcuts = new ConcurrentHashMap<>();

  @NonNull
  private KeyCode key;

  @NonNull
  private Set<KeyMod> mods;

  public Shortcut(@NonNull KeyCode key, @NonNull KeyMod... mods) {
    this.key = key;
    this.mods = Set.of(mods);
  }

  public boolean check(@NonNull KeyCode key, @NonNull KeyMod... mods) {
    return check(key, Set.of(mods));
  }

  public boolean check(@NonNull KeyCode key, @NonNull Set<KeyMod> mods) {
    return this.key.equals(key) && mods.size() == this.mods.size() && this.mods.containsAll(mods);
  }

  /**
   * Used to set shortcut for specified name (for example 'copy').
   * <p>
   * Name would be automatically trimmed and casted to lowercase.
   *
   * @param name     shortcut name.
   * @param shortcut shortcut.
   */
  public void shortcut(@NonNull String name, @NonNull Shortcut shortcut) {
    shortcuts.put(name.toLowerCase().trim(), shortcut);
  }

  /**
   * Used to remove specified shortcut.
   * <p>
   * Name would be automatically casted to lowercase.
   *
   * @param name shortcut name.
   */
  public void removeShortcut(@NonNull String name) {
    shortcuts.remove(name.toLowerCase().trim());
  }

  /**
   * Used to get shortcut by name.
   * <p>
   * Name would be automatically casted to lowercase.
   *
   * @param name shortcut name.
   * @return shortcut for specified name or null if not found.
   */
  public Shortcut shortcut(@NonNull String name) {
    return shortcuts.get(name.toLowerCase().trim());
  }

}
