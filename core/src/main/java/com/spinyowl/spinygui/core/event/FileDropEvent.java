package com.spinyowl.spinygui.core.event;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class FileDropEvent extends Event {

  @NonNull private final ImmutableList<String> paths;

  @Builder
  public FileDropEvent(
      @NonNull EventTarget source,
      @NonNull EventTarget target,
      double timestamp,
      @NonNull ImmutableList<String> paths) {
    super(source, target, timestamp);
    this.paths = paths;
  }
}
