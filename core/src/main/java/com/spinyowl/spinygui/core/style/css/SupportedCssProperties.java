package com.spinyowl.spinygui.core.style.css;

public final class SupportedCssProperties {
private SupportedCssProperties() {
    }

    /**
     * Getter for instance
     */
    public static SupportedCssProperties getInstance() {
        return PropertiesHolder.instance;
    }
/**
     * Instance holder.
     */
    private static class PropertiesHolder {
        private static SupportedCssProperties instance = new SupportedCssProperties();
    }
}
