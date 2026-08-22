package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import org.junit.jupiter.api.Test;

class StyleManagerInteractionImpactTest {
  @Test
  void classifiesPaintLayoutTransformAndUnsupportedHoverDeltasConservatively() {
    assertEquals(
        StyleImpact.PAINT_ONLY,
        hoverImpact("div:hover { color: red; }", defaultManager()));
    assertEquals(
        StyleImpact.LAYOUT,
        hoverImpact("div:hover { width: 42px; }", defaultManager()));
    assertEquals(
        StyleImpact.LAYOUT,
        hoverImpact(
            "div { border: 8px none red; } div:hover { border-style: solid; }",
            defaultManager()));
    assertEquals(
        StyleImpact.TRANSFORM,
        hoverImpact("div:hover { transform: translateX(4px); }", defaultManager()));

    DefaultPropertyStore customStore = new DefaultPropertyStore();
    customStore.addProperty(
        "custom-effect",
        Property.builder()
            .name("custom-effect")
            .defaultValue(new TermIdent("off"))
            .updater((term, styles) -> styles.put("custom-effect", term.value()))
            .validator(term -> true)
            .build());
    StyleSheetParser customParser = StyleSheetParserFactory.createParser(customStore);
    assertEquals(
        StyleImpact.FULL_UNKNOWN,
        hoverImpact(
            "div:hover { custom-effect: on; }",
            new ManagerAndParser(new StyleManagerImpl(customStore, customParser), customParser)));
  }

  @Test
  void scrollbarPseudoStyleAdditionAndRemovalAreBothUnknownFallbacks() {
    ManagerAndParser fixture = defaultManager();
    Frame frame = new Frame();
    Element element = new Element("div");
    frame.addChild(element);
    frame
        .styleSheets()
        .add(
            fixture.parser.parse(
                "div:hover::-webkit-scrollbar-thumb { background-color: red; }"));
    fixture.manager.recalculate(frame);

    element.hovered(true);
    assertEquals(
        StyleImpact.FULL_UNKNOWN,
        fixture.manager.recalculate(frame));
    org.junit.jupiter.api.Assertions.assertFalse(element.scrollbarStyles().isEmpty());

    element.hovered(false);
    assertEquals(
        StyleImpact.FULL_UNKNOWN,
        fixture.manager.recalculate(frame));
    org.junit.jupiter.api.Assertions.assertTrue(element.scrollbarStyles().isEmpty());
  }

  private static StyleImpact hoverImpact(String css, ManagerAndParser fixture) {
    Frame frame = new Frame();
    Element element = new Element("div");
    frame.addChild(element);
    frame.styleSheets().add(fixture.parser.parse(css));
    fixture.manager.recalculate(frame);
    element.hovered(true);
    return fixture.manager.recalculate(frame);
  }

  private static ManagerAndParser defaultManager() {
    var store = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(store);
    return new ManagerAndParser(new StyleManagerImpl(store, parser), parser);
  }

  private record ManagerAndParser(StyleManagerImpl manager, StyleSheetParser parser) {}
}
