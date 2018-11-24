package com.spinyowl.spinygui.core.style;

public final class Configuration<T> {

    public static final Configuration<String> WINDOW_SERVICE = new Configuration<>("spinygui.window.service", Initializer.STRING);
    public static final Configuration<String> MONITOR_SERVICE = new Configuration<>("spinygui.monitor.service", Initializer.STRING);
    public static final Configuration<String> SERVICE_PROVIDER = new Configuration<>("spinygui.service.porvider", Initializer.STRING);

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

    protected interface Initializer<T> {

        T init(String property);

        Initializer<Boolean> BOOLEAN = property -> {
            String value = System.getProperty(property);
            return value == null ? null : Boolean.parseBoolean(value);
        };

        Initializer<Integer> INT = Integer::getInteger;

        Initializer<String> STRING = System::getProperty;
    }
}
