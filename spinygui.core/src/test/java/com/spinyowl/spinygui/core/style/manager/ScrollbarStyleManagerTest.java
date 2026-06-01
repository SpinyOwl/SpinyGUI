package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import org.junit.jupiter.api.Test;

class ScrollbarStyleManagerTest {

  @Test
  void recalculateResolvesScrollbarPseudoStyleSeparatelyFromElementStyle() {
    Element element =
        styledElement(".panel::-webkit-scrollbar-thumb { background-color: red; }", null);

    assertEquals(Color.RED, element.scrollbarStyle(ScrollbarPart.THUMB).backgroundColor());
    assertEquals(Color.TRANSPARENT, element.resolvedStyle().backgroundColor());
  }

  @Test
  void recalculateUsesPseudoSpecificityAndKeepsElementRuleSeparate() {
    Element element =
        styledElement(
            """
            .panel::-webkit-scrollbar-thumb { background-color: red; }
            div.panel::-webkit-scrollbar-thumb { background-color: blue; }
            .panel { background-color: green; }
            """,
            null);

    assertEquals(Color.BLUE, element.scrollbarStyle(ScrollbarPart.THUMB).backgroundColor());
    assertEquals(Color.GREEN, element.resolvedStyle().backgroundColor());
  }

  @Test
  void recalculateUsesPseudoSourceOrderWithinSameSpecificity() {
    Element element =
        styledElement(
            """
            .panel::-webkit-scrollbar-thumb { background-color: red; }
            .panel::-webkit-scrollbar-thumb { background-color: blue; }
            """,
            null);

    assertEquals(Color.BLUE, element.scrollbarStyle(ScrollbarPart.THUMB).backgroundColor());
  }

  @Test
  void recalculateAppliesInlineStyleOnlyToElement() {
    Element element =
        styledElement(
            ".panel::-webkit-scrollbar-thumb { background-color: red; }",
            "background-color: blue");

    assertEquals(Color.RED, element.scrollbarStyle(ScrollbarPart.THUMB).backgroundColor());
    assertEquals(Color.BLUE, element.resolvedStyle().backgroundColor());
  }

  @Test
  void recalculateKeepsNonPseudoSelectorBehaviorUnchanged() {
    Element element = styledElement(".panel { background-color: red; }", null);

    assertEquals(Color.RED, element.resolvedStyle().backgroundColor());
    assertTrue(element.scrollbarStyles().isEmpty());
  }

  private Element styledElement(String css, String inlineStyle) {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element element = new Element("div");
    element.setAttribute("class", "panel");
    if (inlineStyle != null) {
      element.style(inlineStyle);
    }
    frame.addChild(element);
    frame.styleSheets().add(parser.parse(css));

    styleManager.recalculate(frame);

    return element;
  }
}
