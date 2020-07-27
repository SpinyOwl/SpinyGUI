package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Unicode character is input.
 */
@Data
public class SystemCharEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  public final long window;

  /**
   * The Unicode code point of the character.
   */
  public final int codepoint;

}
