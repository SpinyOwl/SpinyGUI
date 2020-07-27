package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Getter;
import org.joml.Vector2f;

@Getter
public class ChangeSizeEvent<T extends Element> extends ElementEvent<T> {

  private final Vector2f oldSize;
  private final Vector2f newSize;

  public ChangeSizeEvent(T target, Vector2f oldSize, Vector2f newSize) {
    super(target);
    this.oldSize = oldSize;
    this.newSize = newSize;
  }

}