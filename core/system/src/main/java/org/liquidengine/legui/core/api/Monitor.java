package org.liquidengine.legui.core.api;

import java.util.List;

public interface Monitor {

    VideoMode getVideoMode();

    void setVideoMode(VideoMode videoMode);

    List<VideoMode> getAvailableVideoModes();

}
