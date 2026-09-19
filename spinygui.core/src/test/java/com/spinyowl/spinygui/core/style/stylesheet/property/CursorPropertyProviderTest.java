package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.CURSOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.CursorType;
import org.junit.jupiter.api.Test;

class CursorPropertyProviderTest {
  @Test
  void cursor_acceptsBoundedCssKeywords() {
    Property property = property();

    assertTrue(property.validator().test(new TermIdent("pointer")));
    assertTrue(property.validator().test(new TermIdent("nwse-resize")));
    assertFalse(property.validator().test(new TermIdent("wait")));
  }

  @Test
  void cursor_updatesResolvedStyle() {
    var element = NodeBuilder.div();

    property().apply(element, new TermIdent("move"));

    assertEquals(CursorType.MOVE, element.resolvedStyle().cursor());
  }

  private Property property() {
    return new CursorPropertyProvider().getProperties().stream()
        .filter(property -> CURSOR.equals(property.name()))
        .findFirst()
        .orElseThrow();
  }
}
