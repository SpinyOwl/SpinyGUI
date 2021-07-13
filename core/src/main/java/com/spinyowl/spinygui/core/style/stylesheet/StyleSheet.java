package com.spinyowl.spinygui.core.style.stylesheet;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;

@Data
public class StyleSheet {

  @NonNull private List<RuleSet> ruleSets;
  @NonNull private List<AtRule> atRules;

  /**
   * Used to search rules in stylesheet that are correspond to specified element.
   *
   * @param element element to search rules.
   * @return set of rules that are correspond to specified element.
   */
  public List<RuleSet> searchRules(Element element) {
    Objects.requireNonNull(element);

    ArrayList<RuleSet> result = new ArrayList<>();
    for (RuleSet ruleSet : ruleSets) {
      if (ruleSet.test(element)) {
        result.add(ruleSet);
      }
    }
    result.sort(Comparator.comparing(o -> o.specificity(element)));
    return result;
  }

  /**
   * Used to get rules in stylesheet that correspond to specified element sorted by specificity.
   *
   * @param element element to search rules.
   * @return set of rules that correspond to specified element.
   */
  public List<RuleSet> searchSpecificRules(Element element) {
    Objects.requireNonNull(element);
    return ruleSets.stream()
        .map(
            rs ->
                rs.selectors().stream()
                    .filter(s -> s.test(element))
                    .reduce(Selector::mostSpecific)
                    .map(s -> new RuleSet(List.of(s), rs.declarations()))
                    .orElse(null))
        .filter(Objects::nonNull)
        .sorted(Comparator.comparing(r -> r.specificity(element)))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    var rulesetJoiner = new StringJoiner("\n\n");
    for (RuleSet ruleSet : ruleSets) {
      rulesetJoiner.add(ruleSet.toString());
    }

    return rulesetJoiner.toString();
  }
}
