package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class ChangeTextEvent extends Event {
  @NonNull private final String oldText;
  @NonNull private final String newText;
}
