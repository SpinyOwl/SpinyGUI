package com.spinyowl.spinygui.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class CharEvent extends Event {

  @NonNull private final String input;
}
