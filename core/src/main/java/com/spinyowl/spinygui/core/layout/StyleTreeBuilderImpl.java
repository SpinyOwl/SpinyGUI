package com.spinyowl.spinygui.core.layout;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.pseudo.PseudoElement;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.ParseException;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.Properties;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.AllSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ElementSelector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class StyleTreeBuilderImpl implements StyleTreeBuilder {

  @NonNull private final PropertyStore propertyStore;
  @NonNull private final StyleSheetParser styleSheetParser;

  private final Map<Element, StyleData> elementStyleDataMap = new IdentityHashMap<>();

  private List<Property> properties;
  private Ruleset defaultRuleset;

  @Override
  public StyledNode build(Element element, List<StyleSheet> styleSheets) {
    List<Ruleset> rules = newRules();
    Map<String, List<Ruleset>> pseudoElementRules = new HashMap<>();

    if (!(element instanceof PseudoElement)) {
      collectElementAndPseudoElementRules(element, styleSheets, rules, pseudoElementRules);

      // apply rules to element.
      ResolvedStyle resolvedStyle = element.resolvedStyle();
      setRules(resolvedStyle, rules);
    }

    List<StyledNode> children = new ArrayList<>();
    StyledNode styledNode = new StyledNode(element, rules, pseudoElementRules, children);
    element.childNodes().forEach(child -> children.add(buildNode(child, styleSheets)));

    if (!(element instanceof PseudoElement)) {
      pseudoElementRules.forEach(
          (pseudoElement, pseudoElementRulesets) -> {
            ResolvedStyle pseudoElementResolvedStyle = element.resolvedStyle(pseudoElement);
            setRules(pseudoElementResolvedStyle, pseudoElementRulesets);
            if ("::before".equals(pseudoElement) || "::after".equals(pseudoElement)) {
              pseudoElementResolvedStyle.styles().put("content", getContent(pseudoElementRulesets));
            }
          });
    }

    return styledNode;
  }

  private void collectElementAndPseudoElementRules(
      Element element,
      List<StyleSheet> styleSheets,
      List<Ruleset> rules,
      Map<String, List<Ruleset>> pseudoElementRules) {
    for (StyleSheet styleSheet : styleSheets) {
      List<Ruleset> specificRules = styleSheet.searchSpecificRules(element);
      for (Ruleset specificRule : specificRules) {
        if (specificRule.appliesToElement()) {
          rules.add(specificRule);
        }
        if (specificRule.appliesToPseudoElement()) {
          specificRule
              .pseudoElementSelectors()
              .forEach(
                  pseudoElementSelector ->
                      pseudoElementRules
                          .computeIfAbsent(pseudoElementSelector, k -> newRules())
                          .add(specificRule));
        }
      }
    }

    rules.add(elementStyleRuleSet(element));
  }

  private String getContent(List<Ruleset> pseudoElementRulesets) {
    return pseudoElementRulesets.stream()
        .map(Ruleset::declarations)
        .flatMap(List::stream)
        .filter(declaration -> Properties.CONTENT.equals(declaration.property().name()))
        .map(Declaration::term)
        .map(term -> term.value().toString())
        .filter(StringUtils::isNotBlank)
        .findFirst()
        .orElse("");
  }

  private static void setRules(ResolvedStyle resolvedStyle, List<Ruleset> rules) {
    if (!Objects.equals(resolvedStyle.rules(), rules)) {
      resolvedStyle.rules(rules);
    }
  }

  private StyledNode buildNode(Node node, List<StyleSheet> styleSheets) {
    if (node instanceof Element element) {
      return build(element, styleSheets);
    }
    return new StyledNode(node);
  }

  private List<Ruleset> newRules() {
    return new ArrayList<>(List.of(defaultRuleset()));
  }

  private Ruleset elementStyleRuleSet(Element element) {
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

  private static Ruleset createElementRuleSet(List<Declaration> declarations) {
    return new Ruleset(List.of(new ElementSelector("element.style")), declarations);
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
