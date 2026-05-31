package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_TYPE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_VALUE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.NODE_INPUT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Runtime model for an {@code input} element.
 *
 * <p>Input type-specific behavior should be composed outside this node and selected by {@link
 * #type()}, instead of adding subclasses for each input type.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InputElement extends EmptyElement {

  private String type = TYPE_TEXT;
  private String value = "";
  private int caretIndex;
  private float textScrollLeft;

  public InputElement() {
    super(NODE_INPUT);
  }

  public InputElement(Map<String, String> attributes) {
    this();
    attributes().putAll(attributes);
    initializeFromAttributes();
  }

  public void initializeFromAttributes() {
    type(attributes().get(ATTR_TYPE));
    value(attributes().get(ATTR_VALUE));
  }

  public void type(String type) {
    this.type = type == null || type.isBlank() ? TYPE_TEXT : type;
  }

  public void value(String value) {
    this.value = value == null ? "" : value;
    caretIndex = Math.min(caretIndex, this.value.length());
  }

  public void caretIndex(int caretIndex) {
    this.caretIndex = Math.max(0, Math.min(caretIndex, value.length()));
  }

  public void textScrollLeft(float textScrollLeft) {
    this.textScrollLeft = Math.max(0, textScrollLeft);
  }

  public boolean textInput() {
    return TYPE_TEXT.equalsIgnoreCase(type);
  }
}
