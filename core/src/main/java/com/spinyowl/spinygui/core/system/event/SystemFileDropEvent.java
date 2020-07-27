package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.NonNull;

/**
 * File drop event. Should be generated when one or more files are dropped on the window.
 */
@Data
public class SystemFileDropEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  public final long window;

  /**
   * UTF-8 encoded path names of the dropped files.
   */
  @NonNull
  public final String[] strings;

}
