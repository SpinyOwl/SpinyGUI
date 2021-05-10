package com.spinyowl.spinygui.core.system.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Will be generated when the client area of the specified window needs to be redrawn, for example
 * if the window has been exposed after having been covered by another window.
 */
@Getter
@ToString
@EqualsAndHashCode
public class SystemWindowRefreshEvent extends SystemEvent {

  @Builder
  protected SystemWindowRefreshEvent(long window) {
    super(window);
  }
}
