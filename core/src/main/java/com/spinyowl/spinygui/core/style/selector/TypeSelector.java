package com.spinyowl.spinygui.core.style.selector;

import com.spinyowl.spinygui.core.component.base.Component;

import java.util.StringJoiner;

public class TypeSelector implements StyleSelector {

    private Class<?> type;

    public TypeSelector(Class<?> type) {
        this.type = type;
    }

    @Override
    public boolean test(Component component) {
        return component.getClass().equals(type);
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TypeSelector.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .toString();
    }
}
