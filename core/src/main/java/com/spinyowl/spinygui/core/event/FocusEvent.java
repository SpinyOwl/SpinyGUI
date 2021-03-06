package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class FocusEvent extends Event {

  @Builder
  public FocusEvent(@NonNull EventTarget source, EventTarget target, double timestamp) {
    super(source, target, timestamp);
  }

  @Getter
  @ToString
  @EqualsAndHashCode
  public static class FocusOutEvent extends FocusEvent {
    @NonNull private final Element nextFocus;

    public FocusOutEvent(
        @NonNull EventTarget source,
        @NonNull EventTarget target,
        double timestamp,
        @NonNull Element nextFocus) {
      super(source, target, timestamp);
      this.nextFocus = nextFocus;
    }
  }

  @Getter
  @ToString
  @EqualsAndHashCode
  public static class FocusInEvent extends FocusEvent {
    @NonNull private final Element prevFocus;

    public FocusInEvent(
        @NonNull EventTarget source,
        @NonNull EventTarget target,
        double timestamp,
        @NonNull Element prevFocus) {
      super(source, target, timestamp);
      this.prevFocus = prevFocus;
    }
  }
}
