package org.spinyowl.spinygui.core;

public final class Configuration<T> {

    public static final Configuration<String> BACKEND = new Configuration<>("spinygui.backend", Initializer.STRING);

    private String property;
    private T state;

    private Configuration(String property, Initializer<T> initializer) {
        this.property = property;
        this.state = initializer.init(property);
    }

    public String getProperty() {
        return property;
    }

    public void setState(T state) {
        this.state = state;
    }

    public T getState() {
        return state;
    }

    public T getState(T defaultValue) {
        T state = this.state;
        if (state == null) {
            state = defaultValue;
        }

        return state;
    }

    private interface Initializer<T> {

        T init(String property);

        Initializer<Boolean> BOOLEAN = property -> {
            String value = System.getProperty(property);
            return value == null ? null : Boolean.parseBoolean(value);
        };

        Initializer<Integer> INT = Integer::getInteger;

        Initializer<String> STRING = System::getProperty;
    }
}
