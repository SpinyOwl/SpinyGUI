package com.spinyowl.spinygui.core.input;

/**
 * Used to store shortcuts.
 */
public interface ShortcutRegistry {

  /**
   * Used to set shortcut for specified name (for example 'copy').
   * <p>
   * Name would be automatically trimmed and casted to lowercase.
   *
   * @param name     shortcut name.
   * @param shortcut shortcut.
   */
  void shortcut(String name, Shortcut shortcut);

  /**
   * Used to remove specified shortcut.
   * <p>
   * Name would be automatically casted to lowercase.
   *
   * @param name shortcut name.
   */
  void removeShortcut(String name);

  /**
   * Used to get shortcut by name.
   * <p>
   * Name would be automatically casted to lowercase.
   *
   * @param name shortcut name.
   * @return shortcut for specified name or null if not found.
   */
  Shortcut shortcut(String name);

}
