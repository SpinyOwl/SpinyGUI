package com.spinyowl.spinygui.core.api;

import java.util.List;

public interface Monitor {

    VideoMode getVideoMode();

    void setVideoMode(VideoMode videoMode);

    List<VideoMode> getAvailableVideoModes();

    long getPointer();

    String getMonitorName();

    long getUserPointer();

    int getPosX();

    int getPosY();

    float getScaleX();

    float getScaleY();

    int getSizeX();

    int getSizeY();
}
