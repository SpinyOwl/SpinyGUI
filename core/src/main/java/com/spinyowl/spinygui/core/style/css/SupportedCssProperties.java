package com.spinyowl.spinygui.core.style.css;

import java.util.*;

public final class SupportedCssProperties {

    //@formatter:off
    public static final String BACKGROUND        = "background";
    public static final String BACKGROUND_COLOR  = "background-color";
    public static final String PADDING     = "padding";
    public static final String MARGIN      = "margin";
    public static final String BORDER      = "border";
    public static final String WIDTH       = "width";
    public static final String HEIGHT      = "height";
    public static final String MIN_WIDTH   = "min-width";
    public static final String MIN_HEIGHT  = "min-height";
    public static final String MAX_WIDTH   = "max-width";
    public static final String MAX_HEIGHT  = "max-height";
    public static final String DISPLAY     = "display";
    public static final String POSITION    = "position";
    public static final String TOP         = "top";
    public static final String BOTTOM      = "bottom";
    public static final String RIGHT       = "right";
    public static final String LEFT        = "left";
    public static final String WHITE_SPACE = "white-space";
    //@formatter:on

    private List<String> supportedProperties = new ArrayList<>();

    private SupportedCssProperties() {
        supportedProperties.add(BACKGROUND);
        supportedProperties.add(PADDING);
        supportedProperties.add(MARGIN);
        supportedProperties.add(BORDER);
        supportedProperties.add(WIDTH);
        supportedProperties.add(HEIGHT);
        supportedProperties.add(MIN_WIDTH);
        supportedProperties.add(MIN_HEIGHT);
        supportedProperties.add(MAX_WIDTH);
        supportedProperties.add(MAX_HEIGHT);
        supportedProperties.add(DISPLAY);
        supportedProperties.add(POSITION);
        supportedProperties.add(TOP);
        supportedProperties.add(BOTTOM);
        supportedProperties.add(RIGHT);
        supportedProperties.add(LEFT);
        supportedProperties.add(WHITE_SPACE);
    }

    public void addProperty(String property) {
        Objects.requireNonNull(property);

        supportedProperties.add(property);
    }

    /**
     * Getter for instance
     */
    public static SupportedCssProperties getInstance() {
        return PropertiesHolder.instance;
    }

    /**
     * Returns unmodifiable list of supported properties.
     *
     * @return unmodifiable list of supported properties.
     */
    public List<String> getSupportedProperties() {
        return Collections.unmodifiableList(supportedProperties);
    }

    /**
     * Instance holder.
     */
    private static class PropertiesHolder {
        private static SupportedCssProperties instance = new SupportedCssProperties();
    }
}
