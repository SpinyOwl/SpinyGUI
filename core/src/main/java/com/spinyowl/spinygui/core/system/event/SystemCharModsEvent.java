package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.system.input.KeyMod;
import lombok.Data;
import lombok.NonNull;

/**
 * Event on Unicode character input regardless of what modifier keys are used.
 */
@Data
public class SystemCharModsEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  public final long window;

  /**
   * The Unicode code point of the character.
   */
  public final int codepoint;

  /**
   * Describes which modifier keys were held down.
   */
  @NonNull
  public final KeyMod[] mods;

}
