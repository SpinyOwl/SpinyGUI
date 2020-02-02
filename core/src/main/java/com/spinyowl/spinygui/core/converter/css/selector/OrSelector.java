package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;

import java.util.Objects;

public class OrSelector implements StyleSelector {
    private StyleSelector first;
    private StyleSelector second;

    public OrSelector(StyleSelector first, StyleSelector second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean test(Element t) {
        return first.test(t) || second.test(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrSelector that = (OrSelector) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
