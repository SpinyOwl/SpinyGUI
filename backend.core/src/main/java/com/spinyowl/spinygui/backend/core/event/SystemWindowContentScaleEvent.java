package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowContentScaleEvent implements SystemEvent {
    public final long window;
    public final float scaleX;
    public final float scaleY;

    public SystemWindowContentScaleEvent(long window, float scaleX, float scaleY) {
        this.window = window;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("scaleX", scaleX)
                .append("scaleY", scaleY)
                .toString();
    }
}
