package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.node.NodeBuilder.NODE_TEXTAREA;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Runtime model for an editable multiline {@code textarea} element. */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TextareaElement extends Element {

  private String value = "";
  private int caretIndex;
  private int selectionAnchor;
  private float textScrollTop;
  private float textScrollLeft;

  public TextareaElement() {
    super(NODE_TEXTAREA);
  }

  public TextareaElement(String value) {
    this();
    value(value);
  }

  public TextareaElement(Map<String, String> attributes, String value) {
    this(value);
    attributes().putAll(attributes);
  }

  public void value(String value) {
    this.value = value == null ? "" : value;
    caretIndex = Math.min(caretIndex, this.value.length());
    selectionAnchor = Math.min(selectionAnchor, this.value.length());
  }

  public void caretIndex(int caretIndex) {
    this.caretIndex = clampTextIndex(caretIndex);
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

  public void textScrollTop(float textScrollTop) {
    this.textScrollTop = Math.max(0, textScrollTop);
  }

  public void textScrollLeft(float textScrollLeft) {
    this.textScrollLeft = Math.max(0, textScrollLeft);
  }

  private int clampTextIndex(int index) {
    return Math.max(0, Math.min(index, value.length()));
  }
}
