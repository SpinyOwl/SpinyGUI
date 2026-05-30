package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import org.junit.jupiter.api.Test;

class FlexStyleManagerTest {

  @Test
  void recalculate_storesFlexGrowAndShrinkAsFloats() {
    Element element = styledElement("flex-grow: 1.5; flex-shrink: 0.25");

    assertEquals(1.5F, element.resolvedStyle().flexGrow());
    assertEquals(0.25F, element.resolvedStyle().flexShrink());
  }

  @Test
  void recalculate_storesDefaultFlexGrowAndShrinkAsFloats() {
    Element element = styledElement("");

    assertEquals(0F, element.resolvedStyle().flexGrow());
    assertEquals(0F, element.resolvedStyle().flexShrink());
  }

  private Element styledElement(String declarations) {
    PropertyStoreProvider provider = new DefaultPropertyStoreProvider();
    var propertyStore = provider.createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    var styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element element = NodeBuilder.div();
    element.setAttribute("style", declarations);
    frame.addChild(element);

    styleManager.recalculate(frame);

    return element;
  }
}
