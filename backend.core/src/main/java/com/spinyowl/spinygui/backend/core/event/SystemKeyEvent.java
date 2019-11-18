package com.spinyowl.spinygui.backend.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemKeyEvent extends SystemEvent {

    public final long window;
    public final int key;
    public final int keyCode;
    public final int action;
    public final int mods;

    public SystemKeyEvent(long window, int key, int keyCode, int action, int mods) {
        this.window = window;
        this.key = key;
        this.keyCode = keyCode;
        this.action = action;
        this.mods = mods;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("key", key)
                .append("keyCode", keyCode)
                .append("action", action)
                .append("mods", mods)
                .toString();
    }
}
