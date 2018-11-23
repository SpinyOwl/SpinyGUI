package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemWindowCloseEvent implements SystemEvent {

    public final Window window;

    public SystemWindowCloseEvent(Window window) {
        this.window = window;
    }

}
