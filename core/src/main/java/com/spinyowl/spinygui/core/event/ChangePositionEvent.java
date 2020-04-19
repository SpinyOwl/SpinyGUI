package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import org.joml.Vector2f;

public class ChangePositionEvent<T extends Element> extends NodeEvent<T> {

  private final Vector2f oldPos;
  private final Vector2f newPos;

  public ChangePositionEvent(T target, Vector2f oldPos, Vector2f newPos) {
    super(target);
    this.oldPos = oldPos;
    this.newPos = newPos;
  }

  public Vector2f getOldPos() {
    return oldPos;
  }

  public Vector2f getNewPos() {
    return newPos;
  }
}
