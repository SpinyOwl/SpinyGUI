package com.spinyowl.spinygui.core.system.event;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

/**
 * File drop event. Should be generated when one or more files are dropped on the window.
 */
@Data
public class SystemFileDropEvent implements SystemEvent {

  /**
   * The window that received the event.
   */
  private final long window;

  /**
   * UTF-8 encoded path names of the dropped files.
   */
  @NonNull
  @Getter(AccessLevel.NONE)
  private final List<String> strings;

  public List<String> strings() {
    return List.copyOf(strings);
  }
}
