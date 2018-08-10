package org.liquidengine.legui.backend.opengl30;

import org.liquidengine.legui.backend.Toolkit;
import org.liquidengine.legui.backend.annotation.ToolkitImpl;

@ToolkitImpl
public class OpenGL30Toolkit extends Toolkit {
    static {
        System.out.println("OpenGL30Toolkit");
    }

    protected void load() {
        System.out.println("OpenGL30Toolkit LOADED");
    }
}
