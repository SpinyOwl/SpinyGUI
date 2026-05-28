package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.ParseException;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.AllSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class StyleManagerImpl implements StyleManager {

  private static final int INLINE_STYLE_RULESET_CACHE_SIZE = 1024;
  private static final List<Selector> INLINE_STYLE_SELECTORS =
      List.of(new ElementSelector("element"));
  private static final Ruleset EMPTY_RULE_SET =
      new Ruleset(INLINE_STYLE_SELECTORS, List.of());

  @NonNull private final PropertyStore propertyStore;
  @NonNull private final StyleSheetParser styleSheetParser;

  private final Map<Element, StyleData> elementStyleDataMap = new WeakHashMap<>();
  private final Map<String, InlineStyleParseResult> inlineStyleRulesetCache =
      new LinkedHashMap<>(INLINE_STYLE_RULESET_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, InlineStyleParseResult> eldest) {
          return size() > INLINE_STYLE_RULESET_CACHE_SIZE;
        }
      };

  private List<Property> properties;
  private Ruleset defaultRuleset;

  public void recalculate(Frame frame) {
    updateStyles(frame, frame.styleSheets());
    resolveStyles(frame);
  }

  private void resolveStyles(Element element) {
    List<Ruleset> rules = element.resolvedStyle().rules();
    rules.forEach(rs -> rs.declarations().forEach(declaration -> declaration.apply(element)));
    element.children().forEach(this::resolveStyles);
  }

  private void updateStyles(Element element, List<StyleSheet> styleSheets) {
    List<Ruleset> rulesets = new ArrayList<>();
    // Initializing with default rule sets.
    rulesets.add(defaultRuleset());
    // find all rule sets applicable to element.
    for (StyleSheet styleSheet : styleSheets) {
      rulesets.addAll(styleSheet.searchSpecificRules(element));
    }
    // at the end we need to add styles specified in "style" attribute.
    rulesets.add(elementStyleRuleSet(element));

    element.resolvedStyle().rules(rulesets);

    element.children().forEach(child -> updateStyles(child, styleSheets));
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
      return InlineStyleParseResult.failure();
    }
  }

  public Ruleset defaultRuleset() {
    List<Property> propertyStoreProperties = propertyStore.getProperties();
    if (properties == null || !properties.equals(propertyStoreProperties)) {
      properties = List.copyOf(propertyStoreProperties);
      List<Declaration> collect = new ArrayList<>();
      for (Property p : properties) {
        collect.add(new Declaration(p, p.defaultValue()));
      }
      defaultRuleset = new Ruleset(List.of(new AllSelector()), collect);
    }
    return defaultRuleset;
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
}
