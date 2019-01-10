package com.spinyowl.spinygui.core.style.selector;

import com.spinyowl.spinygui.core.component.base.Component;

import java.util.List;
import java.util.Objects;

/**
 * Style selector interface.<br>
 * Style selectors are patterns used to select the elements you want to style.
 */
public interface StyleSelector {

    /**
     * Creates new selector that returns true if both selectors are applicable to component.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector and(StyleSelector first, StyleSelector second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        return t -> first.test(t) && second.test(t);
    }

    /**
     * Creates new selector that returns true if one of selectors is applicable to component.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector or(StyleSelector first, StyleSelector second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        return t -> first.test(t) || second.test(t);
    }

    /**
     * Creates new selector that returns true component could be selected with selector 'first > second'.
     * <p>
     * In general first selector should return true for component's parent, and second for component itself.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector immediateChild(StyleSelector first, StyleSelector second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        return t -> t.getParent() != null && second.test(t) && first.test(t.getParent());
    }

    /**
     * Creates new selector that returns true component could be selected with selector 'first second'.
     *
     * <p>
     * In general first selector should return true for any component's ancestor, and second for component itself.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector child(StyleSelector first, StyleSelector second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        return t -> {
            boolean componentTest = second.test(t);
            if (!componentTest) return false;
            Component parent = t.getParent();
            while (parent != null) {
                if (first.test(parent)) return true;
                parent = parent.getParent();
            }
            return false;
        };
    }

    /**
     * Creates new selector that returns true component could be selected with selector 'first + second'.
     *
     * <p>
     * In general first selector should return true for component that placed immediately before tested component, and second for component itself.
     *
     * @param first  first selector.
     * @param second second selector.
     * @return new selector as combination of two provided selectors.
     */
    static StyleSelector immediateNext(StyleSelector first, StyleSelector second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        return t -> {
            boolean secondTest = second.test(t);
            if (!secondTest) return false;

            Component parent = t.getParent();
            if (parent == null) return false;

            List<Component> sibilings = parent.getChildComponents();
            int indexOfComponent = sibilings.indexOf(t);
            if (indexOfComponent != 0) {
                return first.test(sibilings.get(indexOfComponent - 1));
            }
            return false;
        };
    }

    /**
     * Returns true if provided component could be selected using this selector.
     *
     * @param component component to test.
     * @return true if provided component could be selected using this selector.
     */
    boolean test(Component component);
}
