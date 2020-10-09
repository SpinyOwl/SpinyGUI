package com.spinyowl.spinygui.core.window;

import com.spinyowl.spinygui.core.window.service.Services;
import java.util.List;

public interface Monitor {

  static Monitor getPrimaryMonitor() {
    return Services.getMonitorService().getPrimaryMonitor();
  }

  static List<Monitor> getMonitors() {
    return Services.getMonitorService().getMonitors();
  }

  VideoMode getVideoMode();

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
