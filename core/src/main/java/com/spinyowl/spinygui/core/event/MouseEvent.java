package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Getter;

@Getter
public class MouseEvent<T extends Element> extends ElementEvent<T> {

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

  public MouseEvent(T target) {
    super(target);
  }

  public MouseEvent(T target, double timeStamp) {
    super(target, timeStamp);
  }

  public MouseEvent(EventTarget source, T target, double timeStamp) {
    super(source, target, timeStamp);
  }

}
