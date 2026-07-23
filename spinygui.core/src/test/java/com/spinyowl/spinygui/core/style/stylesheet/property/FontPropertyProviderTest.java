package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_FAMILY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FontPropertyProviderTest {

  @Test
  void fontFamily_defaultPreservesBundledFallbackOrder() {
    Element element = NodeBuilder.div();

    property().apply(element, null);

    assertEquals(List.of("Roboto", "Noto Sans CJK SC"), element.resolvedStyle().fontFamilies());
  }

  @Test
  void fontFamily_preservesQuotedCommaSeparatedAndUnavailableFamilies() {
    Element element = NodeBuilder.div();

    property()
        .apply(
            element,
            new TermList(
                Operator.COMMA,
                new TermIdent("\"Unavailable Font\""),
                new TermIdent("\"Noto Sans CJK SC\""),
                new TermIdent("Roboto")));

    assertEquals(
        List.of("Unavailable Font", "Noto Sans CJK SC", "Roboto"),
        element.resolvedStyle().fontFamilies());
  }

  @Test
  void fontFamily_resolvedStyleStoresAnImmutableCopy() {
    Element element = NodeBuilder.div();
    List<String> families = new ArrayList<>(List.of("Roboto", "Noto Sans CJK SC"));

    element.resolvedStyle().fontFamilies(families);
    families.clear();

    assertEquals(List.of("Roboto", "Noto Sans CJK SC"), element.resolvedStyle().fontFamilies());
  }

  private Property property() {
    return new FontPropertyProvider().getProperties().stream()
        .filter(property -> FONT_FAMILY.equals(property.name()))
        .findFirst()
        .orElseThrow();
  }
}
