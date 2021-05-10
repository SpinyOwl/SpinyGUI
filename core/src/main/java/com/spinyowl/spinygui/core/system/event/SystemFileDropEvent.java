package com.spinyowl.spinygui.core.system.event;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class SystemFileDropEvent extends SystemEvent {

  /** The UTF-8 encoded file and/or directory path names. */
  private final ImmutableList<String> paths;

  @Builder
  protected SystemFileDropEvent(long window, ImmutableList<String> paths) {
    super(window);
    this.paths = paths;
  }
}
