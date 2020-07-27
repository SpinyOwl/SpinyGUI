package com.spinyowl.spinygui.core.system.event;

import lombok.Data;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
@Data
public class SystemWindowFocusEvent implements SystemEvent {

  /**
   * The window that was focused or defocused.
   */
  public final long window;

  /**
   * {@code true} if the window was focused, or {@code false} if it was defocused
   */
  public final boolean focused;


}
