package com.spinyowl.spinygui.core.converter.css.model;

import com.spinyowl.spinygui.core.node.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.list.SetUniqueList;

@Getter
@RequiredArgsConstructor
public class StyleSheet {

  private final List<RuleSet> ruleSets;
  private final List<AtRule> atRules;

  /**
   * Used to search elements in node tree that are correspond to specified rule set.
   *
   * @param ruleSet     rule set to search elements.
   * @param elementTree element tree to search elements with rules.
   * @return set of nodes that are correspond to specified rule set.
   */
  public static List<Element> searchElements(RuleSet ruleSet, Element elementTree) {
    Objects.requireNonNull(ruleSet);
    Objects.requireNonNull(elementTree);
    return inspectElementTree(elementTree, ruleSet, SetUniqueList.setUniqueList(new ArrayList<>()));
  }

  /**
   * Used to search rules in stylesheet that are correspond to specified element.
   *
   * @param styleSheet stylesheet to search for rules applicable to specified element.
   * @param element    element to search rules.
   * @return set of rules that are correspond to specified element.
   */
  public static List<RuleSet> searchRules(StyleSheet styleSheet, Element element) {
    Objects.requireNonNull(styleSheet);
    Objects.requireNonNull(element);

    ArrayList<RuleSet> result = new ArrayList<>();
    for (RuleSet ruleSet : styleSheet.ruleSets) {
      if (ruleSet.test(element)) {
        result.add(ruleSet);
      }
    }
    return result;
  }

  /**
   * Used to inspect node tree using specified ruleSet.
   *
   * @param elementTree element tree to search elements.
   * @param ruleSet     ruleSet that used to search nodes.
   * @param elements    element set to store result.
   * @return provided elements array with additional elements that are correspond to specified rule
   * set.
   */
  private static List<Element> inspectElementTree(Element elementTree, RuleSet ruleSet,
      List<Element> elements) {
    Objects.requireNonNull(elementTree);
    Objects.requireNonNull(ruleSet);

    if (ruleSet.test(elementTree)) {
      elements.add(elementTree);
    }

    elementTree.children().forEach(c -> inspectElementTree(c, ruleSet, elements));
    return elements;
  }

}
