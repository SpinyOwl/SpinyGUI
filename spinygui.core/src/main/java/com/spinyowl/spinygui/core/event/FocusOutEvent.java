package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class FocusOutEvent extends Event {

  @NonNull
  private final Element nextFocus;
}
