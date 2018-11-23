package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemWindowPosEvent implements SystemEvent {

    public final Window window;
    public final int posX;
    public final int posY;

    public SystemWindowPosEvent(Window window, int posX, int posY) {
        this.window = window;
        this.posX = posX;
        this.posY = posY;
    }
    
}
