package com.spinyowl.spinygui.core.system.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Unicode character is input. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemCharEvent extends SystemEvent {

  /** The Unicode code point of the character. */
  private final int codepoint;

  @Builder
  protected SystemCharEvent(long window, int codepoint) {
    super(window);
    this.codepoint = codepoint;
  }
}
