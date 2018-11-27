package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowSizeEvent implements SystemEvent {

    public final long window;
    public final int width;
    public final int height;

    public SystemWindowSizeEvent(long window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("width", width)
                .append("height", height)
                .toString();
    }
}
