package com.spinyowl.spinygui.core.node;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SUBMIT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.attr;
import static com.spinyowl.spinygui.core.node.NodeBuilder.attrs;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.cssClass;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.id;
import static com.spinyowl.spinygui.core.node.NodeBuilder.input;
import static com.spinyowl.spinygui.core.node.NodeBuilder.name;
import static com.spinyowl.spinygui.core.node.NodeBuilder.style;
import static com.spinyowl.spinygui.core.node.NodeBuilder.textarea;
import static com.spinyowl.spinygui.core.node.NodeBuilder.type;
import static com.spinyowl.spinygui.core.node.NodeBuilder.value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class NodeBuilderTest {

  @Test
  void attrsHelpersCreateElementAttributes() {
    Element element = div(attrs(id("metric"), cssClass("diagnostics-metric"), style("color: red")));

    assertEquals("metric", element.getAttribute("id"));
    assertEquals("diagnostics-metric", element.getAttribute("class"));
    assertEquals("color: red", element.getAttribute("style"));
  }

  @Test
  void mapAttributesStillWork() {
    Element element = div(Map.of("id", "metric", "class", "diagnostics-metric"));

    assertEquals("metric", element.getAttribute("id"));
    assertEquals("diagnostics-metric", element.getAttribute("class"));
  }

  @Test
  void duplicateAttributesUseLastValue() {
    Element element = div(attrs(attr("id", "first"), attr("id", "second")));

    assertEquals("second", element.getAttribute("id"));
  }

  @Test
  void buttonCreatesButtonElementWithChildContent() {
    ButtonElement element = button(NodeBuilder.text("Save"));

    assertEquals("button", element.nodeName());
    assertEquals(1, element.childNodes().size());
    assertEquals("Save", ((Text) element.childNodes().getFirst()).content());
  }

  @Test
  void buttonDefaultsToSubmitType() {
    ButtonElement element = button();

    assertEquals(TYPE_SUBMIT, element.type());
    assertTrue(element.submitButton());
    assertTrue(element.activatable());
  }

  @Test
  void buttonAcceptsExplicitButtonType() {
    ButtonElement element = button(attrs(type(TYPE_BUTTON)));

    assertEquals(TYPE_BUTTON, element.getAttribute("type"));
    assertEquals(TYPE_BUTTON, element.type());
    assertTrue(element.plainButton());
  }

  @Test
  void addAttributesRefreshesButtonRuntimeState() {
    ButtonElement element = button();

    NodeBuilder.addAttributes(element, attrs(type(TYPE_BUTTON)));

    assertEquals(TYPE_BUTTON, element.type());
  }

  @Test
  void attrRejectsNullName() {
    assertThrows(NullPointerException.class, () -> attr(null, "value"));
  }

  @Test
  void attrRejectsNullValue() {
    assertThrows(NullPointerException.class, () -> attr("id", null));
  }

  @Test
  void inputAcceptsAttributesHelper() {
    InputElement element = input(attrs(type(TYPE_PASSWORD), name("password"), value("secret")));

    assertEquals(TYPE_PASSWORD, element.getAttribute("type"));
    assertEquals("password", element.getAttribute("name"));
    assertEquals("secret", element.getAttribute("value"));
    assertEquals(TYPE_PASSWORD, element.type());
    assertEquals("secret", element.value());
  }

  @Test
  void inputDefaultsToTextTypeAndEmptyValue() {
    InputElement element = input();

    assertEquals(TYPE_TEXT, element.type());
    assertEquals("", element.value());
    assertEquals(0, element.caretIndex());
  }

  @Test
  void inputWithTypeNameAndValueInitializesRuntimeState() {
    InputElement element = input(TYPE_TEXT, "username", "alice");

    assertEquals(TYPE_TEXT, element.type());
    assertEquals("username", element.getAttribute("name"));
    assertEquals("alice", element.value());
    assertEquals("alice", element.getAttribute("value"));
  }

  @Test
  void addAttributesRefreshesInputRuntimeState() {
    InputElement element = input();

    NodeBuilder.addAttributes(element, attrs(type(TYPE_PASSWORD), value("secret")));

    assertEquals(TYPE_PASSWORD, element.type());
    assertEquals("secret", element.value());
  }

  @Test
  void textareaDefaultsToEmptyValue() {
    TextareaElement element = textarea();

    assertEquals("textarea", element.nodeName());
    assertEquals("", element.value());
    assertEquals(0, element.caretIndex());
  }

  @Test
  void textareaInitializesMultilineValue() {
    TextareaElement element = textarea("a\nb");

    assertEquals("a\nb", element.value());
  }

  @Test
  void textareaValueClampsCaretAndSelection() {
    TextareaElement element = textarea("abcdef");
    element.select(5, 6);

    element.value("abc");

    assertEquals(3, element.selectionAnchor());
    assertEquals(3, element.caretIndex());
  }
}
