package com.spinyowl.spinygui.core.node.intersection;

import com.spinyowl.spinygui.core.node.Node;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joml.Vector2fc;

@ToString
@EqualsAndHashCode
public class RectangleIntersection implements Intersection {

  @Override
  public boolean intersects(Node node, Vector2fc point) {
    Vector2fc pos = node.dimensions().borderBoxPosition();
    Vector2fc size = node.dimensions().borderBoxSize();
    return point.x() >= pos.x()
        && point.x() < pos.x() + size.x()
        && point.y() >= pos.y()
        && point.y() < pos.y() + size.y();
  }
}
