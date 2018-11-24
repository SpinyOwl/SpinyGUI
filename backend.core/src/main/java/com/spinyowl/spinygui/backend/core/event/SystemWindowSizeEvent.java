package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SystemWindowSizeEvent implements SystemEvent {

    public final Window window;
    public final int width;
    public final int height;

    public SystemWindowSizeEvent(Window window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("window", window)
                .append("width", width)
                .append("height", height)
                .toString();
    }
}
