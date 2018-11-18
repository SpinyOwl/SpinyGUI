package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.service.MonitorService;

import java.util.List;

public abstract class Monitor {

//    public static Monitor getPrimaryMonitor() {
//        MonitorService.
//    }

    public abstract VideoMode getVideoMode();

    public abstract void setVideoMode(VideoMode videoMode);

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
