package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class MouseEvent extends Event {

  // modificators
  private boolean altKey;
  private boolean ctrlKey;
  private boolean shiftKey;

  // mouse button
  private int button;

  // position
  private float x;
  private float y;

  // offsets
  private float offsetX;
  private float offsetY;

  // screen position
  private float screenX;
  private float screenY;

  // page position
  private float pageX;
  private float pageY;

  // mouse movement delta
  private float movementX;
  private float movementY;

}
