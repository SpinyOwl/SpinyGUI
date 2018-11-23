package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemWindowSizeEvent implements SystemEvent {

    public final Window window;
    public final int width;
    public final int height;

    public SystemWindowSizeEvent(Window window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;
    }

}
