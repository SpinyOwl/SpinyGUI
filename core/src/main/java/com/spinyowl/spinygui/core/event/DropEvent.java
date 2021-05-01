package com.spinyowl.spinygui.core.event;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class DropEvent extends Event {

  @NonNull private final ImmutableList<String> paths;
}
