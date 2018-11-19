package com.spinyowl.spinygui.core.service;

import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;

public interface WindowService {

    Window createWindow(int width, int height, String title);

    Window createWindow(int width, int height, String title, Monitor monitor);

}
