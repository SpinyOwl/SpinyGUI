package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.backend.core.event.handler.SystemEventHandlers;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowCloseEvent extends SystemEvent {

    public final long window;

    public SystemWindowCloseEvent(long window) {
        super(SystemEventHandlers.getSystemWindowCloseEventHandler());
        this.window = window;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .toString();
    }
}
