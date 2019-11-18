package com.spinyowl.spinygui.core.style.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GeneralSiblingSelector implements StyleSelector {
    private StyleSelector first;
    private StyleSelector second;

    public GeneralSiblingSelector(StyleSelector first, StyleSelector second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean test(Element node) {
        boolean secondTest = second.test(node);
        if (!secondTest) return false;

        Element parent = node.getParent();
        if (parent == null) return false;

        List<Element> siblings = parent.getChildElements();
        int nodeIndex = siblings.indexOf(node);
        if (nodeIndex == 0) return false;

        siblings = siblings.subList(0, nodeIndex);
        Collections.reverse(siblings);

        for (Element sibling : siblings) {
            if (first.test(sibling)) return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralSiblingSelector that = (GeneralSiblingSelector) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
