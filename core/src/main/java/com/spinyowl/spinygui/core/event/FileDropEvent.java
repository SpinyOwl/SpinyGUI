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
public class FileDropEvent extends Event {
  @NonNull private final String[] paths;
}
