package org.spinyowl.spinygui.backend.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by Shcherbin Alexander on 6/10/2016.
 */
public class SystemDropEvent implements SystemEvent {

    public final long window;
    public final String[] strings;

    public SystemDropEvent(long window, String[] strings) {
        this.window = window;
        this.strings = strings;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("strings", strings)
                .toString();
    }
}
