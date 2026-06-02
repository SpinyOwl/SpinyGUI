package com.spinyowl.spinygui.core.parser.impl;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SUBMIT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import org.junit.jupiter.api.Test;

class DefaultNodeParserTest {

  private final DefaultNodeParser parser = new DefaultNodeParser();

  @Test
  void fromHtml_whenInputHasNoType_parsesTextInput() {
    InputElement input = assertInstanceOf(InputElement.class, parser.fromHtml("<input value=\"abc\">"));

    assertEquals(TYPE_TEXT, input.type());
    assertEquals("abc", input.value());
    assertFalse(input.hasChildNodes());
  }

  @Test
  void fromHtml_whenInputTypeIsText_parsesTextInput() {
    InputElement input =
        assertInstanceOf(InputElement.class, parser.fromHtml("<input type=\"text\" value=\"abc\">"));

    assertEquals(TYPE_TEXT, input.type());
    assertEquals("abc", input.value());
  }

  @Test
  void fromHtml_whenInputTypeIsUnsupported_parsesInputElementSafely() {
    InputElement input =
        assertInstanceOf(
            InputElement.class, parser.fromHtml("<input type=\"button\" value=\"Click\">"));

    assertEquals(TYPE_BUTTON, input.type());
    assertEquals("Click", input.value());
  }

  @Test
  void toHtml_whenInputValueChanged_serializesRuntimeValue() {
    InputElement input =
        assertInstanceOf(InputElement.class, parser.fromHtml("<input type=\"text\" value=\"old\">"));
    input.value("new");

    String html = parser.toHtml(input, false);

    assertTrue(html.contains("type=\"text\""));
    assertTrue(html.contains("value=\"new\""));
  }

  @Test
  void fromHtml_whenButtonHasText_parsesButtonElementWithTextChild() {
    ButtonElement button =
        assertInstanceOf(ButtonElement.class, parser.fromHtml("<button>Save</button>"));

    assertEquals(TYPE_SUBMIT, button.type());
    assertEquals(1, button.childNodes().size());
    assertEquals("Save", assertInstanceOf(Text.class, button.childNodes().getFirst()).content());
  }

  @Test
  void fromHtml_whenButtonHasNestedContent_preservesChildTree() {
    ButtonElement button =
        assertInstanceOf(
            ButtonElement.class,
            parser.fromHtml("<button type=\"button\"><span>Save</span></button>"));

    assertEquals(TYPE_BUTTON, button.type());
    Element span = assertInstanceOf(Element.class, button.childNodes().getFirst());
    assertEquals("span", span.nodeName());
    assertEquals("Save", assertInstanceOf(Text.class, span.childNodes().getFirst()).content());
  }

  @Test
  void toHtml_whenButtonHasChildren_serializesTypeAndChildren() {
    ButtonElement button =
        assertInstanceOf(
            ButtonElement.class,
            parser.fromHtml("<button type=\"button\"><span>Save</span></button>"));

    String html = parser.toHtml(button, false);

    assertTrue(html.contains("<button"));
    assertTrue(html.contains("type=\"button\""));
    assertTrue(html.contains("<span>Save</span>"));
    assertTrue(html.contains("</button>"));
  }

  @Test
  void fromHtml_whenInputTypeIsButton_keepsInputValueBasedAndChildless() {
    InputElement input =
        assertInstanceOf(
            InputElement.class,
            parser.fromHtml("<input type=\"button\" value=\"Save\">Ignored</input>"));

    assertEquals(TYPE_BUTTON, input.type());
    assertEquals("Save", input.value());
    assertFalse(input.hasChildNodes());
  }

  @Test
  void fromHtml_whenTextareaHasText_parsesTextareaValueWithoutChildren() {
    TextareaElement textarea =
        assertInstanceOf(TextareaElement.class, parser.fromHtml("<textarea>abc</textarea>"));

    assertEquals("abc", textarea.value());
    assertFalse(textarea.hasChildNodes());
  }

  @Test
  void toHtml_whenTextareaValueChanged_serializesRuntimeValueAsTextContent() {
    TextareaElement textarea =
        assertInstanceOf(
            TextareaElement.class,
            parser.fromHtml("<textarea id=\"notes\" name=\"body\">old</textarea>"));
    textarea.value("new & value");

    String html = parser.toHtml(textarea, false);

    assertTrue(html.contains("id=\"notes\""));
    assertTrue(html.contains("name=\"body\""));
    assertTrue(html.contains(">new &amp; value</textarea>"));
    assertFalse(html.contains("value="));
  }
}
