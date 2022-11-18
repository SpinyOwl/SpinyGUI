package com.spinyowl.spinygui.core.style.manager;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.ParseException;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.RuleSet;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.AllSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StyleManagerImpl implements StyleManager {

  @NonNull private final PropertyStore propertyStore;
  @NonNull private final StyleSheetParser styleSheetParser;

  private final Map<Element, StyleData> elementStyleDataMap = new IdentityHashMap<>();

  private List<Property> properties;
  private RuleSet defaultRuleSet;

  public void recalculate(Frame frame) {
    updateStyles(frame, frame.styleSheets());
  }

  private void updateStyles(Element element, List<StyleSheet> styleSheets) {
    List<RuleSet> ruleSets = new ArrayList<>();
    // Initializing with default rule sets.
    ruleSets.add(defaultRuleSet());
    // find all rule sets applicable to element.
    for (StyleSheet styleSheet : styleSheets) {
      ruleSets.addAll(styleSheet.searchSpecificRules(element));
    }
    // at the end we need to add styles specified in "style" attribute.
    ruleSets.add(elementStyleRuleSet(element));

    if (!Objects.equals(element.resolvedStyle().rules(), ruleSets)) {
      element.resolvedStyle().rules(ruleSets);
      ruleSets.forEach(
          ruleSet -> ruleSet.declarations().forEach(declaration -> declaration.apply(element)));
    }

    element.children().forEach(child -> updateStyles(child, styleSheets));
  }

  private RuleSet elementStyleRuleSet(Element element) {
    // obtain style data from cache or create new one.
    StyleData styleData = elementStyleDataMap.computeIfAbsent(element, e -> new StyleData());
    // check that style is the same as cached and return stylesheet if so.
    if (Objects.equals(styleData.style(), element.style()) && styleData.styleRuleset() != null) {
      return styleData.styleRuleset;
    }

    // parse style attribute.
    try {
      String style = element.style();
      styleData.styleRuleset =
          createElementRuleSet(
              isBlank(style) ? List.of() : styleSheetParser.parseDeclarations(style));
    } catch (ParseException e) {
      if (styleData.styleRuleset == null) {
        styleData.styleRuleset = createElementRuleSet(List.of());
      }
    }
    styleData.style = element.style();
    return styleData.styleRuleset;
  }

  private static RuleSet createElementRuleSet(List<Declaration> declarations) {
    return new RuleSet(List.of(new ElementSelector("element")), declarations);
  }

  public RuleSet defaultRuleSet() {
    List<Property> propertyStoreProperties = propertyStore.getProperties();
    if (properties == null || !properties.equals(propertyStoreProperties)) {
      properties = List.copyOf(propertyStoreProperties);
      List<Declaration> collect = new ArrayList<>();
      for (Property p : properties) {
        collect.add(new Declaration(p, p.defaultValue()));
      }
      defaultRuleSet = new RuleSet(List.of(new AllSelector()), collect);
    }
    return defaultRuleSet;
  }

  @Data
  private static class StyleData {
    private String style;
    private RuleSet styleRuleset;
  }
}
