package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemMouseClickEvent extends SystemEvent {

    public final long window;
    public final int button;
    public final int action;
    public final int mods;

    public SystemMouseClickEvent(long window, int button, int action, int mods) {
        this.window = window;
        this.button = button;
        this.action = action;
        this.mods = mods;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("button", button)
                .append("action", action)
                .append("mods", mods)
                .toString();
    }
}
