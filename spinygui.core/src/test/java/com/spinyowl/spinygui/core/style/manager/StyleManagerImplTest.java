package com.spinyowl.spinygui.core.style.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
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
