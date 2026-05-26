package com.spinyowl.spinygui.core.input;

/**
 * Used to store key mapping to native keys. That allows user to have custom keyboard layouts (that
 * differ from standard QWERTY layout).
 */
public interface KeyboardLayout {

  /**
   * Used to set mapping for specified keyCode.
   *
   * @param keyCode key code.
   * @param nativeCode native key code.
   */
  void updateMapping(KeyCode keyCode, Integer nativeCode);

  /**
   * Used to get mapped {@link KeyCode} by native key code.
   *
   * @param nativeCode native key code.
   * @return mapped {@link KeyCode} or null if not found.
   */
  KeyCode keyCode(Integer nativeCode);

  /**
   * Used to get mapped native key code by {@link KeyCode}.
   *
   * @param keyCode key code.
   * @return mapped native key code or null if not found.
   */
  Integer nativeCode(KeyCode keyCode);
}
