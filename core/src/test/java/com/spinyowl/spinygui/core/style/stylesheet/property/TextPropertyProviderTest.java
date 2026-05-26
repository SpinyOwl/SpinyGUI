package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_WRAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WORD_BREAK;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WORD_WRAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import org.junit.jupiter.api.Test;

class TextPropertyProviderTest {

  @Test
  void wordBreak_acceptsSupportedValues() {
    Property property = property(WORD_BREAK);
    Element element = NodeBuilder.div();

    property.apply(element, new TermIdent("break-all"));

    assertEquals(WordBreak.BREAK_ALL, element.resolvedStyle().wordBreak());
  }

  @Test
  void wordBreak_rejectsUnsupportedValues() {
    Property property = property(WORD_BREAK);
    Element element = NodeBuilder.div();

    property.apply(element, new TermIdent("invalid"));

    assertNull(element.resolvedStyle().wordBreak());
  }

  @Test
  void wordWrap_updatesOverflowWrap() {
    Property property = property(WORD_WRAP);
    Element element = NodeBuilder.div();

    property.apply(element, new TermIdent("break-word"));

    assertEquals(OverflowWrap.BREAK_WORD, element.resolvedStyle().overflowWrap());
    assertNull(element.resolvedStyle().getSafe(WORD_WRAP));
  }

  @Test
  void wordWrap_acceptsOverflowWrapValues() {
    Property property = property(WORD_WRAP);

    assertTrue(property.getValidator().test(new TermIdent("normal")));
    assertTrue(property.getValidator().test(new TermIdent("break-word")));
    assertTrue(property.getValidator().test(new TermIdent("anywhere")));
    assertFalse(property.getValidator().test(new TermIdent("break-all")));
  }

  @Test
  void wordWrap_doesNotReplaceOverflowWrapPropertyRegistration() {
    Property overflowWrap = property(OVERFLOW_WRAP);
    Property wordWrap = property(WORD_WRAP);

    assertEquals(OVERFLOW_WRAP, overflowWrap.getName());
    assertEquals(WORD_WRAP, wordWrap.getName());
  }

  private Property property(String name) {
    return new TextPropertyProvider().getProperties().stream()
        .filter(property -> name.equals(property.getName()))
        .findFirst()
        .orElseThrow();
  }
}
