package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ChangeTextEvent extends Event {
  @NonNull private final String oldText;
  @NonNull private final String newText;
}
