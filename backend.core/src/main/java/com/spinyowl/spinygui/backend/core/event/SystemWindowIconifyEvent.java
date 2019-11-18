package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowIconifyEvent extends SystemEvent {

    public final long window;
    public final boolean iconified;

    public SystemWindowIconifyEvent(long window, boolean iconified) {
        this.window = window;
        this.iconified = iconified;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("iconified", iconified)
                .toString();
    }
}
