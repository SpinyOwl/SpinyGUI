package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.ParseException;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StyleManagerImplTest {

  private static final String TEST_PROPERTY_NAME = "test-property";
  private static final Property TEST_PROPERTY =
      Property.builder()
          .name(TEST_PROPERTY_NAME)
          .defaultValue(new TermIdent("default"))
          .updater((term, styles) -> styles.put(TEST_PROPERTY_NAME, term.value()))
          .validator(term -> true)
          .build();

  @Test
  void recalculateParsesSameElementInlineStyleOnlyOnce() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    child.style("border-color: cyan");
    frame.addChild(child);

    styleManager.recalculate(frame);
    styleManager.recalculate(frame);
    styleManager.recalculate(frame);

    assertEquals(1, parser.parseCount("border-color: cyan"));
    assertEquals(1, parser.totalParseCount());
  }

  @Test
  void recalculateKeepsPerElementStyleDataForLiveElements() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    child.style("valid");
    frame.addChild(child);

    styleManager.recalculate(frame);
    styleManager.recalculate(frame);

    Declaration inlineDeclaration = lastRuleSet(child).declarations().getFirst();
    assertEquals("valid", inlineDeclaration.term().value());
    assertEquals(1, parser.parseCount("valid"));
  }

  @Test
  void recalculateSharesParsedRulesetForIdenticalInlineStylesAcrossElements() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element first = new Element("div");
    Element second = new Element("span");
    first.style("border-color: cyan");
    second.style("border-color: cyan");
    frame.addChild(first);
    frame.addChild(second);

    styleManager.recalculate(frame);

    assertEquals(1, parser.parseCount("border-color: cyan"));
    assertEquals(1, parser.totalParseCount());
  }

  @Test
  void recalculateDoesNotParseBlankInlineStyles() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element emptyStyle = new Element("div");
    Element blankStyle = new Element("span");
    blankStyle.style("   ");
    frame.addChild(emptyStyle);
    frame.addChild(blankStyle);

    styleManager.recalculate(frame);
    styleManager.recalculate(frame);

    assertEquals(0, parser.totalParseCount());
  }

  @Test
  void recalculateReusesCachedRulesetWhenInlineStyleChangesBackToKnownValue() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    frame.addChild(child);

    child.style("border-color: cyan");
    styleManager.recalculate(frame);
    child.style("   ");
    styleManager.recalculate(frame);
    child.style("border-color: cyan");
    styleManager.recalculate(frame);

    assertEquals(1, parser.parseCount("border-color: cyan"));
    assertEquals(1, parser.totalParseCount());
  }

  @Test
  void recalculateCachesFailedInlineStyleParses() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    parser.fail("invalid");
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element first = new Element("div");
    Element second = new Element("span");
    first.style("invalid");
    second.style("invalid");
    frame.addChild(first);
    frame.addChild(second);

    styleManager.recalculate(frame);
    styleManager.recalculate(frame);

    assertEquals(1, parser.parseCount("invalid"));
    assertEquals(1, parser.totalParseCount());
  }

  @Test
  void recalculatePreservesPreviousInlineRulesetWhenChangedStyleFailsToParse() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    parser.fail("invalid");
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    frame.addChild(child);

    child.style("valid");
    styleManager.recalculate(frame);
    child.style("invalid");
    styleManager.recalculate(frame);

    Declaration inlineDeclaration = lastRuleSet(child).declarations().getFirst();
    assertEquals("valid", inlineDeclaration.term().value());
    assertEquals(1, parser.parseCount("valid"));
    assertEquals(1, parser.parseCount("invalid"));
  }

  @Test
  void recalculateUsesEmptyRulesetWhenInitialInlineStyleFailsToParse() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    parser.fail("invalid");
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    child.style("invalid");
    frame.addChild(child);

    styleManager.recalculate(frame);

    assertEquals(0, lastRuleSet(child).declarations().size());
    assertEquals(1, parser.parseCount("invalid"));
  }

  @Test
  void recalculateEvictsLeastRecentlyUsedInlineStyleRulesets() {
    CountingStyleSheetParser parser = new CountingStyleSheetParser();
    StyleManager styleManager = new StyleManagerImpl(emptyPropertyStore(), parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    frame.addChild(child);

    child.style("style-0");
    styleManager.recalculate(frame);
    for (int i = 1; i <= 1024; i++) {
      child.style("style-" + i);
      styleManager.recalculate(frame);
    }
    child.style("style-0");
    styleManager.recalculate(frame);

    assertEquals(2, parser.parseCount("style-0"));
  }

  @Test
  void recalculateAppliesSharedInlineRulesetParsedByRealParser() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element first = new Element("div");
    Element second = new Element("span");
    first.style("color: red");
    second.style("color: red");
    frame.addChild(first);
    frame.addChild(second);

    styleManager.recalculate(frame);

    assertEquals(Color.RED, first.resolvedStyle().color());
    assertEquals(Color.RED, second.resolvedStyle().color());
  }

  @Test
  void recalculateAppliesInlineBlockDisplayParsedByRealParser() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    child.style("display: inline-block");
    frame.addChild(child);

    styleManager.recalculate(frame);

    assertEquals(Display.INLINE_BLOCK, child.resolvedStyle().display());
  }

  @Test
  void recalculateAppliesUserAgentInlineBlockDisplayToControls() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element input = NodeBuilder.input();
    Element button = NodeBuilder.button(NodeBuilder.text("Save"));
    Element textarea = NodeBuilder.textarea();
    frame.addChildren(input, button, textarea);

    styleManager.recalculate(frame);

    assertEquals(Display.INLINE_BLOCK, input.resolvedStyle().display());
    assertEquals(Display.INLINE_BLOCK, button.resolvedStyle().display());
    assertEquals(Display.INLINE_BLOCK, textarea.resolvedStyle().display());
  }

  @Test
  void recalculateAuthorStylesOverrideUserAgentInlineBlockDisplay() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element input = NodeBuilder.input();
    frame.addChild(input);
    frame.styleSheets().add(parser.parse("input { display: block; }"));

    styleManager.recalculate(frame);

    assertEquals(Display.BLOCK, input.resolvedStyle().display());
  }

  @Test
  void recalculateAppliesPercentageLengthDeclarations() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    child.setAttribute("class", "fill");
    frame.addChild(child);
    frame.styleSheets().add(parser.parse(".fill { width: 100%; }"));

    styleManager.recalculate(frame);

    assertEquals(Length.percent(1F), child.resolvedStyle().width());
  }

  @Test
  void recalculateAppliesButtonPseudoStateStyles() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element button = NodeBuilder.button(NodeBuilder.text("Save"));
    frame.addChild(button);
    frame
        .styleSheets()
        .add(
            parser.parse(
                """
button {
  background-color: red;
}
button:hover {
  background-color: blue;
}
button:focus {
  background-color: green;
}
button:active {
  background-color: yellow;
}
"""));

    styleManager.recalculate(frame);
    assertEquals(Color.RED, button.resolvedStyle().backgroundColor());

    button.hovered(true);
    styleManager.recalculate(frame);
    assertEquals(Color.BLUE, button.resolvedStyle().backgroundColor());

    button.hovered(false);
    button.focused(true);
    styleManager.recalculate(frame);
    assertEquals(Color.GREEN, button.resolvedStyle().backgroundColor());

    button.focused(false);
    button.pressed(true);
    styleManager.recalculate(frame);
    assertEquals(Color.YELLOW, button.resolvedStyle().backgroundColor());
  }

  @Test
  void recalculateInheritsParentColorWhenChildDoesNotSpecifyColor() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element parent = new Element("div");
    Element child = new Element("span");
    parent.addChild(child);
    frame.addChild(parent);
    frame.styleSheets().add(parser.parse("div { color: red; }"));

    styleManager.recalculate(frame);

    assertEquals(Color.RED, parent.resolvedStyle().color());
    assertEquals(Color.RED, child.resolvedStyle().color());
  }

  @Test
  void recalculateInheritsParentFontWeightWhenChildDoesNotSpecifyFontWeight() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element parent = new Element("div");
    Element child = new Element("span");
    parent.addChild(child);
    frame.addChild(parent);
    frame.styleSheets().add(parser.parse("div { font-weight: bold; }"));

    styleManager.recalculate(frame);

    assertEquals(FontWeight.BOLD, parent.resolvedStyle().fontWeight());
    assertEquals(FontWeight.BOLD, child.resolvedStyle().fontWeight());
  }

  @Test
  void recalculateAppliesExplicitInheritToNonInheritedProperty() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element parent = new Element("div");
    Element child = new Element("span");
    child.style("background-color: inherit");
    parent.addChild(child);
    frame.addChild(parent);
    frame.styleSheets().add(parser.parse("div { background-color: blue; }"));

    styleManager.recalculate(frame);

    assertEquals(Color.BLUE, parent.resolvedStyle().backgroundColor());
    assertEquals(Color.BLUE, child.resolvedStyle().backgroundColor());
  }

  @Test
  void recalculateAppliesInitialInsteadOfInheritedColor() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element parent = new Element("div");
    Element child = new Element("span");
    child.style("color: initial");
    parent.addChild(child);
    frame.addChild(parent);
    frame.styleSheets().add(parser.parse("div { color: red; }"));

    styleManager.recalculate(frame);

    assertEquals(Color.RED, parent.resolvedStyle().color());
    assertEquals(Color.BLACK, child.resolvedStyle().color());
  }

  @Test
  void recalculateClearsComputedValuesWhenInlineStyleIsRemoved() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element child = new Element("div");
    frame.addChild(child);

    child.style("color: red");
    styleManager.recalculate(frame);
    child.style(null);
    styleManager.recalculate(frame);

    assertEquals(Color.BLACK, child.resolvedStyle().color());
  }

  @Test
  void recalculateDoesNotInheritParentDisplayOrPositionByDefault() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element parent = new Element("div");
    Element child = new Element("span");
    parent.addChild(child);
    frame.addChild(parent);
    frame.styleSheets().add(parser.parse("div { display: flex; position: absolute; }"));

    styleManager.recalculate(frame);

    assertEquals(Display.FLEX, parent.resolvedStyle().display());
    assertEquals(Position.ABSOLUTE, parent.resolvedStyle().position());
    assertEquals(Display.BLOCK, child.resolvedStyle().display());
    assertEquals(Position.STATIC, child.resolvedStyle().position());
  }

  @Test
  void recalculateAppliesExplicitInheritToDisplayAndPosition() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new Frame();
    Element parent = new Element("div");
    Element child = new Element("span");
    child.style("display: inherit; position: inherit");
    parent.addChild(child);
    frame.addChild(parent);
    frame.styleSheets().add(parser.parse("div { display: flex; position: absolute; }"));

    styleManager.recalculate(frame);

    assertEquals(Display.FLEX, child.resolvedStyle().display());
    assertEquals(Position.ABSOLUTE, child.resolvedStyle().position());
  }

  private static PropertyStore emptyPropertyStore() {
    return new DefaultPropertyStore();
  }

  private static Ruleset lastRuleSet(Element element) {
    List<Ruleset> rules = element.resolvedStyle().rules();
    return rules.getLast();
  }

  private static final class CountingStyleSheetParser implements StyleSheetParser {
    private final Map<String, Integer> parseCounts = new HashMap<>();
    private final Set<String> failures = new HashSet<>();

    void fail(String style) {
      failures.add(style);
    }

    int parseCount(String style) {
      return parseCounts.getOrDefault(style, 0);
    }

    int totalParseCount() {
      return parseCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public StyleSheet parse(String css) {
      return new StyleSheet(List.of(), List.of());
    }

    @Override
    public List<Declaration> parseDeclarations(String css) {
      parseCounts.merge(css, 1, Integer::sum);
      if (failures.contains(css)) {
        throw new ParseException(css);
      }
      return List.of(new Declaration(TEST_PROPERTY, new TermIdent(css)));
    }

    @Override
    public String toCss(StyleSheet styleSheet) {
      return "";
    }

    @Override
    public String toCss(Ruleset ruleSet) {
      return "";
    }

    @Override
    public String toCss(Declaration declaration) {
      return "";
    }
  }
}
