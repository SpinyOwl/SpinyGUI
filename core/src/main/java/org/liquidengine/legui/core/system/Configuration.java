package org.liquidengine.legui.core.system;

public final class Configuration<T> {

    private String property;
    private T state;

    private Configuration() {
    }

    public T get() {
        return state;
    }

    public T get(T defaultValue) {
        T state = this.state;
        if (state == null) {
            state = defaultValue;
        }

        return state;
    }
}
