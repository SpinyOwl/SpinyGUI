package com.spinyowl.spinygui.core.style.stylesheet;

import com.spinyowl.spinygui.core.node.Element;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;

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
  public List<RuleSet> searchRules(@NonNull Element element) {
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
  public List<RuleSet> searchSpecificRules(@NonNull Element element) {
    return ruleSets.stream()
        .map(rs -> Pair.of(rs, rs.specificity(element)))
        .filter(pair -> pair.getRight() != null)
        .sorted(Comparator.comparing(Pair::getRight))
        .map(Pair::getLeft)
        .toList();
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
