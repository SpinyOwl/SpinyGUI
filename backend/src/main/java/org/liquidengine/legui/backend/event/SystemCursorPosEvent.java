package org.liquidengine.legui.backend.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemCursorPosEvent implements SystemEvent {

    public final long window;
    public final double posX;
    public final double posY;
    public final float fx;
    public final float fy;

    public SystemCursorPosEvent(long window, double posX, double posY) {
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
