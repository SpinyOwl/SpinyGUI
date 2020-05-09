package com.spinyowl.spinygui.core.node;

import java.util.Map;

public final class NodeBuilder {

  public static final String KEY_INPUT = "input";
  public static final String KEY_TYPE = "type";
  public static final String KEY_NAME = "name";
  public static final String KEY_VALUE = "value";

  private NodeBuilder() {
  }

  public static Element button(Node... nodes) {
    Element button = new Element("button");
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

  public static Element label(Map<String, String> attributes, Node... nodes) {
    return addAttributes(label(nodes), attributes);
  }

  public static Element label(Node... nodes) {
    Element label = new Element("label");
    for (Node node : nodes) {
      label.addChild(node);
    }
    return label;
  }

  public static Element div(Map<String, String> attributes, Node... nodes) {
    return addAttributes(div(nodes), attributes);
  }

  public static Element div(Node... nodes) {
    Element div = new Element("div");
    for (Node node : nodes) {
      div.addChild(node);
    }
    return div;
  }

  public static EmptyElement radioButton(
      String name, String value, Map<String, String> attributes
  ) {
    return addAttributes(radioButton(name, value), attributes);
  }

  public static EmptyElement radioButton(String name, String value) {
    return input("radio", name, value);
  }

  private static <T extends Node> T addAttributes(T element, Map<String, String> attributes) {
    element.attributes().putAll(attributes);
    return element;
  }
}
