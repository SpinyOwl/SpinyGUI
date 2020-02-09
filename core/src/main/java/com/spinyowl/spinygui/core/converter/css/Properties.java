package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.converter.css.property.*;
import com.spinyowl.spinygui.core.converter.css.property.background.BackgroundColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.*;
import com.spinyowl.spinygui.core.converter.css.property.border.radius.*;
import com.spinyowl.spinygui.core.converter.css.property.dimension.*;
import com.spinyowl.spinygui.core.converter.css.property.flex.*;
import com.spinyowl.spinygui.core.converter.css.property.margin.*;
import com.spinyowl.spinygui.core.converter.css.property.padding.*;
import com.spinyowl.spinygui.core.converter.css.property.position.BottomProperty;
import com.spinyowl.spinygui.core.converter.css.property.position.LeftProperty;
import com.spinyowl.spinygui.core.converter.css.property.position.RightProperty;
import com.spinyowl.spinygui.core.converter.css.property.position.TopProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Properties {
    public static final String COLOR = "color";

    //    public static final String BACKGROUND            = "background";
    public static final String BACKGROUND_COLOR = "background-color";
//    public static final String BACKGROUND_IMAGE      = "background-image";
//    public static final String BACKGROUND_POSITION   = "background-position";
//    public static final String BACKGROUND_SIZE       = "background-size";
//    public static final String BACKGROUND_REPEAT     = "background-repeat";
//    public static final String BACKGROUND_ORIGIN     = "background-origin";
//    public static final String BACKGROUND_CLIP       = "background-clip";
//    public static final String BACKGROUND_ATTACHMENT = "background-attachment";

    public static final String BORDER_RADIUS              = "border-radius";
    public static final String BORDER_BOTTOM_LEFT_RADIUS  = "border-bottom-left-radius";
    public static final String BORDER_BOTTOM_RIGHT_RADIUS = "border-bottom-right-radius";
    public static final String BORDER_TOP_LEFT_RADIUS     = "border-top-left-radius";
    public static final String BORDER_TOP_RIGHT_RADIUS    = "border-top-right-radius";

    public static final String PADDING        = "padding";
    public static final String PADDING_TOP    = "padding-top";
    public static final String PADDING_RIGHT  = "padding-right";
    public static final String PADDING_BOTTOM = "padding-bottom";
    public static final String PADDING_LEFT   = "padding-left";

    public static final String MARGIN        = "margin";
    public static final String MARGIN_TOP    = "margin-top";
    public static final String MARGIN_RIGHT  = "margin-right";
    public static final String MARGIN_BOTTOM = "margin-bottom";
    public static final String MARGIN_LEFT   = "margin-left";

    public static final String BORDER       = "border";
    public static final String BORDER_WIDTH = "border-width";
    public static final String BORDER_COLOR = "border-color";
    public static final String BORDER_STYLE = "border-style";

    public static final String BORDER_LEFT   = "border-left";
    public static final String BORDER_RIGHT  = "border-right";
    public static final String BORDER_TOP    = "border-top";
    public static final String BORDER_BOTTOM = "border-bottom";

    public static final String BORDER_LEFT_WIDTH   = "border-left-width";
    public static final String BORDER_RIGHT_WIDTH  = "border-right-width";
    public static final String BORDER_TOP_WIDTH    = "border-top-width";
    public static final String BORDER_BOTTOM_WIDTH = "border-bottom-width";

    public static final String BORDER_LEFT_COLOR   = "border-left-color";
    public static final String BORDER_RIGHT_COLOR  = "border-right-color";
    public static final String BORDER_TOP_COLOR    = "border-top-color";
    public static final String BORDER_BOTTOM_COLOR = "border-bottom-color";

    public static final String BORDER_LEFT_STYLE   = "border-left-style";
    public static final String BORDER_RIGHT_STYLE  = "border-right-style";
    public static final String BORDER_TOP_STYLE    = "border-top-style";
    public static final String BORDER_BOTTOM_STYLE = "border-bottom-style";

    public static final String ALIGN_CONTENT   = "align-content";
    public static final String ALIGN_ITEMS     = "align-items";
    public static final String ALIGN_SELF      = "align-self";
    public static final String FLEX_BASIS      = "flex-basis";
    public static final String FLEX_DIRECTION  = "flex-direction";
    public static final String FLEX_GROW       = "flex-grow";
    public static final String FLEX_SHRINK     = "flex-shrink";
    public static final String FLEX_WRAP       = "flex-wrap";
    public static final String JUSTIFY_CONTENT = "justify-content";

    public static final String WIDTH  = "width";
    public static final String HEIGHT = "height";

    public static final String MIN_WIDTH  = "min-width";
    public static final String MIN_HEIGHT = "min-height";

    public static final String MAX_WIDTH  = "max-width";
    public static final String MAX_HEIGHT = "max-height";

    public static final String DISPLAY  = "display";
    public static final String POSITION = "position";

    public static final String TOP    = "top";
    public static final String BOTTOM = "bottom";
    public static final String RIGHT  = "right";
    public static final String LEFT   = "left";

    public static final String WHITE_SPACE = "white-space";

    private List<String> supportedProperties = new ArrayList<>();

    private Map<String, Supplier<Property>> propertySupplierMap = new ConcurrentHashMap<>();

    /**
     * Hidden constructor.
     */
    private Properties() {
        addSupportedProperty(COLOR, ColorProperty::new);
        addSupportedProperty(BACKGROUND_COLOR, BackgroundColorProperty::new);

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

        addSupportedProperty(DISPLAY, DisplayProperty::new);
        addSupportedProperty(POSITION, PositionProperty::new);

        addSupportedProperty(WHITE_SPACE, WhiteSpaceProperty::new);

        addSupportedProperty(BORDER_WIDTH, BorderWidthProperty::new);
        addSupportedProperty(BORDER_LEFT_WIDTH, BorderLeftWidthProperty::new);
        addSupportedProperty(BORDER_TOP_WIDTH, BorderTopWidthProperty::new);
        addSupportedProperty(BORDER_BOTTOM_WIDTH, BorderBottomWidthProperty::new);
        addSupportedProperty(BORDER_RIGHT_WIDTH, BorderRightWidthProperty::new);

        addSupportedProperty(BORDER_COLOR, BorderColorProperty::new);
        addSupportedProperty(BORDER_LEFT_COLOR, BorderLeftColorProperty::new);
        addSupportedProperty(BORDER_TOP_COLOR, BorderTopColorProperty::new);
        addSupportedProperty(BORDER_BOTTOM_COLOR, BorderBottomColorProperty::new);
        addSupportedProperty(BORDER_RIGHT_COLOR, BorderRightColorProperty::new);

        addSupportedProperty(BORDER_STYLE, BorderStyleProperty::new);
        addSupportedProperty(BORDER_LEFT_STYLE, BorderLeftStyleProperty::new);
        addSupportedProperty(BORDER_TOP_STYLE, BorderTopStyleProperty::new);
        addSupportedProperty(BORDER_BOTTOM_STYLE, BorderBottomStyleProperty::new);
        addSupportedProperty(BORDER_RIGHT_STYLE, BorderRightStyleProperty::new);

        addSupportedProperty(BORDER, BorderProperty::new);
        addSupportedProperty(BORDER_LEFT, BorderLeftProperty::new);
        addSupportedProperty(BORDER_RIGHT, BorderRightProperty::new);
        addSupportedProperty(BORDER_TOP, BorderTopProperty::new);
        addSupportedProperty(BORDER_BOTTOM, BorderBottomProperty::new);

        addSupportedProperty(BORDER_RADIUS, BorderRadiusProperty::new);
        addSupportedProperty(BORDER_BOTTOM_LEFT_RADIUS, BorderBottomLeftRadiusProperty::new);
        addSupportedProperty(BORDER_BOTTOM_RIGHT_RADIUS, BorderBottomRightRadiusProperty::new);
        addSupportedProperty(BORDER_TOP_LEFT_RADIUS, BorderTopLeftRadiusProperty::new);
        addSupportedProperty(BORDER_TOP_RIGHT_RADIUS, BorderTopRightRadiusProperty::new);

        addSupportedProperty(ALIGN_CONTENT, AlignContentProperty::new);
        addSupportedProperty(ALIGN_ITEMS, AlignItemsProperty::new);
        addSupportedProperty(ALIGN_SELF, AlignSelfProperty::new);
        addSupportedProperty(FLEX_BASIS, FlexBasisProperty::new);
        addSupportedProperty(FLEX_DIRECTION, FlexDirectionProperty::new);
        addSupportedProperty(FLEX_WRAP, FlexWrapProperty::new);
        addSupportedProperty(JUSTIFY_CONTENT, JustifyContentProperty::new);
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
        property.setValue(propertyValue);
        return property;
    }

    /**
     * Used to add property support.
     *
     * @param property         property to support.
     * @param propertySupplier property supplier which will be used to create new {@link Property} instance.
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
