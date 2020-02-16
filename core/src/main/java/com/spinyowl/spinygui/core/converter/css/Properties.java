package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.converter.css.property.ColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.DisplayProperty;
import com.spinyowl.spinygui.core.converter.css.property.PositionProperty;
import com.spinyowl.spinygui.core.converter.css.property.WhiteSpaceProperty;
import com.spinyowl.spinygui.core.converter.css.property.background.BackgroundColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.BorderBottomProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.BorderLeftProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.BorderProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.BorderRightProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.BorderTopProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.color.BorderBottomColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.color.BorderLeftColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.color.BorderRightColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.color.BorderTopColorProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.radius.BorderBottomLeftRadiusProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.radius.BorderBottomRightRadiusProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.radius.BorderRadiusProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.radius.BorderTopLeftRadiusProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.radius.BorderTopRightRadiusProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.style.BorderBottomStyleProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.style.BorderLeftStyleProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.style.BorderRightStyleProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.style.BorderStyleProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.style.BorderTopStyleProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.width.BorderBottomWidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.width.BorderLeftWidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.width.BorderRightWidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.width.BorderTopWidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.border.width.BorderWidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.dimension.HeightProperty;
import com.spinyowl.spinygui.core.converter.css.property.dimension.MaxHeightProperty;
import com.spinyowl.spinygui.core.converter.css.property.dimension.MaxWidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.dimension.MinHeightProperty;
import com.spinyowl.spinygui.core.converter.css.property.dimension.MinWidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.dimension.WidthProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.AlignContentProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.AlignItemsProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.AlignSelfProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.FlexBasisProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.FlexDirectionProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.FlexGrowProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.FlexShrinkProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.FlexWrapProperty;
import com.spinyowl.spinygui.core.converter.css.property.flex.JustifyContentProperty;
import com.spinyowl.spinygui.core.converter.css.property.margin.MarginBottomProperty;
import com.spinyowl.spinygui.core.converter.css.property.margin.MarginLeftProperty;
import com.spinyowl.spinygui.core.converter.css.property.margin.MarginProperty;
import com.spinyowl.spinygui.core.converter.css.property.margin.MarginRightProperty;
import com.spinyowl.spinygui.core.converter.css.property.margin.MarginTopProperty;
import com.spinyowl.spinygui.core.converter.css.property.padding.PaddingBottomProperty;
import com.spinyowl.spinygui.core.converter.css.property.padding.PaddingLeftProperty;
import com.spinyowl.spinygui.core.converter.css.property.padding.PaddingProperty;
import com.spinyowl.spinygui.core.converter.css.property.padding.PaddingRightProperty;
import com.spinyowl.spinygui.core.converter.css.property.padding.PaddingTopProperty;
import com.spinyowl.spinygui.core.converter.css.property.position.BottomProperty;
import com.spinyowl.spinygui.core.converter.css.property.position.LeftProperty;
import com.spinyowl.spinygui.core.converter.css.property.position.RightProperty;
import com.spinyowl.spinygui.core.converter.css.property.position.TopProperty;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class Properties {

    public static final String COLOR = "color";

    //    public static final String BACKGROUND            = "background";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String BACKGROUND_IMAGE = "background-image";
//    public static final String BACKGROUND_POSITION   = "background-position";
//    public static final String BACKGROUND_SIZE       = "background-size";
//    public static final String BACKGROUND_REPEAT     = "background-repeat";
//    public static final String BACKGROUND_ORIGIN     = "background-origin";
//    public static final String BACKGROUND_CLIP       = "background-clip";
//    public static final String BACKGROUND_ATTACHMENT = "background-attachment";

    public static final String BORDER_RADIUS = "border-radius";
    public static final String BORDER_BOTTOM_LEFT_RADIUS = "border-bottom-left-radius";
    public static final String BORDER_BOTTOM_RIGHT_RADIUS = "border-bottom-right-radius";
    public static final String BORDER_TOP_LEFT_RADIUS = "border-top-left-radius";
    public static final String BORDER_TOP_RIGHT_RADIUS = "border-top-right-radius";

    public static final String FONT_FAMILY = "font-family";
    public static final String FONT_SIZE = "font-size";
    public static final String FONT_STYLE = "font-style";
    public static final String FONT_WEIGHT = "font-weight";

    public static final String PADDING = "padding";
    public static final String PADDING_TOP = "padding-top";
    public static final String PADDING_RIGHT = "padding-right";
    public static final String PADDING_BOTTOM = "padding-bottom";
    public static final String PADDING_LEFT = "padding-left";

    public static final String MARGIN = "margin";
    public static final String MARGIN_TOP = "margin-top";
    public static final String MARGIN_RIGHT = "margin-right";
    public static final String MARGIN_BOTTOM = "margin-bottom";
    public static final String MARGIN_LEFT = "margin-left";

    public static final String BORDER = "border";
    public static final String BORDER_WIDTH = "border-width";
    public static final String BORDER_COLOR = "border-color";
    public static final String BORDER_STYLE = "border-style";

    public static final String BORDER_LEFT = "border-left";
    public static final String BORDER_RIGHT = "border-right";
    public static final String BORDER_TOP = "border-top";
    public static final String BORDER_BOTTOM = "border-bottom";

    public static final String BORDER_LEFT_WIDTH = "border-left-width";
    public static final String BORDER_RIGHT_WIDTH = "border-right-width";
    public static final String BORDER_TOP_WIDTH = "border-top-width";
    public static final String BORDER_BOTTOM_WIDTH = "border-bottom-width";

    public static final String BORDER_LEFT_COLOR = "border-left-color";
    public static final String BORDER_RIGHT_COLOR = "border-right-color";
    public static final String BORDER_TOP_COLOR = "border-top-color";
    public static final String BORDER_BOTTOM_COLOR = "border-bottom-color";

    public static final String BORDER_LEFT_STYLE = "border-left-style";
    public static final String BORDER_RIGHT_STYLE = "border-right-style";
    public static final String BORDER_TOP_STYLE = "border-top-style";
    public static final String BORDER_BOTTOM_STYLE = "border-bottom-style";

    public static final String ALIGN_CONTENT = "align-content";
    public static final String ALIGN_ITEMS = "align-items";
    public static final String ALIGN_SELF = "align-self";
    public static final String FLEX_BASIS = "flex-basis";
    public static final String FLEX_DIRECTION = "flex-direction";
    public static final String FLEX_GROW = "flex-grow";
    public static final String FLEX_SHRINK = "flex-shrink";
    public static final String FLEX_WRAP = "flex-wrap";
    public static final String JUSTIFY_CONTENT = "justify-content";

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    public static final String MIN_WIDTH = "min-width";
    public static final String MIN_HEIGHT = "min-height";

    public static final String MAX_WIDTH = "max-width";
    public static final String MAX_HEIGHT = "max-height";

    public static final String DISPLAY = "display";
    public static final String POSITION = "position";

    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String RIGHT = "right";
    public static final String LEFT = "left";

    public static final String WHITE_SPACE = "white-space";

    private static Map<String, Property> propertyMap = new ConcurrentHashMap<>();

    static {
        addSupportedProperty(COLOR, new ColorProperty());
        addSupportedProperty(BACKGROUND_COLOR, new BackgroundColorProperty());

        addSupportedProperty(PADDING, new PaddingProperty());
        addSupportedProperty(PADDING_TOP, new PaddingTopProperty());
        addSupportedProperty(PADDING_RIGHT, new PaddingRightProperty());
        addSupportedProperty(PADDING_BOTTOM, new PaddingBottomProperty());
        addSupportedProperty(PADDING_LEFT, new PaddingLeftProperty());

        addSupportedProperty(MARGIN, new MarginProperty());
        addSupportedProperty(MARGIN_TOP, new MarginTopProperty());
        addSupportedProperty(MARGIN_RIGHT, new MarginRightProperty());
        addSupportedProperty(MARGIN_BOTTOM, new MarginBottomProperty());
        addSupportedProperty(MARGIN_LEFT, new MarginLeftProperty());

        addSupportedProperty(WIDTH, new WidthProperty());
        addSupportedProperty(HEIGHT, new HeightProperty());

        addSupportedProperty(MIN_WIDTH, new MinWidthProperty());
        addSupportedProperty(MIN_HEIGHT, new MinHeightProperty());

        addSupportedProperty(MAX_WIDTH, new MaxWidthProperty());
        addSupportedProperty(MAX_HEIGHT, new MaxHeightProperty());

        addSupportedProperty(TOP, new TopProperty());
        addSupportedProperty(RIGHT, new RightProperty());
        addSupportedProperty(BOTTOM, new BottomProperty());
        addSupportedProperty(LEFT, new LeftProperty());

        addSupportedProperty(DISPLAY, new DisplayProperty());
        addSupportedProperty(POSITION, new PositionProperty());

        addSupportedProperty(WHITE_SPACE, new WhiteSpaceProperty());

        addSupportedProperty(BORDER_WIDTH, new BorderWidthProperty());
        addSupportedProperty(BORDER_LEFT_WIDTH, new BorderLeftWidthProperty());
        addSupportedProperty(BORDER_TOP_WIDTH, new BorderTopWidthProperty());
        addSupportedProperty(BORDER_BOTTOM_WIDTH, new BorderBottomWidthProperty());
        addSupportedProperty(BORDER_RIGHT_WIDTH, new BorderRightWidthProperty());

        addSupportedProperty(BORDER_COLOR, new BorderColorProperty());
        addSupportedProperty(BORDER_LEFT_COLOR, new BorderLeftColorProperty());
        addSupportedProperty(BORDER_TOP_COLOR, new BorderTopColorProperty());
        addSupportedProperty(BORDER_BOTTOM_COLOR, new BorderBottomColorProperty());
        addSupportedProperty(BORDER_RIGHT_COLOR, new BorderRightColorProperty());

        addSupportedProperty(BORDER_STYLE, new BorderStyleProperty());
        addSupportedProperty(BORDER_LEFT_STYLE, new BorderLeftStyleProperty());
        addSupportedProperty(BORDER_TOP_STYLE, new BorderTopStyleProperty());
        addSupportedProperty(BORDER_BOTTOM_STYLE, new BorderBottomStyleProperty());
        addSupportedProperty(BORDER_RIGHT_STYLE, new BorderRightStyleProperty());

        addSupportedProperty(BORDER, new BorderProperty());
        addSupportedProperty(BORDER_LEFT, new BorderLeftProperty());
        addSupportedProperty(BORDER_RIGHT, new BorderRightProperty());
        addSupportedProperty(BORDER_TOP, new BorderTopProperty());
        addSupportedProperty(BORDER_BOTTOM, new BorderBottomProperty());

        addSupportedProperty(BORDER_RADIUS, new BorderRadiusProperty());
        addSupportedProperty(BORDER_BOTTOM_LEFT_RADIUS, new BorderBottomLeftRadiusProperty());
        addSupportedProperty(BORDER_BOTTOM_RIGHT_RADIUS, new BorderBottomRightRadiusProperty());
        addSupportedProperty(BORDER_TOP_LEFT_RADIUS, new BorderTopLeftRadiusProperty());
        addSupportedProperty(BORDER_TOP_RIGHT_RADIUS, new BorderTopRightRadiusProperty());

        addSupportedProperty(ALIGN_CONTENT, new AlignContentProperty());
        addSupportedProperty(ALIGN_ITEMS, new AlignItemsProperty());
        addSupportedProperty(ALIGN_SELF, new AlignSelfProperty());
        addSupportedProperty(FLEX_BASIS, new FlexBasisProperty());
        addSupportedProperty(FLEX_GROW, new FlexGrowProperty());
        addSupportedProperty(FLEX_DIRECTION, new FlexDirectionProperty());
        addSupportedProperty(FLEX_SHRINK, new FlexShrinkProperty());
        addSupportedProperty(FLEX_WRAP, new FlexWrapProperty());
        addSupportedProperty(JUSTIFY_CONTENT, new JustifyContentProperty());
    }

    public static Property getProperty(String propertyName) {
        return propertyMap.get(propertyName);
    }

    /**
     * Used to add property support.
     *
     * @param name     property to support.
     * @param property property supplier which will be used to create new {@link Property}
     *                 instance.
     */
    public static void addSupportedProperty(String name, Property property) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(property);

        if (!Objects.equals(name, property.getName())) {
            throw new IllegalArgumentException("Name should be the same as the property name.");
        }

        propertyMap.put(name, property);
    }

    public static void removeSupportedProperty(String property) {
        Objects.requireNonNull(property);
        propertyMap.remove(property);
    }

    /**
     * Returns unmodifiable list of supported properties.
     *
     * @return unmodifiable list of supported properties.
     */
    public static List<String> getSupportedProperties() {
        return List.copyOf(propertyMap.keySet());
    }

}
