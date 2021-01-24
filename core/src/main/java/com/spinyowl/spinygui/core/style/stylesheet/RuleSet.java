package com.spinyowl.spinygui.core.style.stylesheet;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.collections4.list.SetUniqueList;

/**
 * Combines set of selectors and set of declarations which should be applied for elements accessed
 * by those selectors.
 */
@Data
public class RuleSet {

  private final @NonNull List<Selector> selectors;
  private final @NonNull List<Declaration<?>> declarations;

  /**
   * Used to check if any selector could be used to reach provided element.
   *
   * @param element element to test.
   * @return true if any selector could be used to reach provided element.
   */
  public boolean test(Element element) {
    return selectors.stream().anyMatch(selector -> selector.test(element));
  }

  /**
   * Used to calculate specificity based on selectors in ruleset for provided element.
   *
   * @param element element to calculate specificity.
   * @return specificity for element based on selectors in ruleset.
   */
  public Specificity specificity(Element element) {
    return selectors.stream().filter(selector -> selector.test(element)).map(Selector::specificity)
        .reduce(Specificity::max).orElse(null);
  }

  /**
   * Used to search all elements in element tree that could be reached by selectors in ruleset.
   *
   * @param elementTree element tree to search.
   * @return all elements in element tree that could be reached by selectors in ruleset.
   */
  public List<Element> searchElements(Element elementTree) {
    Objects.requireNonNull(elementTree);
    return inspectElementTree(elementTree, SetUniqueList.setUniqueList(new ArrayList<>()));
  }

  /**
   * Used to inspect node tree using specified ruleSet.
   *
   * @param elementTree element tree to search elements.
   * @param elements    element set to store result.
   * @return provided elements array with additional elements that are correspond to specified rule
   * set.
   */
  private List<Element> inspectElementTree(Element elementTree, List<Element> elements) {
    Objects.requireNonNull(elementTree);
    if (test(elementTree)) {
      elements.add(elementTree);
    }

    elementTree.children().forEach(c -> inspectElementTree(c, elements));
    return elements;
  }
}
