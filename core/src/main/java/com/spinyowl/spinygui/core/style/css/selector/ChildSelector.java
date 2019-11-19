package com.spinyowl.spinygui.core.style.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;

import java.util.Objects;

public class ChildSelector implements StyleSelector {
    private StyleSelector first;
    private StyleSelector second;

    public ChildSelector(StyleSelector first, StyleSelector second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean test(Element t) {
        boolean componentTest = second.test(t);
        if (!componentTest) return false;
        Element parent = t.getParent();
        while (parent != null) {
            if (first.test(parent))
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildSelector that = (ChildSelector) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
