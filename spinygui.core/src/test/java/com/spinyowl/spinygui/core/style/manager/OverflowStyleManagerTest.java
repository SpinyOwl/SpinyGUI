package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.junit.jupiter.api.Test;

class OverflowStyleManagerTest {

  @Test
  void recalculateResolvesOverflowShorthandSingleValue() {
    Element element = styledElement("overflow: hidden");

    assertEquals(Overflow.HIDDEN, element.resolvedStyle().overflowX());
    assertEquals(Overflow.HIDDEN, element.resolvedStyle().overflowY());
  }

  @Test
  void recalculateResolvesOverflowShorthandTwoValues() {
    Element element = styledElement("overflow: auto scroll");

    assertEquals(Overflow.AUTO, element.resolvedStyle().overflowX());
    assertEquals(Overflow.SCROLL, element.resolvedStyle().overflowY());
  }

  @Test
  void recalculateResolvesOverflowLonghands() {
    Element element = styledElement("overflow-x: auto; overflow-y: scroll");

    assertEquals(Overflow.AUTO, element.resolvedStyle().overflowX());
    assertEquals(Overflow.SCROLL, element.resolvedStyle().overflowY());
  }

  @Test
  void recalculateRejectsUnsupportedOverflowValue() {
    Element element = styledElement("overflow: overlay");

    assertEquals(Overflow.VISIBLE, element.resolvedStyle().overflowX());
    assertEquals(Overflow.VISIBLE, element.resolvedStyle().overflowY());
  }

  private Element styledElement(String style) {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element element = new Element("div");
    element.style(style);
    frame.addChild(element);

    styleManager.recalculate(frame);

    return element;
  }
}
