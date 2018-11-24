package com.spinyowl.spinygui.core.style.selector;

import com.spinyowl.spinygui.core.component.base.Component;

import java.util.stream.Stream;

public class ClassNameSelector implements StyleSelector {

    private String className;

    public ClassNameSelector(String className) {
        this.className = className;
    }

    @Override
    public boolean test(Component component) {

        var classes = component.getAttribute("class");
        if (classes != null) {
            var classList = classes.split(" ");
            return Stream.of(classList).anyMatch((clazz) -> clazz.equals(className));
        }
        return false;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
