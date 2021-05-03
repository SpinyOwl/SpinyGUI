package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class FocusEvent extends Event {

  @Data
  @SuperBuilder(toBuilder = true)
  public static class FocusOutEvent extends FocusEvent {
    @NonNull private final Element nextFocus;
  }

  @Data
  @SuperBuilder(toBuilder = true)
  public static class FocusInEvent extends FocusEvent {
    @NonNull private final Element prevFocus;
  }
}
