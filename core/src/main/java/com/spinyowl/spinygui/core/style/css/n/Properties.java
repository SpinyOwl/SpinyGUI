package com.spinyowl.spinygui.core.style.css.n;

import com.spinyowl.spinygui.core.style.css.n.impl.color.ColorProperty;
import com.spinyowl.spinygui.core.style.css.n.impl.color.ColorPropertyBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Properties {
    private Map<String, Class<? extends Property>> propertyMap = new ConcurrentHashMap<>();
    private Map<Class<? extends Property>, PropertyBuilder<? extends Property>> propertyBuilderMap = new ConcurrentHashMap<>();

    private Properties() {
        addProperty("color", ColorProperty.class, new ColorPropertyBuilder());
    }

    /**
     * Getter for instance
     */
    public static Properties getInstance() {
        return PropertiesHolder.INSTANCE;
    }

    public <T extends Property> void addProperty(String propertyName, Class<T> propertyClass, PropertyBuilder<T> builder) {
        propertyMap.put(propertyName, propertyClass);
        propertyBuilderMap.put(propertyClass, builder);
    }

    public <T extends Property> T valueOf(String property, String value) {
        Class<? extends Property> propertyClass = propertyMap.get(property);
        if (propertyClass != null) {
            PropertyBuilder<? extends Property> builder = propertyBuilderMap.get(propertyClass);
            return builder == null ? null : (T) builder.createValue(value);
        } else {
            return null;
        }
    }

    /**
     * Instance holder.
     */
    private static class PropertiesHolder {
        private static final Properties INSTANCE = new Properties();
    }

}
