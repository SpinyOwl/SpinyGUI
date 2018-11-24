package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.api.Window;

public class WindowEvent extends Event<Window> {

    public WindowEvent(Window target) {
        super(target);
    }
}
