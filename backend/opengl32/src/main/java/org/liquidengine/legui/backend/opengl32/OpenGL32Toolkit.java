package org.liquidengine.legui.backend.opengl32;

import org.liquidengine.legui.backend.Toolkit;

public class OpenGL32Toolkit extends Toolkit {
    static {
        System.out.println("OpenGL32Toolkit");
    }
    protected void load() {
        System.out.println("OpenGL32Toolkit LOADED");
    }
}
