package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowFocusEvent implements SystemEvent {

    public final long window;
    public final boolean focused;

    public SystemWindowFocusEvent(long window, boolean focused) {
        this.window = window;
        this.focused = focused;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("focused", focused)
                .toString();
    }
}
