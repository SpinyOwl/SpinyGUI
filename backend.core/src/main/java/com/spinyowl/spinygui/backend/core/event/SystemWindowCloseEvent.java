package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowCloseEvent implements SystemEvent {

    public final long window;

    public SystemWindowCloseEvent(long window) {
        this.window = window;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .toString();
    }
}
