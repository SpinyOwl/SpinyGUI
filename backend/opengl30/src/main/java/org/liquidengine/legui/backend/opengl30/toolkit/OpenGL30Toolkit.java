package org.liquidengine.legui.backend.opengl30.toolkit;

import org.liquidengine.legui.backend.Toolkit;

public class OpenGL30Toolkit extends Toolkit {

    static {
        System.out.println("OpenGL30Toolkit");
    }

    protected void load() {
        System.out.println("OpenGL30Toolkit LOADED");
    }
}
