package com.spinyowl.spinygui.core.style.manager;

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
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class StyleManagerImpl implements StyleManager {

  private static final RuleSet EMPTY_RULE_SET =
      new RuleSet(List.of(new ElementSelector("element")), List.of());

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

    element.resolvedStyle().rules(ruleSets);

    element.children().forEach(child -> updateStyles(child, styleSheets));
  }

  private RuleSet elementStyleRuleSet(Element element) {
    StyleData styleData = elementStyleDataMap.computeIfAbsent(element, e -> new StyleData());
    RuleSet ruleSet;
    if (!Objects.equals(styleData.style(), element.style()) || styleData.styleRuleset() == null) {
      try {
        String style = element.style();
        List<Declaration> declarations;
        if (StringUtils.isBlank(style)) {
          declarations = List.of();
        } else {
          declarations = styleSheetParser.parseDeclarations(style);
        }
        ruleSet = new RuleSet(List.of(new ElementSelector("element")), declarations);

      } catch (ParseException e) {
        ruleSet = styleData.styleRuleset == null ? EMPTY_RULE_SET : styleData.styleRuleset;
      }
      styleData.style(element.style());
      styleData.styleRuleset(ruleSet);
    } else {
      ruleSet = styleData.styleRuleset;
    }

    return ruleSet;
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
