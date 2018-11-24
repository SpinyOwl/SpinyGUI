package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemScrollEvent implements SystemEvent {

    public final Window window;
    public final double offsetX;
    public final double offsetY;

    public SystemScrollEvent(Window window, double offsetX, double offsetY) {
        this.window = window;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("offsetX", offsetX)
                .append("offsetY", offsetY)
                .toString();
    }
}
