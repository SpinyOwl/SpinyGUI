package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.ParseException;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Properties;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement.ScrollbarSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.AllSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class StyleManagerImpl implements StyleManager {

  private static final int INLINE_STYLE_RULESET_CACHE_SIZE = 1024;
  private static final List<Selector> INLINE_STYLE_SELECTORS =
      List.of(new ElementSelector("element"));
  private static final Ruleset EMPTY_RULE_SET = new Ruleset(INLINE_STYLE_SELECTORS, List.of());

  @NonNull private final PropertyStore propertyStore;
  @NonNull private final StyleSheetParser styleSheetParser;

  private final Map<Element, StyleData> elementStyleDataMap = new WeakHashMap<>();
  private final Map<StyleSheet, StyleSheetRules> styleSheetRulesCache = new WeakHashMap<>();
  private final Map<String, InlineStyleParseResult> inlineStyleRulesetCache =
      new LinkedHashMap<>(INLINE_STYLE_RULESET_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, InlineStyleParseResult> eldest) {
          return size() > INLINE_STYLE_RULESET_CACHE_SIZE;
        }
      };

  private List<Property> properties;
  private Ruleset defaultRuleset;
  private List<Ruleset> userAgentRulesets;

  public void recalculate(Frame frame) {
    updateStyles(frame, frame.styleSheets());
    resolveStyles(frame);
  }

  private void resolveStyles(Element element) {
    applyElementStyle(element);
    applyScrollbarStyles(element);
    element.children().forEach(this::resolveStyles);
  }

  private void applyElementStyle(Element element) {
    element.resolvedStyle().styles().clear();
    element
        .resolvedStyle()
        .rules()
        .forEach(
            ruleSet -> ruleSet.declarations().forEach(declaration -> declaration.apply(element)));
    applyAbsentProperties(element);
  }

  private void applyAbsentProperties(Element element) {
    Map<String, Object> styles = element.resolvedStyle().styles();
    properties().stream()
        .filter(property -> !styles.containsKey(property.name()))
        .forEach(property -> property.computeAbsent(element, styles));
  }

  private void applyScrollbarStyles(Element element) {
    element.scrollbarStyles().values().forEach(style -> applyScrollbarStyle(element, style));
  }

  private void applyScrollbarStyle(Element element, ResolvedStyle style) {
    style
        .rules()
        .forEach(
            ruleSet ->
                ruleSet
                    .declarations()
                    .forEach(
                        declaration -> applyScrollbarDeclaration(element, style, declaration)));
  }

  private void applyScrollbarDeclaration(
      Element element, ResolvedStyle style, Declaration declaration) {
    declaration.property().apply(element, declaration.term(), style);
  }

  private void updateStyles(Element element, List<StyleSheet> styleSheets) {
    List<Ruleset> rulesets = new ArrayList<>();
    Map<ScrollbarPart, List<Ruleset>> scrollbarRulesets = new EnumMap<>(ScrollbarPart.class);
    element.clearScrollbarStyles();
    // Initializing with default rule sets.
    rulesets.add(defaultRuleset());
    rulesets.addAll(searchSpecificElementRules(userAgentRulesets(), element));
    // find all rule sets applicable to element.
    for (StyleSheet styleSheet : styleSheets) {
      StyleSheetRules styleSheetRules = styleSheetRules(styleSheet);
      rulesets.addAll(searchSpecificElementRules(styleSheetRules.elementRules(), element));
      addScrollbarRules(
          scrollbarRulesets,
          searchSpecificScrollbarRules(styleSheetRules.scrollbarRules(), element));
    }
    // at the end we need to add styles specified in "style" attribute.
    rulesets.add(elementStyleRuleSet(element));

    element.resolvedStyle().rules(rulesets);
    scrollbarRulesets.forEach(
        (part, partRulesets) -> {
          List<Ruleset> pseudoRulesets = new ArrayList<>();
          pseudoRulesets.addAll(partRulesets);
          element.getOrCreateScrollbarStyle(part).rules(pseudoRulesets);
        });

    element.children().forEach(child -> updateStyles(child, styleSheets));
  }

  private StyleSheetRules styleSheetRules(StyleSheet styleSheet) {
    return styleSheetRulesCache.computeIfAbsent(styleSheet, this::partitionRules);
  }

  private StyleSheetRules partitionRules(StyleSheet styleSheet) {
    List<Ruleset> elementRules = new ArrayList<>();
    List<ScrollbarRuleset> scrollbarRules = new ArrayList<>();
    for (Ruleset ruleSet : styleSheet.rulesets()) {
      List<Selector> elementSelectors = new ArrayList<>();
      for (Selector selector : ruleSet.selectors()) {
        ScrollbarPart part = scrollbarPart(selector);
        if (part == null) {
          elementSelectors.add(selector);
        } else {
          scrollbarRules.add(
              new ScrollbarRuleset(part, new Ruleset(List.of(selector), ruleSet.declarations())));
        }
      }
      if (!elementSelectors.isEmpty()) {
        elementRules.add(new Ruleset(elementSelectors, ruleSet.declarations()));
      }
    }
    return new StyleSheetRules(List.copyOf(elementRules), List.copyOf(scrollbarRules));
  }

  private List<Ruleset> searchSpecificElementRules(List<Ruleset> rulesets, Element element) {
    return rulesets.stream()
        .map(ruleSet -> matchingRuleSet(ruleSet, element))
        .filter(Objects::nonNull)
        .sorted(Comparator.comparing(ruleSet -> ruleSet.specificity(element)))
        .toList();
  }

  private List<ScrollbarRuleset> searchSpecificScrollbarRules(
      List<ScrollbarRuleset> scrollbarRules, Element element) {
    List<ScrollbarRuleset> rulesets = new ArrayList<>();
    for (ScrollbarRuleset scrollbarRule : scrollbarRules) {
      if (scrollbarRule.ruleset().test(element)) {
        rulesets.add(scrollbarRule);
      }
    }
    rulesets.sort(Comparator.comparing(ruleSet -> ruleSet.ruleset().specificity(element)));
    return rulesets;
  }

  private Ruleset matchingRuleSet(Ruleset ruleSet, Element element) {
    List<Selector> selectors =
        ruleSet.selectors().stream().filter(selector -> selector.test(element)).toList();
    return selectors.isEmpty() ? null : new Ruleset(selectors, ruleSet.declarations());
  }

  private void addScrollbarRules(
      Map<ScrollbarPart, List<Ruleset>> rulesetsByPart, List<ScrollbarRuleset> scrollbarRulesets) {
    for (ScrollbarRuleset scrollbarRuleset : scrollbarRulesets) {
      rulesetsByPart
          .computeIfAbsent(scrollbarRuleset.part(), ignored -> new ArrayList<>())
          .add(scrollbarRuleset.ruleset());
    }
  }

  private ScrollbarPart scrollbarPart(Selector selector) {
    if (selector instanceof ScrollbarSelector scrollbarSelector) {
      return scrollbarSelector.part();
    }
    if (selector instanceof CombinatorSelector combinatorSelector) {
      ScrollbarPart secondPart = scrollbarPart(combinatorSelector.second());
      return secondPart == null ? scrollbarPart(combinatorSelector.first()) : secondPart;
    }
    return null;
  }

  private Ruleset elementStyleRuleSet(Element element) {
    String style = element.style();
    if (StringUtils.isBlank(style)) {
      elementStyleDataMap.remove(element);
      return EMPTY_RULE_SET;
    }

    StyleData styleData = elementStyleDataMap.computeIfAbsent(element, e -> new StyleData());
    Ruleset ruleSet;
    if (!Objects.equals(styleData.style(), style) || styleData.styleRuleset() == null) {
      InlineStyleParseResult parseResult =
          inlineStyleRulesetCache.computeIfAbsent(style, this::parseInlineStyleRuleSet);
      ruleSet =
          parseResult.successful()
              ? parseResult.ruleset()
              : previousOrEmptyInlineRuleSet(styleData);
      styleData.style(style);
      styleData.styleRuleset(ruleSet);
    } else {
      ruleSet = styleData.styleRuleset;
    }

    return ruleSet;
  }

  private Ruleset previousOrEmptyInlineRuleSet(StyleData styleData) {
    return styleData.styleRuleset == null ? EMPTY_RULE_SET : styleData.styleRuleset;
  }

  private InlineStyleParseResult parseInlineStyleRuleSet(String style) {
    try {
      List<Declaration> declarations = styleSheetParser.parseDeclarations(style);
      return InlineStyleParseResult.success(new Ruleset(INLINE_STYLE_SELECTORS, declarations));
    } catch (ParseException e) {
      log.debug("Failed to parse inline style: {}", style, e);
      return InlineStyleParseResult.failure();
    }
  }

  public Ruleset defaultRuleset() {
    if (defaultRuleset == null || propertiesChanged()) {
      properties = List.copyOf(propertyStore.getProperties());
      defaultRuleset = new Ruleset(List.of(new AllSelector()), List.of());
      userAgentRulesets = null;
    }
    return defaultRuleset;
  }

  private List<Property> properties() {
    defaultRuleset();
    return properties;
  }

  private boolean propertiesChanged() {
    return properties == null || !properties.equals(propertyStore.getProperties());
  }

  private List<Ruleset> userAgentRulesets() {
    if (userAgentRulesets == null) {
      Property displayProperty =
          properties.stream()
              .filter(property -> Properties.DISPLAY.equals(property.name()))
              .findFirst()
              .orElse(null);
      if (displayProperty == null) {
        userAgentRulesets = List.of();
      } else {
        Declaration inlineBlock =
            new Declaration(displayProperty, new TermIdent(Display.INLINE_BLOCK.name()));
        userAgentRulesets =
            List.of(
                new Ruleset(List.of(new ElementSelector("input")), List.of(inlineBlock)),
                new Ruleset(List.of(new ElementSelector("button")), List.of(inlineBlock)),
                new Ruleset(List.of(new ElementSelector("textarea")), List.of(inlineBlock)));
      }
    }
    return userAgentRulesets;
  }

  @Data
  private static class StyleData {
    private String style;
    private Ruleset styleRuleset;
  }

  private record InlineStyleParseResult(boolean successful, Ruleset ruleset) {

    private static InlineStyleParseResult success(Ruleset ruleset) {
      return new InlineStyleParseResult(true, ruleset);
    }

    private static InlineStyleParseResult failure() {
      return new InlineStyleParseResult(false, null);
    }
  }

  private record StyleSheetRules(
      List<Ruleset> elementRules, List<ScrollbarRuleset> scrollbarRules) {}

  private record ScrollbarRuleset(ScrollbarPart part, Ruleset ruleset) {}
}
