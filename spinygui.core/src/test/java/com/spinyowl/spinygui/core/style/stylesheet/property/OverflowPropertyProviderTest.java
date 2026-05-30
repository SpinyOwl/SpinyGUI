package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_X;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_Y;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.junit.jupiter.api.Test;

class OverflowPropertyProviderTest {

  @Test
  void overflowX_acceptsSupportedValues() {
    Property property = property(OVERFLOW_X);
    Element element = NodeBuilder.div();

    property.apply(element, new TermIdent("auto"));

    assertEquals(Overflow.AUTO, element.resolvedStyle().overflowX());
  }

  @Test
  void overflowY_acceptsSupportedValues() {
    Property property = property(OVERFLOW_Y);
    Element element = NodeBuilder.div();

    property.apply(element, new TermIdent("scroll"));

    assertEquals(Overflow.SCROLL, element.resolvedStyle().overflowY());
  }

  @Test
  void overflow_singleValue_updatesBothAxes() {
    Property property = property(OVERFLOW);
    Element element = NodeBuilder.div();

    property.apply(element, new TermIdent("hidden"));

    assertEquals(Overflow.HIDDEN, element.resolvedStyle().overflowX());
    assertEquals(Overflow.HIDDEN, element.resolvedStyle().overflowY());
    assertNull(element.resolvedStyle().overflow());
  }

  @Test
  void overflow_twoValues_updatesXThenY() {
    Property property = property(OVERFLOW);
    Element element = NodeBuilder.div();

    property.apply(
        element, new TermList(Operator.SPACE, new TermIdent("auto"), new TermIdent("scroll")));

    assertEquals(Overflow.AUTO, element.resolvedStyle().overflowX());
    assertEquals(Overflow.SCROLL, element.resolvedStyle().overflowY());
  }

  @Test
  void overflow_rejectsUnsupportedValues() {
    Property property = property(OVERFLOW);
    Element element = NodeBuilder.div();

    property.apply(element, new TermIdent("overlay"));

    assertNull(element.resolvedStyle().overflowX());
    assertNull(element.resolvedStyle().overflowY());
  }

  @Test
  void overflow_acceptsOnlyOneOrTwoSupportedIdentifiers() {
    Property property = property(OVERFLOW);

    assertTrue(property.validator().test(new TermIdent("visible")));
    assertTrue(property.validator().test(new TermIdent("hidden")));
    assertTrue(property.validator().test(new TermIdent("auto")));
    assertTrue(property.validator().test(new TermIdent("scroll")));
    assertTrue(
        property
            .validator()
            .test(new TermList(Operator.SPACE, new TermIdent("auto"), new TermIdent("scroll"))));
    assertFalse(property.validator().test(new TermIdent("overlay")));
    assertFalse(
        property
            .validator()
            .test(
                new TermList(
                    Operator.SPACE,
                    new TermIdent("auto"),
                    new TermIdent("scroll"),
                    new TermIdent("hidden"))));
    assertFalse(
        property
            .validator()
            .test(new TermList(Operator.COMMA, new TermIdent("auto"), new TermIdent("scroll"))));
  }

  private Property property(String name) {
    return new OverflowPropertyProvider().getProperties().stream()
        .filter(property -> name.equals(property.name()))
        .findFirst()
        .orElseThrow();
  }
}
