package com.spinyowl.spinygui.core.style.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;

import java.util.List;
import java.util.Objects;

public class ImmediateNextSelector implements StyleSelector {
    private StyleSelector first;
    private StyleSelector second;

    public ImmediateNextSelector(StyleSelector first, StyleSelector second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean test(Element t) {
        boolean secondTest = second.test(t);
        if (!secondTest) return false;

        Element parent = t.getParent();
        if (parent == null) return false;

        List<Node> siblings = parent.getChildNodes();
        int indexOfComponent = siblings.indexOf(t);
        if (indexOfComponent != 0) {
            return first.test(siblings.get(indexOfComponent - 1));
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmediateNextSelector that = (ImmediateNextSelector) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
