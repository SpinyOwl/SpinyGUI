package com.spinyowl.spinygui.core.node;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public final class Text extends Node {

  public static final String ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED =
      "Attribute operations are not supported for Text";
  public static final String CHILD_OPERATIONS_ARE_NOT_SUPPORTED =
      "Child operations are not supported for Text";
  private String content;

  public Text() {
    super("#text");
  }

  public Text(String content) {
    this();
    this.content = content;
  }

  /**
   * Used to add a new child node, to an element, as the last child node.
   *
   * @param node node to add.
   */
  @Override
  public void addChild(@NonNull Node node) {
    throw new UnsupportedOperationException("Child operations are not supported for Text.");
  }

  /**
   * Used to remove child node.
   *
   * @param node node to remove.
   */
  @Override
  public void removeChild(@NonNull Node node) {
    throw new UnsupportedOperationException("Child operations are not supported for Text.");
  }

  /**
   * The {@link #childNodes()} method returns a collection of a node's child nodes, as {@code
   * List<Node>} object.
   *
   * <p>The nodes in the collection are sorted as they was added to the element.
   *
   * <p>Tip: To return a collection of a node's element nodes (excluding text and comment nodes),
   * use the {@link #children()} method.
   *
   * @return list of child nodes.
   */
  @Override
  public List<Node> childNodes() {
    return Collections.emptyList();
  }

  /**
   * Returns true if an element has any child nodes, otherwise false.
   *
   * @return true if an element has any child nodes, otherwise false.
   */
  @Override
  public boolean hasChildNodes() {
    return false;
  }

  /**
   * Shorthand to set attribute.
   *
   * @param key attribute name.
   * @param value attribute value.
   */
  @Override
  public void setAttribute(String key, String value) {
    throw new UnsupportedOperationException("Attribute operations are not supported for Text.");
  }

  /**
   * Shorthand to get attribute.
   *
   * @param key attribute name.
   * @return attribute value.
   */
  @Override
  public String getAttribute(String key) {
    return null;
  }

  /**
   * Returns true if node contains specified attribute.
   *
   * @param attribute attribute check.
   * @return true if node has specified attribute.
   */
  @Override
  public boolean hasAttribute(String attribute) {
    return false;
  }

  @Override
  public void removeAttribute(String attribute) {
    // do nothing as there is no attributes in text node
  }

  /**
   * Returns true if node contains any attribute.
   *
   * @return true if node contains any attribute.
   */
  @Override
  public boolean hasAttributes() {
    return false;
  }

  @Override
  public Map<String, String> attributes() {
    return Map.of();
  }
}
