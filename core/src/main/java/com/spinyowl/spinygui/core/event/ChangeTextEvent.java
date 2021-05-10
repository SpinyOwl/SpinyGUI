package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ChangeTextEvent extends Event {
  private final String oldText;
  private final String newText;
}
