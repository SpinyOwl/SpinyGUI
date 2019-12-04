package com.spinyowl.spinygui.core.style.css;

import com.spinyowl.spinygui.core.style.css.property.*;
import com.spinyowl.spinygui.core.style.css.property.dimension.*;
import com.spinyowl.spinygui.core.style.css.property.margin.*;
import com.spinyowl.spinygui.core.style.css.property.padding.*;
import com.spinyowl.spinygui.core.style.css.property.position.BottomProperty;
import com.spinyowl.spinygui.core.style.css.property.position.LeftProperty;
import com.spinyowl.spinygui.core.style.css.property.position.RightProperty;
import com.spinyowl.spinygui.core.style.css.property.UnsupportedProperty;
import com.spinyowl.spinygui.core.style.css.property.position.TopProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Properties {
    //@formatter:off
    public static final String COLOR          = "color";
    public static final String PADDING        = "padding";
    public static final String PADDING_TOP    = "padding-top";
    public static final String PADDING_RIGHT  = "padding-right";
    public static final String PADDING_BOTTOM = "padding-bottom";
    public static final String PADDING_LEFT   = "padding-left";
    public static final String MARGIN         = "margin";
    public static final String MARGIN_TOP     = "margin-top";
    public static final String MARGIN_RIGHT   = "margin-right";
    public static final String MARGIN_BOTTOM  = "margin-bottom";
    public static final String MARGIN_LEFT    = "margin-left";
    public static final String BORDER         = "border";
    public static final String WIDTH          = "width";
    public static final String HEIGHT         = "height";
    public static final String MIN_WIDTH      = "min-width";
    public static final String MIN_HEIGHT     = "min-height";
    public static final String MAX_WIDTH      = "max-width";
    public static final String MAX_HEIGHT     = "max-height";
    public static final String DISPLAY        = "display";
    public static final String POSITION       = "position";
    public static final String TOP            = "top";
    public static final String BOTTOM         = "bottom";
    public static final String RIGHT          = "right";
    public static final String LEFT           = "left";
    public static final String WHITE_SPACE    = "white-space";
    //@formatter:on
    private List<String> supportedProperties = new ArrayList<>();

    private Map<String, Supplier<Property>> propertySupplierMap = new ConcurrentHashMap<>();

    /**
     * Hidden constructor.
     */
    private Properties() {
        addSupportedProperty(COLOR, ColorProperty::new);

        addSupportedProperty(PADDING, PaddingProperty::new);
        addSupportedProperty(PADDING_TOP, PaddingTopProperty::new);
        addSupportedProperty(PADDING_RIGHT, PaddingRightProperty::new);
        addSupportedProperty(PADDING_BOTTOM, PaddingBottomProperty::new);
        addSupportedProperty(PADDING_LEFT, PaddingLeftProperty::new);

        addSupportedProperty(MARGIN, MarginProperty::new);
        addSupportedProperty(MARGIN_TOP, MarginTopProperty::new);
        addSupportedProperty(MARGIN_RIGHT, MarginRightProperty::new);
        addSupportedProperty(MARGIN_BOTTOM, MarginBottomProperty::new);
        addSupportedProperty(MARGIN_LEFT, MarginLeftProperty::new);

        addSupportedProperty(WIDTH, WidthProperty::new);
        addSupportedProperty(HEIGHT, HeightProperty::new);

        addSupportedProperty(MIN_WIDTH, MinWidthProperty::new);
        addSupportedProperty(MIN_HEIGHT, MinHeightProperty::new);

        addSupportedProperty(MAX_WIDTH, MaxWidthProperty::new);
        addSupportedProperty(MAX_HEIGHT, MaxHeightProperty::new);

        addSupportedProperty(TOP, TopProperty::new);
        addSupportedProperty(RIGHT, RightProperty::new);
        addSupportedProperty(BOTTOM, BottomProperty::new);
        addSupportedProperty(LEFT, LeftProperty::new);

//        BORDER
//        DISPLAY
//        POSITION
//        WHITE_SPACE
    }

    //@formatter:off
    /** Getter for instance. */
    public static Properties getInstance() { return PropertiesHolder.INSTANCE; }
    //@formatter:on

    public Supplier<Property> getPropertySupplier(String propertyName) {
        return propertySupplierMap.get(propertyName);
    }

    /**
     * Used to create {@link Property}.
     * Returns {@link UnsupportedProperty} in next cases:
     * <ul>
     *     <li>property not listed in supported properties ({@link Properties#getSupportedProperties()}</li>
     *     <li>property supplier not exist</li>
     *     <li>property supplier creates null property</li>
     * </ul>
     *
     * @param propertyName  property name which should be used to find appropriate property supplier.
     * @param propertyValue property value which should be used to fill created property.
     * @return new created property instance or instance of {@link UnsupportedProperty} if property not supported.
     */
    public Property createProperty(String propertyName, String propertyValue) {
        Objects.requireNonNull(propertyName);
        Property property;
        if (supportedProperties.contains(propertyName)) {
            Supplier<Property> propertySupplier = propertySupplierMap.get(propertyName);
            if (propertySupplier != null) {
                property = propertySupplier.get();
                if (property == null) {
                    property = new UnsupportedProperty(propertyName);
                }
            } else {
                property = new UnsupportedProperty(propertyName);
            }
        } else {
            property = new UnsupportedProperty(propertyName);
        }
        // todo: check that there is no any side effects on property value to lower case
        property.setValue(propertyValue.toLowerCase());
        return property;
    }

    /**
     * Used to add property support.
     *
     * @param property         property to support.
     * @param propertySupplier property supplier which will be used to create {@link Property} instance
     *                         by property name.
     */
    public void addSupportedProperty(String property, Supplier<Property> propertySupplier) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(propertySupplier);

        supportedProperties.add(property);
        propertySupplierMap.put(property, propertySupplier);
    }

    public void removeSupportedProperty(String property) {
        Objects.requireNonNull(property);
        supportedProperties.remove(property);
        propertySupplierMap.remove(property);
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
        private static final Properties INSTANCE = new Properties();
    }
}
