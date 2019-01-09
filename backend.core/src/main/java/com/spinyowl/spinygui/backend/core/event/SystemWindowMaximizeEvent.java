package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowMaximizeEvent extends SystemEvent {
    public final long window;
    public final boolean maximized;

    public SystemWindowMaximizeEvent(long window, boolean maximized) {
        this.window = window;
        this.maximized = maximized;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("maximized", maximized)
                .toString();
    }
}
