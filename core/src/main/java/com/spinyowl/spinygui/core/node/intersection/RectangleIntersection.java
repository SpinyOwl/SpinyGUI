package com.spinyowl.spinygui.core.node.intersection;

import com.spinyowl.spinygui.core.node.Node;
import lombok.Data;
import org.joml.Vector2fc;

@Data
public class RectangleIntersection implements Intersection {

  @Override
  public boolean intersects(Node node, Vector2fc point) {
    Vector2fc pos = node.position();
    Vector2fc size = node.size();
    return point.x() >= pos.x()
        && point.x() <= pos.x() + size.x()
        && point.y() >= pos.y()
        && point.y() <= pos.y() + size.y();
  }
}
