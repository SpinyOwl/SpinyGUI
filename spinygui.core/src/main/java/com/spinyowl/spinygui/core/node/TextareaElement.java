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
    setAttributes(attributes);
  }

  /**
   * Replaces the value, clamps existing caret and selection offsets to its UTF-16 length, and snaps
   * either offset backward when it would split a valid surrogate pair in the new value.
   *
   * @param value new value, or {@code null} for an empty value.
   */
  public void value(String value) {
    String normalized = value == null ? "" : value;
    if (java.util.Objects.equals(this.value, normalized)) return;
    this.value = normalized;
    caretIndex = TextIndexNormalizer.clampAndSnapBackward(this.value, caretIndex);
    selectionAnchor = TextIndexNormalizer.clampAndSnapBackward(this.value, selectionAnchor);
    invalidateLayoutSource();
  }

  /**
   * Sets and collapses the caret to a UTF-16 offset clamped to {@code [0, value.length()]}, snapping
   * backward to the preceding valid code-point boundary when the clamped offset splits a surrogate
   * pair.
   *
   * @param caretIndex requested UTF-16 offset.
   */
  public void caretIndex(int caretIndex) {
    int previousCaret = this.caretIndex;
    int previousAnchor = selectionAnchor;
    this.caretIndex = clampTextIndex(caretIndex);
    selectionAnchor = this.caretIndex;
    if (previousCaret != this.caretIndex || previousAnchor != selectionAnchor) {
      invalidatePaintSource();
    }
  }

  /**
   * Sets selection anchor and focus as independently clamped UTF-16 offsets, snapping either one
   * backward when it would split a valid surrogate pair.
   *
   * @param anchor requested selection-anchor UTF-16 offset.
   * @param focus requested caret/focus UTF-16 offset.
   */
  public void select(int anchor, int focus) {
    int previousCaret = caretIndex;
    int previousAnchor = selectionAnchor;
    selectionAnchor = clampTextIndex(anchor);
    caretIndex = clampTextIndex(focus);
    if (previousCaret != caretIndex || previousAnchor != selectionAnchor) invalidatePaintSource();
  }

  /**
   * Sets the selection anchor to a clamped UTF-16 offset without moving the caret, snapping it
   * backward when it would split a valid surrogate pair.
   *
   * @param selectionAnchor requested selection-anchor UTF-16 offset.
   */
  public void selectionAnchor(int selectionAnchor) {
    int normalized = clampTextIndex(selectionAnchor);
    if (this.selectionAnchor == normalized) return;
    this.selectionAnchor = normalized;
    invalidatePaintSource();
  }

  public void clearSelection() {
    if (selectionAnchor == caretIndex) return;
    selectionAnchor = caretIndex;
    invalidatePaintSource();
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

  public void textScrollTop(float textScrollTop) {
    float normalized = Math.max(0, textScrollTop);
    if (Float.compare(this.textScrollTop, normalized) == 0) return;
    this.textScrollTop = normalized;
    invalidatePaintSource();
  }

  public void textScrollLeft(float textScrollLeft) {
    float normalized = Math.max(0, textScrollLeft);
    if (Float.compare(this.textScrollLeft, normalized) == 0) return;
    this.textScrollLeft = normalized;
    invalidatePaintSource();
  }

  private int clampTextIndex(int index) {
    return TextIndexNormalizer.clampAndSnapBackward(value, index);
  }
}
