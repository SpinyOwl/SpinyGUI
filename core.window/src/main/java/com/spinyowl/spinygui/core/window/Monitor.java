package com.spinyowl.spinygui.core.window;

import com.spinyowl.spinygui.core.window.service.Services;
import java.util.List;

public abstract class Monitor {

    public static Monitor getPrimaryMonitor() {
        return Services.getMonitorService().getPrimaryMonitor();
    }

    public static List<Monitor> getMonitors() {
        return Services.getMonitorService().getMonitors();
    }

    public abstract VideoMode getVideoMode();

    public abstract List<VideoMode> getAvailableVideoModes();

    public abstract long getPointer();

    public abstract String getMonitorName();

    public abstract long getUserPointer();

    public abstract int getPosX();

    public abstract int getPosY();

    public abstract float getScaleX();

    public abstract float getScaleY();

    public abstract int getSizeX();

    public abstract int getSizeY();
}
