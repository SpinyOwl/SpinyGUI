package com.spinyowl.spinygui.core.node;

import static java.util.stream.Collectors.toList;
import com.spinyowl.spinygui.core.node.intersection.Intersection;
import com.spinyowl.spinygui.core.node.intersection.Intersections;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector2f;

/**
 * Base structure of any node.
 *
 * <p>Have three base subclasses that should be used to create any kind of element:
 *
 * <ul>
 *   <li>{@link Element}<br>
 *       - base for components that could contain other components.
 *   <li>{@link EmptyElement}<br>
 *       - base for components that could not contain other components.
 *   <li>{@link Text}<br>
 *       - representation of text node.
 * </ul>
 */
@Getter
@Setter
@ToString(exclude = {"parent", "nextSibling", "previousSibling"})
@RequiredArgsConstructor
public abstract class Node {

  @NonNull private final String nodeName;

  /** Parent node. */
  @Setter(AccessLevel.NONE)
  private Element parent;

  /** Node position. Assigned to node by layout manager. */
  @NonNull private Vector2f position = new Vector2f();

  /** Node size. Assigned to node by layout manager. */
  @NonNull private Vector2f size = new Vector2f();

  /** Determines whether this node hovered or not (cursor is over this node). */
  private boolean hovered;

  /** Determines whether this node focused or not. */
  private boolean focused;

  /** Determines whether this node pressed or not (Mouse button is down and on this node). */
  private boolean pressed;

  /**
   * Node intersection. During initialization used {@link Intersections#getDefaultIntersection()}.
   * Used to allow detect intersection of point on virtual window surface and node.
   */
  @NonNull private Intersection intersection = Intersections.getDefaultIntersection();

  @Setter(AccessLevel.MODULE)
  private Node nextSibling;

  @Setter(AccessLevel.MODULE)
  private Node previousSibling;

  /**
   * Used to set parent node.
   *
   * @param parent new parent node.
   */
  public void parent(Element parent) {
    if (parent == this) {
      return;
    }

    if (this.parent != null) {
      this.parent.removeChild(this);
    }
    this.parent = parent;
    if (parent != null) {
      parent.addChild(this);
    }
  }

  /**
   * Used to add a new child node, to an element, as the last child node.
   *
   * @param node node to add.
   */
  public abstract void addChild(@NonNull Node node);

  /**
   * Used to add several child node, to an element, as the last child node.
   *
   * @param nodes list of nodes to add.
   */
  public final void addChildren(@NonNull Node... nodes) {
    Arrays.stream(nodes).forEach(this::addChild);
  }

  /**
   * Used to remove child node.
   *
   * @param node node to remove.
   */
  public abstract void removeChild(@NonNull Node node);

  /**
   * Returns a collection of a node's child nodes, as {@code List<Node>} object.
   *
   * <p>The nodes in the collection are sorted as they was added to the element.
   *
   * <p>Tip: To return a collection of a node's element nodes (excluding text and comment nodes),
   * use the {@link #children()} method.
   *
   * @return list of child nodes.
   */
  public abstract List<Node> childNodes();

  /**
   * Returns true if an element has any child nodes, otherwise false.
   *
   * @return true if an element has any child nodes, otherwise false.
   */
  public abstract boolean hasChildNodes();

  /**
   * Returns a collection of an element's child elements, as an {@code List<Element>} object.
   *
   * <p>The elements in the collection are sorted as they was added tp the element.
   *
   * @return list of child elements.
   */
  public List<Element> children() {
    return childNodes().stream()
        .filter(Element.class::isInstance)
        .map(Element.class::cast)
        .collect(toList());
  }

  /**
   * Returns absolute element position.
   *
   * @return position vector.
   */
  public Vector2f absolutePosition() {
    var screenPos = new Vector2f(this.position());
    for (Node p = this.parent(); p != null; p = p.parent()) {
      screenPos.add(p.position());
    }
    return screenPos;
  }

  /**
   * Used to set node position in coordinates of parent node.
   *
   * <p>Used by layout engine.
   *
   * @param x node x position in coordinates of parent node.
   * @param y node y position in coordinates of parent node.
   */
  public Node position(float x, float y) {
    this.position.set(x, y);
    return this;
  }

  /**
   * Used to set node size.
   *
   * <p>Used by layout engine.
   *
   * @param width node width.
   * @param height node height.
   */
  public Node size(float width, float height) {
    this.size.set(width, height);
    return this;
  }

  /**
   * Shorthand to set attribute.
   *
   * @param key attribute name.
   * @param value attribute value.
   */
  public abstract void setAttribute(String key, String value);

  /**
   * Shorthand to get attribute.
   *
   * @param key attribute name.
   * @return attribute value.
   */
  public abstract String getAttribute(String key);

  /**
   * Returns true if node contains specified attribute.
   *
   * @param attribute attribute check.
   * @return true if node has specified attribute.
   */
  public abstract boolean hasAttribute(String attribute);

  /**
   * Removes attribute and returns it's value.
   *
   * @param attribute attribute to remove.
   */
  public abstract void removeAttribute(String attribute);

  /**
   * Returns true if node contains any attribute.
   *
   * @return true if node contains any attribute.
   */
  public abstract boolean hasAttributes();

  public abstract Map<String, String> attributes();

  public Frame frame() {
    return parent == null ? null : parent.frame();
  }
}
