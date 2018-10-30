package org.liquidengine.legui.backend.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemWindowPosEvent implements SystemEvent {

    public final long window;
    public final int posX;
    public final int posY;

    public SystemWindowPosEvent(long window, int posX, int posY) {
        this.window = window;
        this.posX = posX;
        this.posY = posY;
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
