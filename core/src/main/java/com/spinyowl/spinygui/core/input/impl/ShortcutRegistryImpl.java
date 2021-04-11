package com.spinyowl.spinygui.core.input.impl;

import com.spinyowl.spinygui.core.input.Shortcut;
import com.spinyowl.spinygui.core.input.ShortcutRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/** Used to store shortcuts. */
public class ShortcutRegistryImpl implements ShortcutRegistry {

  private final Map<String, Shortcut> shortcuts = new ConcurrentHashMap<>();

  /**
   * Used to set shortcut for specified name (for example 'copy').
   *
   * <p>Name would be automatically trimmed and casted to lowercase.
   *
   * @param name shortcut name.
   * @param shortcut shortcut.
   */
  @Override
  public void shortcut(@NonNull String name, @NonNull Shortcut shortcut) {
    shortcuts.put(name.toLowerCase().trim(), shortcut);
  }

  /**
   * Used to remove specified shortcut.
   *
   * <p>Name would be automatically casted to lowercase.
   *
   * @param name shortcut name.
   */
  @Override
  public void removeShortcut(@NonNull String name) {
    shortcuts.remove(name.toLowerCase().trim());
  }

  /**
   * Used to get shortcut by name.
   *
   * <p>Name would be automatically casted to lowercase.
   *
   * @param name shortcut name.
   * @return shortcut for specified name or null if not found.
   */
  @Override
  public Shortcut shortcut(@NonNull String name) {
    return shortcuts.get(name.toLowerCase().trim());
  }
}
