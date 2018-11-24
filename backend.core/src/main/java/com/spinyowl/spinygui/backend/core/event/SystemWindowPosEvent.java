package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowPosEvent implements SystemEvent {

    public final Window window;
    public final int posX;
    public final int posY;

    public SystemWindowPosEvent(Window window, int posX, int posY) {
        this.window = window;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("posX", posX)
                .append("posY", posY)
                .toString();
    }
}
