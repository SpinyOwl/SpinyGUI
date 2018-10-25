package org.liquidengine.legui.backend.renderer;

import org.liquidengine.legui.backend.context.Context;
import org.liquidengine.legui.core.component.Frame;

public interface Renderer {

    void initialize();
    void render(Frame f, Context c);
    void destroy();
}
