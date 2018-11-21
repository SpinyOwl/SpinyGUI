package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.api.Window;

public class WindowCloseEvent implements Event {
    private final Window window;

    public WindowCloseEvent(Window window) {
        this.window = window;
    }

    public Window getWindow() {
        return window;
    }
}
