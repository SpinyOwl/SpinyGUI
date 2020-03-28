package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;
import java.util.Objects;

/**
 * Style selector interface.<br> Style selectors are patterns used to select the elements you want
 * to style.
 */
public interface StyleSelector {

    /**
     * Creates new selector that returns true if both selectors are applicable to node.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector and(StyleSelector first, StyleSelector second) {
        check(first, second);
        return new AndSelector(first, second);
    }

    /**
     * Creates new selector that returns true if one of selectors is applicable to node.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector or(StyleSelector first, StyleSelector second) {
        check(first, second);
        return new OrSelector(first, second);
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
    static StyleSelector immediateChild(StyleSelector first, StyleSelector second) {
        check(first, second);
        return new ImmediateChildSelector(first, second);
    }

    /**
     * Creates new selector that returns true if node could be selected with selector 'first
     * second'.
     * <p>
     * In general first selector should return true for any node's ancestor, and second for node
     * itself.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector child(StyleSelector first, StyleSelector second) {
        check(first, second);
        return new ChildSelector(first, second);
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
    static StyleSelector immediateNext(StyleSelector first, StyleSelector second) {
        check(first, second);
        return new ImmediateNextSelector(first, second);
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
    static StyleSelector generalSibling(StyleSelector first, StyleSelector second) {
        check(first, second);
        return new GeneralSiblingSelector(first, second);
    }

    static void check(StyleSelector first, StyleSelector second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
    }

    /**
     * Returns true if provided node could be selected using this selector.
     *
     * @param element node to test.
     * @return true if provided node could be selected using this selector.
     */
    boolean test(Element element);
}
