package org.liquidengine.legui.demo;

import org.liquidengine.legui.core.Legui;
import org.liquidengine.legui.backend.opengl32.LeguiOpenGL32;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;

/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    public static void main(String[] args) {
        LeguiOpenGL32 f;

        Monitor monitor = Legui.getPrimaryMonitor();
        Window window = Legui.createWindow(800, 600, "Example", true);
        window.setPosition(50,50);


//        window.setVisible(true);
//        window.addCloseEventListener(event -> Legui.destroyWindow(window));
//        window.
    }

}
