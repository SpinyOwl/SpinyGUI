package com.spinyowl.spinygui.core.style.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;

import java.util.Objects;
import java.util.stream.Stream;

public class ClassNameSelector implements StyleSelector {

    private String className;

    public ClassNameSelector(String className) {
        this.className = className;
    }

    @Override
    public boolean test(Element node) {
        var classes = node.getAttribute("class");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassNameSelector that = (ClassNameSelector) o;
        return Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }
}
