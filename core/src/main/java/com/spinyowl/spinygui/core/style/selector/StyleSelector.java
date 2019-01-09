package com.spinyowl.spinygui.core.style.selector;

import com.spinyowl.spinygui.core.component.base.Component;

import java.util.List;
import java.util.Objects;

public interface StyleSelector {

    boolean test(Component component);

    default StyleSelector and(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> test(t) && selector.test(t);
    }

    default StyleSelector or(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> test(t) || selector.test(t);
    }

    /**
     * Creates new selector that applies to immediate children.<br><br>
     * Example:<br>
     * This selector is `button`, provided selector is `div` -> new selector is `div > button`
     *
     * @param selector selector to filter child elements.
     * @return new selector that select immediate children (which could be selected by provided selector)of component that could be selected by this selector.
     */
    default StyleSelector immediateChild(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> t.getParent() != null && test(t) && selector.test(t.getParent());
    }

    /**
     * Creates new selector that applies to any children.<br><br>
     * Example:<br>
     * This selector is `button`, provided selector is `div` -> new selector is `div button`.
     *
     * @param selector selector to filter child elements.
     * @return new selector that select immediate children (which could be selected by provided selector)of component that could be selected by this selector.
     */
    default StyleSelector child(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> {
            Component parent = t.getParent();
            boolean parentTest = false;
            while (parent != null) {
                parentTest = selector.test(parent);
                if (parentTest) break;
                parent = parent.getParent();
            }
            return test(t) && parentTest;
        };
    }

    /**
     * Creates new selector that applies to immediate next.<br><br>
     * Example:<br>
     * This selector is `button`, provided selector is `div` -> new selector is `div + button`.
     *
     * @param selector selector to filter child elements.
     * @return new selector that select immediate children (which could be selected by provided selector)of component that could be selected by this selector.
     */
    default StyleSelector immediateNext(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> {
            Component parent = t.getParent();
            if (parent == null) return false;
            List<Component> childComponents = parent.getChildComponents();
            int i = childComponents.indexOf(t);
            boolean appliedToNext = false;
            if (i != 0) {
                appliedToNext = selector.test(childComponents.get(i - 1));
            }
            return test(t) && appliedToNext;
        };
    }
}
