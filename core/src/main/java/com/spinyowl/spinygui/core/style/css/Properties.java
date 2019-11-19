package com.spinyowl.spinygui.core.style.css;

import com.spinyowl.spinygui.core.style.css.handler.ColorPropertyHandler;
import com.spinyowl.spinygui.core.style.css.property.ColorProperty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class Properties {

    private Map<String, Supplier<Property>> propertyMap = new ConcurrentHashMap<>();
    private Map<String, PropertyHandler> handlerMap = new ConcurrentHashMap<>();

    //@formatter:off
    /** Hidden constructor. */
    private Properties() {
        propertyMap.put("color",ColorProperty::new);
        handlerMap.put("color",new  ColorPropertyHandler());
    }

    /** Getter for instance. */
    public static Properties getInstance() { return PropertiesHolder.INSTANCE; }
    //@formatter:on


    public void addPorpertySupplier(String name, Supplier<Property> supplier) {
        propertyMap.put(name, supplier);
    }

    public Supplier<Property> getPropertySupplier(String name) {
        return propertyMap.get(name);
    }

    public void addPorpertyHandler(String name, PropertyHandler propertyHandler) {
        handlerMap.put(name, propertyHandler);
    }

    public PropertyHandler getPropertyHandler(String name) {
        return handlerMap.get(name);
    }

    public Property createProperty(String name, String value) {
        Supplier<Property> propertySupplier = propertyMap.get(name);
        Property property;
        if (propertySupplier != null) {
            property = propertySupplier.get();
        } else {
            property = new Property(name);
        }
        property.setValue(value);
        property.setPropertyHandler(handlerMap.get(name));
        return property;
    }

    /**
     * Instance holder.
     */
    private static class PropertiesHolder {
        private static final Properties INSTANCE = new Properties();
    }
}
