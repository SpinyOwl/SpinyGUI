package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.MouseButton;
import lombok.Data;
import lombok.NonNull;

/**
 * Will be generated when a mouse button is pressed or released.
 */
@Data
public class SystemMouseClickEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  private final long window;

  private final MouseButton button;

  /**
   * The key action. One of:
   * <ul>
   *   <li>{@link KeyAction#PRESS PRESS}</li>
   *   <li>{@link KeyAction#RELEASE RELEASE}</li>
   *   <li>{@link KeyAction#REPEAT REPEAT}</li>
   * </ul>>.
   */
  private final KeyAction action;

  /**
   * Describes which modifier keys were held down.
   */
  @NonNull
  private final KeyMod[] mods;

}
