package org.liquidengine.legui.backend.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemCharModsEvent implements SystemEvent {

    public final long window;
    public final int codePoint;
    public final int mods;

    public SystemCharModsEvent(long window, int codePoint, int mods) {
        this.window = window;
        this.codePoint = codePoint;
        this.mods = mods;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("window", window)
            .append("codePoint", codePoint)
            .append("mods", mods)
            .toString();
    }
}
