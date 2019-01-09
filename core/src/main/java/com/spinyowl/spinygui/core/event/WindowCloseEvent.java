package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.api.Window;

public class WindowCloseEvent extends WindowEvent {

    public WindowCloseEvent(Window target) {
        super(target);
    }

}
