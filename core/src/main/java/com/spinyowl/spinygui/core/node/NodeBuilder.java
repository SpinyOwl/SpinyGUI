package com.spinyowl.spinygui.core.node;

import java.util.Map;

public final class NodeBuilder {

  //@formatter:off
  public static final String KEY_INPUT = "input";
  public static final String KEY_TYPE  = "type";
  public static final String KEY_NAME  = "name";
  public static final String KEY_VALUE = "value";

  public static final String INPUT_BUTTON         = "button";
  public static final String INPUT_CHECKBOX       = "checkbox";
  public static final String INPUT_COLOR          = "color";
  public static final String INPUT_DATE           = "date";
  public static final String INPUT_DATETIME_LOCAL = "datetime-local";
  public static final String INPUT_EMAIL          = "email";
  public static final String INPUT_FILE           = "file";
  public static final String INPUT_HIDDEN         = "hidden";
  public static final String INPUT_IMAGE          = "image";
  public static final String INPUT_MONTH          = "month";
  public static final String INPUT_NUMBER         = "number";
  public static final String INPUT_PASSWORD       = "password";
  public static final String INPUT_RADIO          = "radio";
  public static final String INPUT_RANGE          = "range";
  public static final String INPUT_RESET          = "reset";
  public static final String INPUT_SEARCH         = "search";
  public static final String INPUT_SUBMIT         = "submit";
  public static final String INPUT_TEL            = "tel";
  public static final String INPUT_TEXT           = "text";
  public static final String INPUT_TIME           = "time";
  public static final String INPUT_URL            = "url";
  public static final String INPUT_WEEK           = "week";
  //@formatter:on

  private NodeBuilder() {
  }

  public static Element button(Node... nodes) {
    Element button = new Element(INPUT_BUTTON);
    if (nodes != null) {
      for (Node node : nodes) {
        button.addChild(node);
      }
    }
    return button;
  }

  public static Element button(Map<String, String> attributes, Node... nodes) {
    return addAttributes(button(nodes), attributes);
  }

  public static Text text(String text) {
    return new Text(text);
  }

  public static EmptyElement input() {
    return new EmptyElement(KEY_INPUT);
  }

  public static EmptyElement input(Map<String, String> attributes) {
    return addAttributes(new EmptyElement(KEY_INPUT), attributes);
  }

  public static EmptyElement input(String type) {
    return addAttributes(new EmptyElement(KEY_INPUT), Map.of(KEY_TYPE, type));
  }

  public static EmptyElement input(String type, String name) {
    return addAttributes(new EmptyElement(KEY_INPUT), Map.of(KEY_TYPE, type, KEY_NAME, name));
  }

  public static EmptyElement input(String type, String name, String value) {
    return addAttributes(new EmptyElement(KEY_INPUT),
        Map.of(KEY_TYPE, type, KEY_NAME, name, KEY_VALUE, value));
  }

  public static Element label(Node... nodes) {
    Element label = new Element("label");
    for (Node node : nodes) {
      label.addChild(node);
    }
    return label;
  }

  public static Element label(Map<String, String> attributes, Node... nodes) {
    return addAttributes(label(nodes), attributes);
  }

  public static Element div(Node... nodes) {
    Element div = new Element("div");
    for (Node node : nodes) {
      div.addChild(node);
    }
    return div;
  }

  public static Element div(Map<String, String> attributes, Node... nodes) {
    return addAttributes(div(nodes), attributes);
  }

  public static EmptyElement radioButton(String name, String value) {
    return input(INPUT_RADIO, name, value);
  }

  public static EmptyElement radioButton(String name, String value,
      Map<String, String> attributes) {
    return addAttributes(radioButton(name, value), attributes);
  }

  private static <T extends Node> T addAttributes(T element, Map<String, String> attributes) {
    element.attributes().putAll(attributes);
    return element;
  }
}
