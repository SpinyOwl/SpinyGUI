package com.spinyowl.spinygui.core.event.listener.impl;

import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.event.WindowCloseEvent;
import com.spinyowl.spinygui.core.event.listener.Listener;

public class DefaultWindowCloseEventListener implements Listener<WindowCloseEvent> {
    @Override
    public void process(WindowCloseEvent event) {
        Window window = event.target;
        if (!window.isClosed()) {
            window.close();
        }
    }
}
