package com.spinyowl.spinygui.core.window.service;

public final class Services {

  private static WindowService windowService;
  private static MonitorService monitorService;

  private Services() {
  }

  public static WindowService getWindowService() {
    return windowService;
  }

  public static void setWindowService(WindowService windowService) {
    Services.windowService = windowService;
  }

  public static MonitorService getMonitorService() {
    return monitorService;
  }

  public static void setMonitorService(MonitorService monitorService) {
    Services.monitorService = monitorService;
  }
}
