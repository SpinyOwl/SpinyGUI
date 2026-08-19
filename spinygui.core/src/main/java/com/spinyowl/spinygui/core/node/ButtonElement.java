package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_DISABLED;
import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_TYPE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.NODE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RESET;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SUBMIT;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Runtime model for a content-bearing {@code button} element. */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ButtonElement extends Element {

  private String type = TYPE_SUBMIT;

  public ButtonElement() {
    super(NODE_BUTTON);
  }

  public ButtonElement(Map<String, String> attributes) {
    this();
    attributes().putAll(attributes);
    initializeFromAttributes();
  }

  public void initializeFromAttributes() {
    type(attributes().get(ATTR_TYPE));
  }

  public void type(String type) {
    this.type = type == null || type.isBlank() ? TYPE_SUBMIT : type;
  }

  @Override
  public boolean disabled() {
    return hasAttribute(ATTR_DISABLED);
  }

  public boolean plainButton() {
    return TYPE_BUTTON.equalsIgnoreCase(type);
  }

  public boolean submitButton() {
    return TYPE_SUBMIT.equalsIgnoreCase(type);
  }

  public boolean resetButton() {
    return TYPE_RESET.equalsIgnoreCase(type);
  }

  public boolean activatable() {
    return !disabled() && (plainButton() || submitButton() || resetButton());
  }
}
