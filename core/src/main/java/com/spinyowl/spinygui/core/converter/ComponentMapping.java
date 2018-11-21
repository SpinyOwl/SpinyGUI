package com.spinyowl.spinygui.core.converter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.spinyowl.spinygui.core.component.*;
import com.spinyowl.spinygui.core.component.base.Component;

/**
 * Component mapping - contains component to tag mapping for {@link ComponentMarshaller}.
 */
public final class ComponentMapping {
    private static final BiMap<Class<? extends Component>, String> tagMapping = HashBiMap.create();
    private static final BiMap<String, String> tagNameMapping = HashBiMap.create();

    static {
        tagMapping.put(Button.class, "button");
        tagMapping.put(Input.class, "input");
        tagMapping.put(Label.class, "label");
        tagMapping.put(Panel.class, "div");
        tagMapping.put(Pre.class, "pre");
        tagMapping.put(RadioButton.class, "radioButton");
    }

    private ComponentMapping() {
    }

    public static void addMapping(Class<? extends Component> aClass, String tagName) {
        tagMapping.put(aClass, "radioButton");
        tagNameMapping.put(tagName.toUpperCase(), tagName);
    }

    public static boolean containsKey(Class<? extends Component> componentClass) {
        return tagMapping.containsKey(componentClass);
    }

    public static String get(Class<? extends Component> componentClass) {
        return tagMapping.get(componentClass);
    }

    public static boolean containsTag(String name) {
        return tagNameMapping.containsKey(name.toUpperCase());
    }

    public static Class<? extends Component> getByTag(String tag) {
        return tagMapping.inverse().get(tagNameMapping.get(tag.toUpperCase()));
    }

    public static void removeMapping(Class<? extends Component> componentClass) {
        if (tagMapping.containsKey(componentClass)) {
            tagNameMapping.remove(tagMapping.remove(componentClass).toUpperCase());
        }
    }

    public static void removeMapping(String tag) {
        if (tagNameMapping.containsKey(tag.toUpperCase())) {
            tagMapping.inverse().remove(tagNameMapping.remove(tag.toUpperCase()));
        }
    }
}
