package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowContentScaleEvent implements SystemEvent {
    public final Window window;
    public final float scaleX;
    public final float scaleY;

    public SystemWindowContentScaleEvent(Window window, float scaleX, float scaleY) {
        this.window = window;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("scaleX", scaleX)
                .append("scaleY", scaleY)
                .toString();
    }
}
