package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowIconifyEvent implements SystemEvent {

    public final Window window;
    public final boolean iconified;

    public SystemWindowIconifyEvent(Window window, boolean iconified) {
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
