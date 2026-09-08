package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.node.layout.Rect;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector4f;

/** Continuous outer/inner solid-border contours; side order is top, right, bottom, left. */
final class BorderContour {
  private BorderContour() {}

  /** CSS scales all corner radii together when adjacent radii exceed an edge. */
  static Vector4f clampRadii(Vector4f radius, float width, float height) {
    Vector4f r = new Vector4f(Math.max(0, radius.x), Math.max(0, radius.y),
        Math.max(0, radius.z), Math.max(0, radius.w));
    float scale = 1;
    scale = ratio(scale, width, r.x + r.y);
    scale = ratio(scale, width, r.z + r.w);
    scale = ratio(scale, height, r.x + r.w);
    scale = ratio(scale, height, r.y + r.z);
    return r.mul(scale);
  }

  private static float ratio(float scale, float available, float sum) {
    return sum > 0 ? Math.min(scale, available / sum) : scale;
  }

  /** Adjacent sides share the same outer and inner corner split points, without overlapping area. */
  static List<Vector2f> side(Rect rect, float[] widths, Vector4f radius, int side) {
    List<Vector2f> outer = new ArrayList<>();
    List<Vector2f> inner = new ArrayList<>();
    int next = (side + 1) % 4;
    appendCorner(outer, inner, rect, widths, radius, side, 225 + side * 90, 270 + side * 90);
    appendCorner(outer, inner, rect, widths, radius, next, 270 + side * 90, 315 + side * 90);
    for (int i = inner.size() - 1; i >= 0; i--) outer.add(inner.get(i));
    return List.copyOf(outer);
  }

  private static void appendCorner(List<Vector2f> outer, List<Vector2f> inner,
      Rect rect, float[] widths, Vector4f radii, int corner, float from, float to) {
    float radius = radii.get(corner);
    boolean left = corner == 0 || corner == 3;
    boolean top = corner < 2;
    float horizontal = widths[left ? 3 : 1];
    float vertical = widths[top ? 0 : 2];
    float rx = Math.max(0, radius - horizontal);
    float ry = Math.max(0, radius - vertical);
    float outerX = rect.x() + (left ? radius : rect.width() - radius);
    float outerY = rect.y() + (top ? radius : rect.height() - radius);
    float innerX = rect.x() + (left ? horizontal + rx : rect.width() - horizontal - rx);
    float innerY = rect.y() + (top ? vertical + ry : rect.height() - vertical - ry);
    // At most 0.1 px chord error for ordinary radii, with shared endpoints on either side.
    int segments = Math.max(1, (int) Math.ceil(Math.sqrt(radius) * 2));
    for (int i = 0; i <= segments; i++) {
      double angle = Math.toRadians(from + (to - from) * i / segments);
      float cos = (float) Math.cos(angle);
      float sin = (float) Math.sin(angle);
      outer.add(new Vector2f(outerX + radius * cos, outerY + radius * sin));
      inner.add(new Vector2f(innerX + rx * cos, innerY + ry * sin));
    }
  }
}
