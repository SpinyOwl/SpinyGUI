package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowMaximizeEvent implements SystemEvent {
    public final Window window;
    public final boolean maximized;

    public SystemWindowMaximizeEvent(Window window, boolean maximized) {
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
