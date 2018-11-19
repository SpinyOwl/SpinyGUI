package com.spinyowl.spinygui.demo;

import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;

/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    public static void main(String[] args) {

        Monitor monitor = Monitor.getPrimaryMonitor();
        Window window = Window.createWindow(800, 600, "Example", null);
        window.setPosition(50, 50);

    }

}
