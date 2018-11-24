package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemCursorPosEvent implements SystemEvent {

    public final Window window;
    public final double posX;
    public final double posY;
    public final float fx;
    public final float fy;

    public SystemCursorPosEvent(Window window, double posX, double posY) {
        this.window = window;
        this.posX = posX;
        this.posY = posY;
        fx = (float) posX;
        fy = (float) posY;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("posX", posX)
                .append("posY", posY)
                .toString();
    }

}
