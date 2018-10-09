package org.liquidengine.legui.core.system.component;

public class Attribute<T> {
    private T value;
    private Class<T> type;

    public Attribute(T value, Class<T> type) {
        this.value = value;
        this.type = type;
    }

    public Attribute(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
