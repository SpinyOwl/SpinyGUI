package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.KeyMod;
import lombok.Data;
import lombok.NonNull;

/**
 * Will be generated when a key is pressed, repeated or released.
 */
@Data
public class SystemKeyEvent implements SystemEvent {

  public final long window;

  /**
   * The keyboard key that was pressed or released.
   */
  public final KeyCode key;

  /**
   * The system-specific scancode of the key.
   * <p>
   * The scancode is unique for every key, regardless of whether it has a key token. Scancodes are
   * platform-specific but consistent over time, so keys will have different scancodes depending on
   * the platform but they are safe to save to disk.
   */
  public final int scancode;

  /**
   * The key action. One of:
   * <ul>
   *   <li>{@link KeyAction#PRESS PRESS}</li>
   *   <li>{@link KeyAction#RELEASE RELEASE}</li>
   *   <li>{@link KeyAction#REPEAT REPEAT}</li>
   * </ul>>.
   */
  public final KeyAction action;

  /**
   * Describes which modifier keys were held down.
   */
  @NonNull
  public final KeyMod[] mods;

}
