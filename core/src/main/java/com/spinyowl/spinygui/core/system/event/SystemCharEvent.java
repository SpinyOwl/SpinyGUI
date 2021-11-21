package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/** Unicode character is input. */
@Data
@SuperBuilder
public class SystemCharEvent extends SystemEvent {
  /** The Unicode code point of the character. */
  private final int codepoint;
}
