package com.spinyowl.spinygui.core.event;

public class WindowCloseEvent<T extends EventTarget> extends Event<T> {

    public WindowCloseEvent(T target) {
        super(target);
    }

    public WindowCloseEvent(T target, double timeStamp) {
        super(target, timeStamp);
    }

    public WindowCloseEvent(EventTarget source, T target, double timeStamp) {
        super(source, target, timeStamp);
    }
}
