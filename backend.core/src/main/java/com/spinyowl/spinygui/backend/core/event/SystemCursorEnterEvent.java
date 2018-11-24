package com.spinyowl.spinygui.backend.core.event;

import com.spinyowl.spinygui.core.api.Window;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SystemCursorEnterEvent implements SystemEvent {

    public final Window window;
    public final boolean entered;

    public SystemCursorEnterEvent(Window window, boolean entered) {
        this.window = window;
        this.entered = entered;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("window", window)
                .append("entered", entered)
                .toString();
    }
}
