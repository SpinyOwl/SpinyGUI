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
  void normalLineHeightRemainsTypedThroughInheritanceAndNumericSetterOverridesIt() {
    var frame = NodeBuilder.frame();
    Element child = NodeBuilder.div();
    frame.addChild(child);
    var store = new com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider()
        .createPropertyStore();
    var parser = com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory.createParser(store);
    var manager = new com.spinyowl.spinygui.core.style.manager.StyleManagerImpl(store, parser);
    frame.style("font-size:30px; line-height:normal");
    child.style("font-size:14px");
    manager.recalculate(frame);
    org.junit.jupiter.api.Assertions.assertTrue(frame.resolvedStyle().normalLineHeight());
    org.junit.jupiter.api.Assertions.assertTrue(child.resolvedStyle().normalLineHeight());
    child.resolvedStyle().lineHeight(1.25f);
    org.junit.jupiter.api.Assertions.assertFalse(child.resolvedStyle().normalLineHeight());
    assertEquals(1.25f, child.resolvedStyle().lineHeight());
  }

  @Test
  void lineHeightKeepsAbsoluteInheritanceAndUnitlessScalingDistinct() {
    var frame = NodeBuilder.frame();
    Element child = NodeBuilder.div();
    frame.addChild(child);
    var store = new com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider()
        .createPropertyStore();
    var parser = com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory.createParser(store);
    var manager = new com.spinyowl.spinygui.core.style.manager.StyleManagerImpl(store, parser);
    frame.style("font-size:16px; line-height:24px");
    child.style("font-size:12px");
    manager.recalculate(frame);
    assertEquals(1.5f, frame.resolvedStyle().lineHeight());
    assertEquals(2f, child.resolvedStyle().lineHeight());
    frame.style("font-size:16px; line-height:1.25");
    manager.recalculate(frame);
    assertEquals(1.25f, child.resolvedStyle().lineHeight());
    frame.style("font-size:16px; line-height:2");
    manager.recalculate(frame);
    assertEquals(2f, child.resolvedStyle().lineHeight());
    child.style("font-size:12px; line-height:18.5px");
    manager.recalculate(frame);
    assertEquals(18.5f, child.resolvedStyle().lineHeight() * 12, 0.0001f);
  }

  @Test
  void numericWeightsResolveInheritAndRejectInvalidUpdates() {
    var frame = NodeBuilder.frame();
    Element child = NodeBuilder.div();
    frame.addChild(child);
    var store = new com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider()
        .createPropertyStore();
    var parser = com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory.createParser(store);
    var manager = new com.spinyowl.spinygui.core.style.manager.StyleManagerImpl(store, parser);
    frame.style("font-weight: 700");
    manager.recalculate(frame);
    assertEquals(700, child.resolvedStyle().fontWeight().weight());
    child.style("font-weight: 800");
    manager.recalculate(frame);
    assertEquals(800, child.resolvedStyle().fontWeight().weight());
    child.style("font-weight: normal");
    manager.recalculate(frame);
    assertEquals(400, child.resolvedStyle().fontWeight().weight());
    Property weight = new FontPropertyProvider().getProperties().stream()
        .filter(p -> p.name().equals("font-weight")).findFirst().orElseThrow();
    for (float invalid : new float[] {0, -1, 1001, Float.NaN, Float.POSITIVE_INFINITY}) {
      weight.apply(child, new com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat(invalid));
      assertEquals(400, child.resolvedStyle().fontWeight().weight());
    }
  }

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
