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

  /**
   * Scroll edges - adds edges to the content+padding. Used to define space that used by scrollbars.
   */
  private final Edges scroll = new Edges();

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

  /**
   * Returns position of content in parent node coordinate system relative to outer border bounds.
   *
   * @return position of content in parent node coordinate system.
   */
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

  /**
   * Returns size of content.
   *
   * @return size of content.
   */
  public Vector2f contentSize() {
    return content().size();
  }

  /**
   * Returns position of padding box in parent node coordinate system relative to outer border.
   *
   * @return position of padding box in parent node coordinate system.
   */
  public Vector2f paddingBoxPosition() {
    return boxPosition(padding());
  }

  /**
   * Returns position of border box in parent node coordinate system relative to outer border.
   *
   * @return position of border box in parent node coordinate system.
   */
  public Vector2f borderBoxPosition() {
    return boxPosition(padding(), border());
  }

  /**
   * Returns position of margin box in parent node coordinate system relative to outer border.
   *
   * @return position of margin box in parent node coordinate system.
   */
  public Vector2f marginBoxPosition() {
    return boxPosition(padding(), border(), margin());
  }

  /**
   * Returns size of padding box.
   *
   * @return size of padding box.
   */
  public Vector2f paddingBoxSize() {
    return boxSize(padding());
  }

  /**
   * Returns size of border box.
   *
   * @return size of border box.
   */
  public Vector2f borderBoxSize() {
    return boxSize(padding(), scroll(), border());
  }

  /**
   * Returns size of margin box.
   *
   * @return size of margin box.
   */
  public Vector2f marginBoxSize() {
    return boxSize(padding(), scroll(), border(), margin());
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
    return content.expandedBy(padding, scroll, border);
  }

  public Rect marginBox() {
    return content.expandedBy(padding, scroll, border, margin);
  }
}
