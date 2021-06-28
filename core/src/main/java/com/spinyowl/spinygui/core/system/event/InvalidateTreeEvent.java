package com.spinyowl.spinygui.core.system.event;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Event, that used only for invalidating tree, so it should be rendered again. */
@Getter
@ToString
@EqualsAndHashCode
public class InvalidateTreeEvent extends SystemEvent {

  public InvalidateTreeEvent(@NonNull Frame frame) {
    super(frame);
  }
}
