package com.spinyowl.spinygui.core.system.event;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** File drop event. Should be generated when one or more files are dropped on the window. */
@Data
@SuperBuilder
public class SystemFileDropEvent extends SystemEvent {

  /** UTF-8 encoded path names of the dropped files. */
  @NonNull
  @Getter(AccessLevel.NONE)
  private final List<String> strings;

  public List<String> strings() {
    return List.copyOf(strings);
  }
}
