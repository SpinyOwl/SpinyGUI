package org.spinyowl.spinygui.demo;

import org.spinyowl.spinygui.core.SpinyGui;
import org.spinyowl.spinygui.backend.opengl32.SpinyGuiOpenGL32;
import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.Window;

/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    public static void main(String[] args) {
        SpinyGuiOpenGL32 f;

        Monitor monitor = SpinyGui.getPrimaryMonitor();
        Window window = SpinyGui.createWindow(800, 600, "Example", null);
        window.setPosition(50,50);

//        org.spinyowl.spinygui.core.system.renderer.
//        window.setVisible(true);
//        window.addCloseEventListener(event -> SpinyGui.destroyWindow(window));
//        window.
    }

}
