package com.spinyowl.spinygui.core.converter.css.model.selector;

import static java.util.Objects.requireNonNull;
import com.spinyowl.spinygui.core.converter.css.model.selector.combinator.AdjacentSiblingSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.combinator.AndSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.combinator.ChildSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.combinator.DescendantSelector;
import com.spinyowl.spinygui.core.converter.css.model.selector.combinator.GeneralSiblingSelector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Selectors {

  private Selectors() {
  }

  /**
   * Creates new selector that returns true if both selectors are applicable to node.
   * <br>Example: {@code .first.second}
   *
   * @param first  first selector.
   * @param second second selector.
   * @return new selector as combination of two provided selectors.
   */
  public static Selector and(Selector first, Selector second) {
    return new AndSelector(requireNonNull(first), requireNonNull(second));
  }

  /**
   * Creates new selector that returns true if node could be selected with selector 'first >
   * second'.
   * <p>
   * In general first selector should return true for node's parent, and second for node itself.
   *
   * @param first  first selector.
   * @param second second selector.
   * @return new selector as combination of two provided selectors.
   */
  public static Selector child(Selector first, Selector second) {
    return new ChildSelector(requireNonNull(first), requireNonNull(second));
  }

  /**
   * Creates new selector that returns true if node could be selected with selector 'first second'.
   * <p>
   * In general first selector should return true for any node's ancestor, and second for node
   * itself.
   *
   * @param first  first selector.
   * @param second second selector.
   * @return new selector as combination of two provided selectors.
   */
  public static Selector descendant(Selector first, Selector second) {
    return new DescendantSelector(requireNonNull(first), requireNonNull(second));
  }

  /**
   * Creates new selector that returns true if node could be selected with selector 'first +
   * second'.
   * <p>
   * <p>
   * In general first selector should return true for node that placed immediately before tested
   * node, and second for node itself.
   *
   * @param first  first selector.
   * @param second second selector.
   * @return new selector as combination of two provided selectors.
   */
  public static Selector adjacentSibling(Selector first, Selector second) {
    return new AdjacentSiblingSelector(requireNonNull(first), requireNonNull(second));
  }

  /**
   * Creates new selector that returns true if node could be selected with selector 'first ~
   * second'.
   * <p>
   * <p>
   * In general first selector should return true for node that placed before tested node, and
   * second for node itself.
   *
   * @param first  first selector.
   * @param second second selector.
   * @return new selector as combination of two provided selectors.
   */
  public static Selector generalSibling(Selector first, Selector second) {
    return new GeneralSiblingSelector(requireNonNull(first), requireNonNull(second));
  }

}
