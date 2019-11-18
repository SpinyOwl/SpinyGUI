package com.spinyowl.spinygui.core.style.manager;

import java.util.Objects;

/**
 * Singleton for storing StyleManager.
 */
public final class StyleManagerProvider {
    private StyleManagerProvider() {
    }

    /**
     * Getter for instance
     */
    public static StyleManager getInstance() {
        return StyleManagerHolder.instance;
    }

    /**
     * Setter for instance
     */
    public static void setInstance(StyleManager instance) {
        Objects.requireNonNull(instance);
        StyleManagerHolder.instance = instance;
    }

    /**
     * Instance holder.
     */
    private static class StyleManagerHolder {
        private static StyleManager instance = new DefaultStyleManger();
    }
}
