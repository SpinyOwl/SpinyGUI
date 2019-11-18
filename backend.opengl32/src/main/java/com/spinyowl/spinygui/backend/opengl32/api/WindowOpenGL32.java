package com.spinyowl.spinygui.backend.opengl32.api;

import com.spinyowl.spinygui.backend.glfwutil.callback.CallbackKeeper;
import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.node.base.Node;

public class WindowOpenGL32 extends Window {
    private long pointer;

    private Monitor monitor;
    private CallbackKeeper keeper;

    public WindowOpenGL32(long pointer, Monitor monitor, CallbackKeeper keeper) {
        this.pointer = pointer;
        this.monitor = monitor;
        this.keeper = keeper;
    }

    @Override
    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public long getPointer() {
        return pointer;
    }

    public CallbackKeeper getKeeper() {
        return keeper;
    }

    public void setKeeper(CallbackKeeper keeper) {
        this.keeper = keeper;
    }

    @Override
    public Node getFocusOwner() {
        return null;
    }
}