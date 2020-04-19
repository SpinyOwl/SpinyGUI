package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import org.joml.Vector2f;

public class ChangeSizeEvent<T extends Element> extends NodeEvent<T> {

  private final Vector2f oldSize;
  private final Vector2f newSize;

  public ChangeSizeEvent(T target, Vector2f oldSize, Vector2f newSize) {
    super(target);
    this.oldSize = oldSize;
    this.newSize = newSize;
  }

  public Vector2f getOldSize() {
    return oldSize;
  }

  public Vector2f getNewSize() {
    return newSize;
  }
}
