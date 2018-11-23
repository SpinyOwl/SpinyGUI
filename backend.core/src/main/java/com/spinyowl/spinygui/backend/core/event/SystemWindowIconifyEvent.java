package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemWindowIconifyEvent implements SystemEvent {

    public final Window window;
    public final boolean iconified;

    public SystemWindowIconifyEvent(Window window, boolean iconified) {
        this.window = window;
        this.iconified = iconified;
    }

}
