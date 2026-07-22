package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_TYPE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_VALUE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.NODE_INPUT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Runtime model for an {@code input} element.
 *
 * <p>Input type-specific behavior should be composed outside this node and selected by its type,
 * instead of adding subclasses for each input type.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InputElement extends EmptyElement {

  private String type = TYPE_TEXT;
  private String value = "";
  private int caretIndex;
  private int selectionAnchor;
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
    selectionAnchor = Math.min(selectionAnchor, this.value.length());
  }

  public void caretIndex(int caretIndex) {
    this.caretIndex = Math.max(0, Math.min(caretIndex, value.length()));
    selectionAnchor = this.caretIndex;
  }

  public void select(int anchor, int focus) {
    selectionAnchor = clampTextIndex(anchor);
    caretIndex = clampTextIndex(focus);
  }

  public void selectionAnchor(int selectionAnchor) {
    this.selectionAnchor = clampTextIndex(selectionAnchor);
  }

  public void clearSelection() {
    selectionAnchor = caretIndex;
  }

  public boolean hasSelection() {
    return selectionAnchor != caretIndex;
  }

  public int selectionStart() {
    return Math.min(selectionAnchor, caretIndex);
  }

  public int selectionEnd() {
    return Math.max(selectionAnchor, caretIndex);
  }

  public void textScrollLeft(float textScrollLeft) {
    this.textScrollLeft = Math.max(0, textScrollLeft);
  }

  public boolean textInput() {
    return TYPE_TEXT.equalsIgnoreCase(type);
  }

  public boolean buttonInput() {
    return TYPE_BUTTON.equalsIgnoreCase(type);
  }

  private int clampTextIndex(int index) {
    return Math.max(0, Math.min(index, value.length()));
  }
}
