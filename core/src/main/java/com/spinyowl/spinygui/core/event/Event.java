package com.spinyowl.spinygui.core.event;

public abstract class Event<T extends EventTarget> {
    public final T target;

    public Event(T target) {
        this.target = target;
    }
}
