package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemCharEvent implements SystemEvent {

    public final Window window;
    public final int codePoint;

    public SystemCharEvent(Window window, int codePoint) {
        this.window = window;
        this.codePoint = codePoint;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("codePoint", codePoint)
                .toString();
    }
}
