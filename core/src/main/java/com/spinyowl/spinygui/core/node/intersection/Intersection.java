package com.spinyowl.spinygui.core.node.intersection;

import com.spinyowl.spinygui.core.node.Node;
import org.joml.Vector2fc;

/**
 * Intersection class specifies intersection rules for node and point. Used to detect
 * intersection of point on virtual window surface and node.
 */
public interface Intersection {

  /**
   * Intersection rule.
   *
   * @param node node to check intersection.
   * @param point vector with coordinates of point to check intersection.
   * @return true if node intersected by point.
   */
  boolean intersects(Node node, Vector2fc point);
}
