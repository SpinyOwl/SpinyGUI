package com.spinyowl.spinygui.core.parser.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import org.junit.jupiter.api.Test;

class DefaultNodeParserWhitespaceTest {

  private final DefaultNodeParser parser = new DefaultNodeParser();

  @Test
  void fromHtml_preservesInternalTextSpaces() {
    Element root = parser.fromHtml("<div>Horizontal auto</div>").asElement();

    Text text = root.childNodes().get(0).asText();

    assertEquals("Horizontal auto", text.content());
  }

  @Test
  void fromHtml_preservesBoundarySpacesAroundInlineElements() {
    Element root = parser.fromHtml("<div>Hello <span>wide</span> world</div>").asElement();
    Text left = root.childNodes().get(0).asText();
    Text middle = root.childNodes().get(1).asElement().childNodes().get(0).asText();
    Text right = root.childNodes().get(2).asText();

    assertEquals("Hello ", left.content());
    assertEquals("wide", middle.content());
    assertEquals(" world", right.content());
  }

  @Test
  void fromHtml_ignoresFormattingWhitespaceOnlyTextNodes() {
    Element root = parser.fromHtml("<div>\n  <span>child</span>\n</div>").asElement();

    assertEquals(1, root.childNodes().size());
    assertEquals("span", root.childNodes().get(0).nodeName());
  }
}
