package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemWindowFocusEvent implements SystemEvent {

    public final Window window;
    public final boolean focused;

    public SystemWindowFocusEvent(Window window, boolean focused) {
        this.window = window;
        this.focused = focused;
    }

}
