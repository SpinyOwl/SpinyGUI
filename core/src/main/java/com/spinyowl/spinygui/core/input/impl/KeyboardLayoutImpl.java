package com.spinyowl.spinygui.core.input.impl;

import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
import lombok.NonNull;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * This class used to store key mapping to native keys. That allows user to have custom keyboard
 * layouts (that differ from standard QWERTY layout).
 */
public class KeyboardLayoutImpl implements KeyboardLayout {

  private final BidiMap<KeyCode, Integer> keys = new DualHashBidiMap<>();

  /**
   * Used to set mapping for specified keyCode.
   *
   * @param keyCode key code.
   * @param nativeCode native key code.
   */
  @Override
  public void updateMapping(@NonNull KeyCode keyCode, @NonNull Integer nativeCode) {
    keys.put(keyCode, nativeCode);
  }

  /**
    * Used to get mapped {@link KeyCode} by native key code.
   *
   * @param nativeCode native key code.
   * @return mapped {@link KeyCode} or null if not found.
   */
  @Override
  public KeyCode keyCode(@NonNull Integer nativeCode) {
    return keys.getKey(nativeCode);
  }

  /**
   * Used to get mapped native key code by {@link KeyCode}.
   *
   * @param keyCode key code.
   * @return mapped native key code or null if not found.
   */
  @Override
  public Integer nativeCode(@NonNull KeyCode keyCode) {
    return keys.get(keyCode);
  }
}
