package com.spinyowl.spinygui.core.node.layout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector2f;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Rect {
  private float x;
  private float y;
  private float width;
  private float height;

  public Rect expandedBy(Edges... edges) {
    var rect = new Rect(x, y, width, height);
    for (var edge : edges) {
      rect.x(rect.x() - edge.left());
      rect.y(rect.y() - edge.top());
      rect.width(rect.width() + edge.left() + edge.right());
      rect.height(rect.height() + edge.top() + edge.bottom());
    }
    return rect;
  }

  public void position(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public void size(float width, float height) {
    this.width = width;
    this.height = height;
  }

  public Vector2f position() {
    return new Vector2f(x, y);
  }

  public Vector2f size() {
    return new Vector2f(width, height);
  }
}
