package com.spinyowl.spinygui.core.converter.css3;

import java.util.Objects;
import java.util.StringJoiner;

public class CSSProperty {

    private String name;
    private Object value;

    public CSSProperty(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSSProperty that = (CSSProperty) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CSSProperty.class.getSimpleName() + "(", ")")
                .add("name='" + name + "'")
                .add("value=" + value)
                .toString();
    }



}
