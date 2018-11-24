package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowCloseEvent implements SystemEvent {

    public final Window window;

    public SystemWindowCloseEvent(Window window) {
        this.window = window;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .toString();
    }
}
