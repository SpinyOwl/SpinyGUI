package org.spinyowl.spinygui.backend.renderer;

import org.spinyowl.spinygui.backend.context.Context;
import org.spinyowl.spinygui.core.component.Frame;

public interface Renderer {

    void initialize();
    void render(Frame f, Context c);
    void destroy();
}
