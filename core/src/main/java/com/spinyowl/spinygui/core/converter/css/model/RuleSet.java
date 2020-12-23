package com.spinyowl.spinygui.core.converter.css.model;

import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.converter.css.model.selector.Specificity;
import com.spinyowl.spinygui.core.node.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.collections4.list.SetUniqueList;

@Data
public class RuleSet {

  private final @NonNull List<Selector> selectors;
  private final @NonNull List<Declaration<?>> declarations;

  public boolean test(Element element) {
    return selectors.stream().anyMatch(selector -> selector.test(element));
  }

  public Specificity specificity(Element element) {
    return selectors.stream().filter(selector -> selector.test(element)).map(Selector::specificity)
        .reduce(Specificity::max).orElse(null);
  }

  /**
   * Used to search elements in node tree that are correspond to specified rule set.
   *
   * @param elementTree element tree to search elements with rules.
   * @return set of nodes that are correspond to specified rule set.
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
