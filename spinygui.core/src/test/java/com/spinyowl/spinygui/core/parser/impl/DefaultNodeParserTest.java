package com.spinyowl.spinygui.core.parser.impl;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.InputElement;
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
}
