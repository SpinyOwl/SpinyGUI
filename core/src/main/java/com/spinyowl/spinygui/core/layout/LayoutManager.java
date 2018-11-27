package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.style.Display;

/**
 * Layout manager. Used to layout component and it's child components.
 */
public abstract class LayoutManager {

    /**
     * Used to register layout for specified display type.
     *
     * @param displayType display type.
     * @param layout layout to register.
     */
    public abstract void registerLayout(Display displayType, Layout layout);

    /**
     * Used to layout frame layers and all of their child components.
     *
     * @param frame frame to lay out.
     */
    public abstract void layout(Window frame);

    /**
     * Used to layout component and all of his child components.
     *
     * @param component component to lay out.
     */
    public abstract void layout(Component component);
}
