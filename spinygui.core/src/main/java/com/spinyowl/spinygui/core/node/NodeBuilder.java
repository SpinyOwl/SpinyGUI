package com.spinyowl.spinygui.core.node;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class NodeBuilder {

  // @formatter:off
  public static final String ATTR_TYPE = "type";
  public static final String ATTR_NAME = "name";
  public static final String ATTR_VALUE = "value";
  public static final String ATTR_DISABLED = "disabled";
  public static final String ATTR_ROWS = "rows";
  public static final String ATTR_COLS = "cols";

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
  public static final String NODE_TEXTAREA = "textarea";
  public static final String NODE_DIV = "div";
  public static final String NODE_LABEL = "label";
  // @formatter:on

  private NodeBuilder() {}

  public record Attribute(String name, String value) {
    public Attribute {
      Objects.requireNonNull(name);
      Objects.requireNonNull(value);
    }
  }

  public record Attributes(Map<String, String> values) {
    public Attributes {
      Objects.requireNonNull(values);
      values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }
  }

  public static Attribute attr(String name, String value) {
    return new Attribute(name, value);
  }

  public static Attributes attrs(Attribute... attributes) {
    Objects.requireNonNull(attributes);

    Map<String, String> values = new LinkedHashMap<>();
    for (Attribute attribute : attributes) {
      Objects.requireNonNull(attribute);
      values.put(attribute.name(), attribute.value());
    }
    return new Attributes(values);
  }

  public static Attribute id(String value) {
    return attr("id", value);
  }

  public static Attribute cssClass(String value) {
    return attr("class", value);
  }

  public static Attribute style(String value) {
    return attr("style", value);
  }

  public static Attribute name(String value) {
    return attr(ATTR_NAME, value);
  }

  public static Attribute type(String value) {
    return attr(ATTR_TYPE, value);
  }

  public static Attribute value(String value) {
    return attr(ATTR_VALUE, value);
  }

  /** Creates a boolean {@code disabled} attribute. */
  public static Attribute disabled() {
    return attr(ATTR_DISABLED, "");
  }

  public static Attribute rows(String value) {
    return attr(ATTR_ROWS, value);
  }

  public static Attribute cols(String value) {
    return attr(ATTR_COLS, value);
  }

  /**
   * Creates frame with provided child nodes.
   *
   * @param nodes child nodes to add.
   * @return frame with specified child nodes.
   */
  public static Frame frame(Node... nodes) {
    var div = new Frame();
    for (Node node : nodes) {
      div.addChild(node);
    }
    return div;
  }

  /**
   * Creates frame with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return frame with specified attributes and child nodes.
   */
  public static Frame frame(Map<String, String> attributes, Node... nodes) {
    return addAttributes(frame(nodes), attributes);
  }

  /**
   * Creates frame with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return frame with specified attributes and child nodes.
   */
  public static Frame frame(Attributes attributes, Node... nodes) {
    return addAttributes(frame(nodes), attributes);
  }

  /**
   * Creates button element with provided child nodes.
   *
   * @param nodes child nodes to add.
   * @return button with specified child nodes.
   */
  public static ButtonElement button(Node... nodes) {
    var button = new ButtonElement();
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
  public static ButtonElement button(Map<String, String> attributes, Node... nodes) {
    return addAttributes(button(nodes), attributes);
  }

  /**
   * Creates button with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return button with specified attributes and child nodes.
   */
  public static ButtonElement button(Attributes attributes, Node... nodes) {
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
  public static InputElement input() {
    return new InputElement();
  }

  /**
   * Creates input element with specified attributes.
   *
   * @param attributes attributes to add.
   * @return input element with specified attributes.
   */
  public static InputElement input(Map<String, String> attributes) {
    return new InputElement(attributes);
  }

  /**
   * Creates input element with specified attributes.
   *
   * @param attributes attributes to add.
   * @return input element with specified attributes.
   */
  public static InputElement input(Attributes attributes) {
    return input(attributes.values());
  }

  /**
   * Creates input element with specified type attribute value.
   *
   * @param type type attribute value.
   * @return input element with specified type attribute value.
   */
  public static InputElement input(String type) {
    return input(Map.of(ATTR_TYPE, type));
  }

  /**
   * Creates input with specified name and type attributes.
   *
   * @param type type attribute value.
   * @param name name attribute value.
   * @return input with specified name and type attributes.
   */
  public static InputElement input(String type, String name) {
    return input(Map.of(ATTR_TYPE, type, ATTR_NAME, name));
  }

  /**
   * Creates input with specified name, type and value attributes.
   *
   * @param type type attribute value.
   * @param name name attribute value.
   * @param value value attribute value.
   * @return input with specified name, type and value attributes.
   */
  public static InputElement input(String type, String name, String value) {
    return input(Map.of(ATTR_TYPE, type, ATTR_NAME, name, ATTR_VALUE, value));
  }

  /**
   * Creates textarea element with empty runtime value.
   *
   * @return textarea element.
   */
  public static TextareaElement textarea() {
    return new TextareaElement();
  }

  /**
   * Creates textarea element with specified runtime value.
   *
   * @param value textarea runtime value.
   * @return textarea element.
   */
  public static TextareaElement textarea(String value) {
    return new TextareaElement(value);
  }

  /**
   * Creates textarea element with specified attributes and runtime value.
   *
   * @param attributes attributes to add.
   * @param value textarea runtime value.
   * @return textarea element with specified attributes and value.
   */
  public static TextareaElement textarea(Map<String, String> attributes, String value) {
    return new TextareaElement(attributes, value);
  }

  /**
   * Creates textarea element with specified attributes and runtime value.
   *
   * @param attributes attributes to add.
   * @param value textarea runtime value.
   * @return textarea element with specified attributes and value.
   */
  public static TextareaElement textarea(Attributes attributes, String value) {
    return textarea(attributes.values(), value);
  }

  /**
   * Creates label element with provided child nodes.
   *
   * @param text text content.
   * @return label with specified child nodes.
   */
  public static Element label(String text) {
    var label = new Element(NODE_LABEL);
    label.addChild(text(text));
    return label;
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
   * Creates label with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return label with specified attributes and child nodes.
   */
  public static Element label(Attributes attributes, Node... nodes) {
    return addAttributes(label(nodes), attributes);
  }

  /**
   * Creates div element with provided child nodes.
   *
   * @param text text content.
   * @return div with specified child nodes.
   */
  public static Element div(String text) {
    var div = new Element(NODE_DIV);
    div.addChild(text(text));
    return div;
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
   * Creates div with specified attributes and child nodes.
   *
   * @param attributes attributes to add.
   * @param nodes child nodes to add.
   * @return div with specified attributes and child nodes.
   */
  public static Element div(Attributes attributes, Node... nodes) {
    return addAttributes(div(nodes), attributes);
  }

  /**
   * Creates input with {@code type="radio-button"} and with specified name and value attributes.
   *
   * @param name name attribute value.
   * @param value value attribute value.
   * @return input with {@code type="radio-button"} and with specified name and value attributes.
   */
  public static InputElement radioButton(String name, String value) {
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
    if (node instanceof InputElement input) {
      input.initializeFromAttributes();
    } else if (node instanceof ButtonElement button) {
      button.initializeFromAttributes();
    }
    return node;
  }

  /**
   * Used to add attributes to provided node. Allows to chain calls.
   *
   * @param node node to which attributes should be added.
   * @param attributes attributes to add to node.
   * @param <T> type of node.
   * @return returns filled node.
   */
  public static <T extends Node> T addAttributes(T node, Attributes attributes) {
    return addAttributes(node, attributes.values());
  }
}
