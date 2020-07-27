package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Getter;
import org.joml.Vector2f;

@Getter
public class ChangePositionEvent<T extends Element> extends ElementEvent<T> {

  private final Vector2f oldPos;
  private final Vector2f newPos;

  public ChangePositionEvent(T target, Vector2f oldPos, Vector2f newPos) {
    super(target);
    this.oldPos = oldPos;
    this.newPos = newPos;
  }
}
