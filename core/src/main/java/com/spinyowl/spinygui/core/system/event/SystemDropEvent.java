package com.spinyowl.spinygui.core.system.event;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class SystemDropEvent extends SystemEvent {

  /** The UTF-8 encoded file and/or directory path names. */
  private final ImmutableList<String> paths;
}
