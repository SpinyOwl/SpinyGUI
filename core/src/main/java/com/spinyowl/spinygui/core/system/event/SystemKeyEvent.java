package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.system.input.KeyAction;
import com.spinyowl.spinygui.core.system.input.KeyMod;
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
  public final int key;

  /**
   * The system-specific scancode of the key.
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
