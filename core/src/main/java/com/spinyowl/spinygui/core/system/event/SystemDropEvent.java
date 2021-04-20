package com.spinyowl.spinygui.core.system.event;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class SystemDropEvent extends SystemEvent {

  private final ImmutableList<String> strings;

}
