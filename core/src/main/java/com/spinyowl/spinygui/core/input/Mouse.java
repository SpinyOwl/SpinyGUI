package com.spinyowl.spinygui.core.input;

import com.spinyowl.spinygui.core.input.MouseService.CursorPositions;
import com.spinyowl.spinygui.core.node.Frame;

public class Mouse {

  private static MouseService mouseService = new MouseService.DefaultMouseService();

  public static MouseService mouseService() {
    return mouseService;
  }

  public static void mouseService(MouseService mouseService) {
    Mouse.mouseService = mouseService;
  }

  public static CursorPositions getCursorPositions(Frame frame) {
    return mouseService.getCursorPositions(frame);
  }

  public static boolean pressed(MouseButton button) {
    return mouseService.pressed(button);
  }
}
