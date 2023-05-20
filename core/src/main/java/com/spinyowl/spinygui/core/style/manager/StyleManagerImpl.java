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

  private static final Ruleset EMPTY_RULE_SET =
      new Ruleset(List.of(new ElementSelector("element")), List.of());

  @NonNull private final PropertyStore propertyStore;
  @NonNull private final StyleSheetParser styleSheetParser;

  private final Map<Element, StyleData> elementStyleDataMap = new IdentityHashMap<>();

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
    StyleData styleData = elementStyleDataMap.computeIfAbsent(element, e -> new StyleData());
    Ruleset ruleSet;
    if (!Objects.equals(styleData.style(), element.style()) || styleData.styleRuleset() == null) {
      try {
        String style = element.style();
        List<Declaration> declarations;
        if (StringUtils.isBlank(style)) {
          declarations = List.of();
        } else {
          declarations = styleSheetParser.parseDeclarations(style);
        }
        ruleSet = new Ruleset(List.of(new ElementSelector("element")), declarations);

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
}
