package com.spinyowl.spinygui.core.node;

import java.util.Map;

public final class NodeBuilder {

  // @formatter:off
  public static final String ATTR_TYPE = "type";
  public static final String ATTR_NAME = "name";
  public static final String ATTR_VALUE = "value";

  public static final String TYPE_BUTTON = "button";
  public static final String TYPE_CHECKBOX = "checkbox";
  public static final String TYPE_COLOR = "color";
  public static final String TYPE_DATE = "date";
  public static final String TYPE_DATETIME_LOCAL = "datetime-local";
  public static final String TYPE_EMAIL = "email";
  public static final String TYPE_FILE = "file";
  public static final String TYPE_HIDDEN = "hidden";
  public static final String TYPE_IMAGE = "image";
  public static final String TYPE_MONTH = "month";
  public static final String TYPE_NUMBER = "number";
  public static final String TYPE_PASSWORD = "password";
  public static final String TYPE_RADIO = "radio";
  public static final String TYPE_RANGE = "range";
  public static final String TYPE_RESET = "reset";
  public static final String TYPE_SEARCH = "search";
  public static final String TYPE_SUBMIT = "submit";
  public static final String TYPE_TEL = "tel";
  public static final String TYPE_TEXT = "text";
  public static final String TYPE_TIME = "time";
  public static final String TYPE_URL = "url";
  public static final String TYPE_WEEK = "week";

  public static final String NODE_BUTTON = "button";
  public static final String NODE_INPUT = "input";
  public static final String NODE_DIV = "div";
  public static final String NODE_LABEL = "label";
  // @formatter:on

  private NodeBuilder() {}

  /**
   * Creates button element with provided child nodes.
   *
   * @param nodes child nodes to add.
   * @return button with specified child nodes.
   */
  public static Element button(Node... nodes) {
    var button = new Element(NODE_BUTTON);
    if (nodes != null) {
      for (Node node : nodes) {
        button.addChild(node);
      }
    }
    return button;
  }

  /**
   * Creates button with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return button with specified attributes and child nodes.
   */
  public static Element button(Map<String, String> attributes, Node... nodes) {
    return addAttributes(button(nodes), attributes);
  }

  /**
   * Creates text node with specified content.
   *
   * @param text text node.
   * @return text node with specified content.
   */
  public static Text text(String text) {
    return new Text(text);
  }

  /**
   * Creates input element (text input).
   *
   * @return input element.
   */
  public static EmptyElement input() {
    return new EmptyElement(NODE_INPUT);
  }

  /**
   * Creates input element with specified attributes.
   *
   * @param attributes attributes to add.
   * @return input element with specified attributes.
   */
  public static EmptyElement input(Map<String, String> attributes) {
    return addAttributes(new EmptyElement(NODE_INPUT), attributes);
  }

  /**
   * Creates input element with specified type attribute value.
   *
   * @param type type attribute value.
   * @return input element with specified type attribute value.
   */
  public static EmptyElement input(String type) {
    return addAttributes(new EmptyElement(NODE_INPUT), Map.of(ATTR_TYPE, type));
  }

  /**
   * Creates input with specified name and type attributes.
   *
   * @param type type attribute value.
   * @param name name attribute value.
   * @return input with specified name and type attributes.
   */
  public static EmptyElement input(String type, String name) {
    return addAttributes(new EmptyElement(NODE_INPUT), Map.of(ATTR_TYPE, type, ATTR_NAME, name));
  }

  /**
   * Creates input with specified name, type and value attributes.
   *
   * @param type type attribute value.
   * @param name name attribute value.
   * @param value value attribute value.
   * @return input with specified name, type and value attributes.
   */
  public static EmptyElement input(String type, String name, String value) {
    return addAttributes(
        new EmptyElement(NODE_INPUT), Map.of(ATTR_TYPE, type, ATTR_NAME, name, ATTR_VALUE, value));
  }

  /**
   * Creates label element with provided child nodes.
   *
   * @param nodes child nodes to add.
   * @return label with specified child nodes.
   */
  public static Element label(Node... nodes) {
    var label = new Element(NODE_LABEL);
    for (Node node : nodes) {
      label.addChild(node);
    }
    return label;
  }

  /**
   * Creates label with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return label with specified attributes and child nodes.
   */
  public static Element label(Map<String, String> attributes, Node... nodes) {
    return addAttributes(label(nodes), attributes);
  }

  /**
   * Creates div element with provided child nodes.
   *
   * @param nodes child nodes to add.
   * @return div with specified child nodes.
   */
  public static Element div(Node... nodes) {
    var div = new Element(NODE_DIV);
    for (Node node : nodes) {
      div.addChild(node);
    }
    return div;
  }

  /**
   * Creates div with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return div with specified attributes and child nodes.
   */
  public static Element div(Map<String, String> attributes, Node... nodes) {
    return addAttributes(div(nodes), attributes);
  }

  /**
   * Creates input with {@code type="radio-button"} and with specified name and value attributes.
   *
   * @param name name attribute value.
   * @param value value attribute value.
   * @return input with {@code type="radio-button"} and with specified name and value attributes.
   */
  public static EmptyElement radioButton(String name, String value) {
    return input(TYPE_RADIO, name, value);
  }

  /**
   * Used to add attributes to provided node. Allows to chain calls.
   *
   * @param node node to which attributes should be added.
   * @param attributes attributes to add to node.
   * @param <T> type of node.
   * @return returns filled node.
   */
  public static <T extends Node> T addAttributes(T node, Map<String, String> attributes) {
    node.attributes().putAll(attributes);
    return node;
  }
}
