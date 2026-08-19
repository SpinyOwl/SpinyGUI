package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_DISABLED;
import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_TYPE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_VALUE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.NODE_INPUT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_EMAIL;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RESET;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SEARCH;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SUBMIT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEL;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_URL;

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

  private static final String ATTR_CHECKED = "checked";

  private String type = TYPE_TEXT;
  private String value = "";
  private boolean checked;
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
    checked = attributes().containsKey(ATTR_CHECKED);
  }

  public void type(String type) {
    this.type = type == null || type.isBlank() ? TYPE_TEXT : type;
  }

  /**
   * Replaces the value, clamps existing caret and selection offsets to its UTF-16 length, and snaps
   * either offset backward when it would split a valid surrogate pair in the new value.
   *
   * @param value new value, or {@code null} for an empty value.
   */
  public void value(String value) {
    this.value = value == null ? "" : value;
    caretIndex = TextIndexNormalizer.clampAndSnapBackward(this.value, caretIndex);
    selectionAnchor = TextIndexNormalizer.clampAndSnapBackward(this.value, selectionAnchor);
  }

  public boolean checked() {
    return checked;
  }

  public void checked(boolean checked) {
    this.checked = checked;
  }

  /**
   * Sets and collapses the caret to a UTF-16 offset clamped to {@code [0, value.length()]}, snapping
   * backward to the preceding valid code-point boundary when the clamped offset splits a surrogate
   * pair.
   *
   * @param caretIndex requested UTF-16 offset.
   */
  public void caretIndex(int caretIndex) {
    this.caretIndex = clampTextIndex(caretIndex);
    selectionAnchor = this.caretIndex;
  }

  /**
   * Sets selection anchor and focus as independently clamped UTF-16 offsets, snapping either one
   * backward when it would split a valid surrogate pair.
   *
   * @param anchor requested selection-anchor UTF-16 offset.
   * @param focus requested caret/focus UTF-16 offset.
   */
  public void select(int anchor, int focus) {
    selectionAnchor = clampTextIndex(anchor);
    caretIndex = clampTextIndex(focus);
  }

  /**
   * Sets the selection anchor to a clamped UTF-16 offset without moving the caret, snapping it
   * backward when it would split a valid surrogate pair.
   *
   * @param selectionAnchor requested UTF-16 offset.
   */
  public void selectionAnchor(int selectionAnchor) {
    this.selectionAnchor = clampTextIndex(selectionAnchor);
  }

  public void clearSelection() {
    selectionAnchor = caretIndex;
  }

  public boolean hasSelection() {
    return selectionAnchor != caretIndex;
  }

  /** Returns the smaller absolute UTF-16 selection endpoint. */
  public int selectionStart() {
    return Math.min(selectionAnchor, caretIndex);
  }

  /** Returns the larger absolute UTF-16 selection endpoint. */
  public int selectionEnd() {
    return Math.max(selectionAnchor, caretIndex);
  }

  public void textScrollLeft(float textScrollLeft) {
    this.textScrollLeft = Math.max(0, textScrollLeft);
  }

  @Override
  public boolean disabled() {
    return hasAttribute(ATTR_DISABLED);
  }

  /** Returns whether this type uses the single-line editable text model. */
  public boolean textInput() {
    return TYPE_TEXT.equalsIgnoreCase(type)
        || TYPE_EMAIL.equalsIgnoreCase(type)
        || TYPE_SEARCH.equalsIgnoreCase(type)
        || TYPE_TEL.equalsIgnoreCase(type)
        || TYPE_URL.equalsIgnoreCase(type)
        || TYPE_PASSWORD.equalsIgnoreCase(type);
  }

  public boolean passwordInput() {
    return TYPE_PASSWORD.equalsIgnoreCase(type);
  }

  /** Returns whether this input uses button activation and sizing semantics. */
  public boolean buttonInput() {
    return TYPE_BUTTON.equalsIgnoreCase(type)
        || TYPE_SUBMIT.equalsIgnoreCase(type)
        || TYPE_RESET.equalsIgnoreCase(type);
  }

  public boolean checkboxInput() {
    return TYPE_CHECKBOX.equalsIgnoreCase(type);
  }

  public boolean radioInput() {
    return TYPE_RADIO.equalsIgnoreCase(type);
  }

  public boolean rangeInput() {
    return TYPE_RANGE.equalsIgnoreCase(type);
  }

  public boolean toggleInput() {
    return checkboxInput() || radioInput();
  }

  private int clampTextIndex(int index) {
    return TextIndexNormalizer.clampAndSnapBackward(value, index);
  }
}
