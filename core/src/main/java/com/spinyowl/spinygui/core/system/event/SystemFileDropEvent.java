package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class SystemFileDropEvent extends SystemEvent {

  /** The UTF-8 encoded file and/or directory path names. */
  @NonNull private final String[] paths;
}
