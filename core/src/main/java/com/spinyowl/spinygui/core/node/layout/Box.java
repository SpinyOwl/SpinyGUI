package com.spinyowl.spinygui.core.node.layout;

import lombok.Data;
import org.joml.Vector2f;

@Data
public final class Box {
  /**
   * Rectangle that defines content size and position relative to frame origin (top left corner).
   */
  private final Rect content = new Rect();

  /** Padding edges - adds edges to the content */
  private final Edges padding = new Edges();

  /** Border edges - adds edges to the content+padding */
  private final Edges border = new Edges();

  /** Margin edges - adds edges to the content+padding+border */
  private final Edges margin = new Edges();

  public void contentPosition(float x, float y) {
    content().x(x);
    content().y(y);
  }

  public void contentPosition(Vector2f position) {
    content().x(position.x);
    content().y(position.y);
  }

  public Vector2f contentPosition() {
    return content().position();
  }

  public void contentSize(float width, float height) {
    content().width(width);
    content().height(height);
  }

  public void contentSize(Vector2f size) {
    content().width(size.x);
    content().height(size.y);
  }

  public Vector2f contentSize() {
    return content().size();
  }

  public Vector2f paddingBoxPosition() {
    return boxPosition(padding());
  }

  public Vector2f borderBoxPosition() {
    return boxPosition(padding(), border());
  }

  public Vector2f marginBoxPosition() {
    return boxPosition(padding(), border(), margin());
  }

  public Vector2f paddingBoxSize() {
    return boxSize(padding());
  }

  public Vector2f borderBoxSize() {
    return boxSize(padding(), border());
  }

  public Vector2f marginBoxSize() {
    return boxSize(padding(), border(), margin());
  }

  private Vector2f boxPosition(Edges... edges) {
    Vector2f position = contentPosition();
    for (Edges edge : edges) {
      position.sub(edge.left(), edge.top());
    }
    return position;
  }

  private Vector2f boxSize(Edges... edges) {
    Vector2f boxSize = contentSize();
    for (Edges edge : edges) {
      boxSize.add(edge.left() + edge.right(), edge.top() + edge.bottom());
    }
    return boxSize;
  }

  public Rect paddingBox() {
    return content.expandedBy(padding);
  }

  public Rect borderBox() {
    return content.expandedBy(padding, border);
  }

  public Rect marginBox() {
    return content.expandedBy(padding, border, margin);
  }
}
