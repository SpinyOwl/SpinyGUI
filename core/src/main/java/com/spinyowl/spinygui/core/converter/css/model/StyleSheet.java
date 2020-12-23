package com.spinyowl.spinygui.core.converter.css.model;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.node.Element;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StyleSheet {

  private final List<RuleSet> ruleSets;
  private final List<AtRule> atRules;

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
   * Used to search rules in stylesheet that are correspond to specified element and filter out less
   * specific selectors from result.
   *
   * @param element element to search rules.
   * @return set of rules that are correspond to specified element.
   */
  public List<RuleSet> searchSpecificRules(Element element) {
    Objects.requireNonNull(element);
    return ruleSets.stream()
        .map(rs -> rs.selectors().stream()
            .filter(s -> s.test(element))
            .reduce(Selector::mostSpecific)
            .map(s -> new RuleSet(List.of(s), rs.declarations())).orElse(null)
        ).filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
