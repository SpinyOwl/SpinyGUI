package com.spinyowl.spinygui.core.style.css.property;

import com.spinyowl.spinygui.core.style.css.SupportedCssProperties;
import com.spinyowl.spinygui.core.style.types.Background;

import java.util.Objects;
import java.util.StringJoiner;

public class Property {

    private String name;
    private PropertyValue value;

    public Property(String name, PropertyValue value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PropertyValue getValue() {
        return value;
    }

    public void setValue(PropertyValue value) {
        this.value = value;
    }


    public boolean isBackground() {
        return SupportedCssProperties.BACKGROUND.equals(name);
    }

    public Background getValueAsBackground() {
        return (Background) value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property that = (Property) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Property.class.getSimpleName() + "(", ")")
                .add("name='" + name + "'")
                .add("value=" + value)
                .toString();
    }


}
