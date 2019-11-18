package com.spinyowl.spinygui.core.style.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;

import java.util.Objects;

/**
 * Returns true if node could be selected with selector 'first > second'
 */
public class ImmediateChildSelector implements StyleSelector {
    private StyleSelector first;
    private StyleSelector second;

    public ImmediateChildSelector(StyleSelector first, StyleSelector second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean test(Element t) {
        return t.getParent() != null && second.test(t) && first.test(t.getParent());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmediateChildSelector that = (ImmediateChildSelector) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
