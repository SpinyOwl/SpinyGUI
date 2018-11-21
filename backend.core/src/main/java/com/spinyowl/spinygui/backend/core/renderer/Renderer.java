package com.spinyowl.spinygui.backend.core.renderer;

import com.spinyowl.spinygui.backend.core.context.Context;
import com.spinyowl.spinygui.core.component.Frame;

public interface Renderer {

    void initialize();

    void render(Frame f, Context c);

    void destroy();
}
