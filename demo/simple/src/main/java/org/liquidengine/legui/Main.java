package org.liquidengine.legui;

import org.liquidengine.legui.core.LeguiCore;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;

/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    public static void main(String[] args) {
        Monitor monitor = LeguiCore.getPrimaryMonitor();
        Window window = LeguiCore.createWindow(800, 600, "Example", true);
        window.setPosition(50,50);
//        window.setVisible(true);
//        window.addCloseEventListener(event -> LeguiCore.destroyWindow(window));
//        window.
    }

}
