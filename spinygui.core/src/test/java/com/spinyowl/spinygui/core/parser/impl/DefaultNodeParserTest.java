package com.spinyowl.spinygui.core.parser.impl;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.InputElement;
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
