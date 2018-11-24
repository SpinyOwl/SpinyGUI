package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemFramebufferSizeEvent implements SystemEvent {

    public final Window window;
    public final int width;
    public final int height;

    public SystemFramebufferSizeEvent(Window window, int width, int height) {

        this.window = window;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("width", width)
                .append("height", height)
                .toString();
    }
}
