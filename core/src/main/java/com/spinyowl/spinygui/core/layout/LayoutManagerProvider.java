package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.layout.impl.LayoutManagerImpl;
import java.util.Objects;

/**
 * Layout manager. Used to layout node and it's child components.
 */
public final class LayoutManagerProvider {

    private LayoutManagerProvider() {
    }

    public static LayoutManager getInstance() {
        return LMH.instance;
    }

    public static void setInstance(LayoutManager instance) {
        Objects.requireNonNull(instance);
        LMH.instance = instance;
    }

    /**
     * Instance holder.
     */
    private static class LMH {

        private static LayoutManager instance = new LayoutManagerImpl();
    }
}
