package com.spinyowl.spinygui.backend.core.renderer;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.system.context.Context;

/**
 * Master renderer interface. Defines behavior of
 */
public interface MasterRenderer {


    /**
     * Defines initialization method which runs once during master renderer life cycle. Second call should do nothing.
     */
    void initialize();

    /**
     * Defines render method for frame element and for all child elements.
     * Should maintain rendering for all elements.
     *
     * @param frame   frame to render.
     * @param context rendering context.
     */
    void render(Frame frame, Context context);

    /**
     * Defines destruction method which runs once during master renderer life cycle. Second call should do nothing.
     */
    void destroy();
}
