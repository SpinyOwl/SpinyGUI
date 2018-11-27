package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowPosEvent implements SystemEvent {

    public final long window;
    public final int posX;
    public final int posY;

    public SystemWindowPosEvent(long window, int posX, int posY) {
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
