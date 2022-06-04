package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.node.intersection.Intersection;
import com.spinyowl.spinygui.core.node.intersection.Intersections;
import com.spinyowl.spinygui.core.node.layout.Box;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

  /** Determines whether this node hovered or not (cursor is over this node). */
  private boolean hovered;

  /** Determines whether this node focused or not. */
  private boolean focused;

  /** Determines whether this node pressed or not (Mouse button is down and on this node). */
  private boolean pressed;

  /**
   * Node intersection. During initialization used {@link Intersections#getDefaultIntersection()}.
   * Used to detect intersection of point on virtual window surface and node.
   */
  @NonNull private Intersection intersection = Intersections.getDefaultIntersection();

  @Setter(AccessLevel.MODULE)
  private Node nextSibling;

  @Setter(AccessLevel.MODULE)
  private Node previousSibling;

  /// LAYOUT SECTION

  /**
   * Parent defined by layout service.
   *
   * <p>It could be different from nodes original parent based on position property (for example if
   * node was removed from normal flow).
   */
  private Node layoutParent;

  /**
   * List of child nodes defined by layout service.
   *
   * <p>It could be different from nodes original parent based on position property (for example if
   * node was removed from normal flow).
   */
  private List<Node> layoutChildNodes;

  /** Dimensions of node defined by layout service. */
  @Setter(AccessLevel.NONE)
  private final Box box = new Box();

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
   * <p>The nodes in the collection are sorted as they were added to the element.
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
   * <p>The elements in the collection are sorted as they were added tp the element.
   *
   * @return list of child elements.
   */
  @SuppressWarnings("squid:S6204")
  public List<Element> children() {
    return childNodes().stream()
        .filter(Element.class::isInstance)
        .map(Element.class::cast)
        // we need to collect with `Collectors.toList()` instead of `Stream::toList` as we need it
        // to be modifiable list.
        .collect(Collectors.toList());
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
   * Removes attribute and returns its value.
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

  public Element asElement() {
    return (Element) this;
  }

  public Text asText() {
    return (Text) this;
  }

  /** Returns absolute position of node (border-box) on virtual window surface. */
  public Vector2f absolutePosition() {
    var borderBox = this.box().borderBoxPosition();
    if (this.layoutParent != null) {
      borderBox.add(layoutParent.absolutePosition());
    }
    return borderBox;
  }

  /** Returns size of node (border-box) on virtual window surface. */
  public Vector2f size() {
    return box.borderBoxSize();
  }
}
