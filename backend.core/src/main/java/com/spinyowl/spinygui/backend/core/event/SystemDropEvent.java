package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemDropEvent implements SystemEvent {

    public final long window;
    public final String[] strings;

    public SystemDropEvent(long window, String[] strings) {
        this.window = window;
        this.strings = strings;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("strings", strings)
                .toString();
    }
}
