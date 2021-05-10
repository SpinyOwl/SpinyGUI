package com.spinyowl.spinygui.core.system.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Will be generated when the user attempts to close the specified window, for example by clicking
 * the close widget in the title bar.
 */
@Getter
@ToString
@EqualsAndHashCode
public class SystemWindowCloseEvent extends SystemEvent {

  @Builder
  protected SystemWindowCloseEvent(long window) {
    super(window);
  }
}
