package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.converter.annotation.Priority;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Properties {

    public static final String COLOR = "color";

    public static final String BACKGROUND = "background";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String BACKGROUND_IMAGE = "background-image";
    public static final String BACKGROUND_POSITION = "background-position";
    public static final String BACKGROUND_SIZE = "background-size";
    public static final String BACKGROUND_REPEAT = "background-repeat";
    public static final String BACKGROUND_ORIGIN = "background-origin";
    public static final String BACKGROUND_CLIP = "background-clip";
    public static final String BACKGROUND_ATTACHMENT = "background-attachment";

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

        var scanResult = new ClassGraph()
            .whitelistPackages("com.spinyowl.spinygui")
            .enableAllInfo()
            .scan();

        var propertiesToAdd = new HashMap<String, List<CssPropertyTuple>>();

        for (ClassInfo classInfo : scanResult.getSubclasses(Property.class.getName())) {
            Class<Property> clazz = (Class<Property>) classInfo.loadClass();
            Priority annotation = clazz.getAnnotation(Priority.class);
            int priority = 0;
            if (annotation != null) {
                priority = annotation.value();
            }
            Property property = null;
            try {
                property = clazz.getConstructor().newInstance();

            } catch (Exception e) {
                LOGGER.error(
                    "Can't initialize property handler {}. Public default constructor should be declared.",
                    clazz.getName());
            }
            if (property != null) {
                propertiesToAdd.computeIfAbsent(property.getName(), p -> new ArrayList<>()).
                    add(CssPropertyTuple.of(property, priority));
            }
        }

        for (var entry : propertiesToAdd.entrySet()) {
            String name = entry.getKey();
            var value = entry.getValue();
            if (value.size() > 1) {
                value.sort(Comparator.comparingInt(o -> -o.priority));
                LOGGER.warn("Found several tag mappings for tag {} : {}. Using {}", name,
                    Arrays.toString(
                        value.stream().map(c -> c.property.getClass().getName()).toArray()),
                    value.get(0).property.getClass().getName());
            }
            addSupportedProperty(name, value.get(0).property);
        }

    }

    private Properties() {
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

        if (LOGGER.isWarnEnabled() && propertyMap.containsKey(name)) {
            LOGGER.warn(
                "There is already exist property handler for {} : {}. Would be replaced by {}.",
                name, propertyMap.get(name).getClass().getName(), property.getClass().getName());
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

    @AllArgsConstructor(staticName = "of")
    private static class CssPropertyTuple {

        private final Property property;
        private final int priority;
    }
}
