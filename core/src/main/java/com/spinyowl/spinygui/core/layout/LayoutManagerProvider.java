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
        return LMH.INSTANCE;
    }

    public static void setInstance(LayoutManager instance) {
        Objects.requireNonNull(instance);
        LMH.INSTANCE = instance;
    }

    /**
     * Instance holder.
     */
    private static class LMH {
        private static LayoutManager INSTANCE = new LayoutManagerImpl();
    }
}
