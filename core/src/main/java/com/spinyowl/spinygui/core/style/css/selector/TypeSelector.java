package com.spinyowl.spinygui.core.style.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;

import java.util.Objects;
import java.util.StringJoiner;

public class TypeSelector implements StyleSelector {

    private Class<?> type;

    public TypeSelector(Class<?> type) {
        this.type = type;
    }

    @Override
    public boolean test(Element node) {
        return node.getClass().equals(type);
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeSelector that = (TypeSelector) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TypeSelector.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .toString();
    }
}
