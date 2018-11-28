package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowRefreshEvent extends SystemEvent {

    public final long window;

    public SystemWindowRefreshEvent(long window) {
        this.window = window;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .toString();
    }
}
