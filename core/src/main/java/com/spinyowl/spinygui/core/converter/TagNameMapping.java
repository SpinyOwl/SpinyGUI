package com.spinyowl.spinygui.core.converter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.spinyowl.spinygui.core.component.*;
import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.converter.dom.ComponentMarshaller;

/**
 * Component mapping - contains component to tag mapping for {@link ComponentMarshaller}.
 */
public final class TagNameMapping {
    private static final BiMap<Class<? extends Component>, String> tagMapping = HashBiMap.create();
    private static final BiMap<String, String> tagNameMapping = HashBiMap.create();

    static {
        addMapping(Button.class, "button");
        addMapping(Input.class, "input");
        addMapping(Label.class, "label");
        addMapping(Panel.class, "panel");
        addMapping(Pre.class, "pre");
        addMapping(RadioButton.class, "radioButton");
    }

    private TagNameMapping() {
    }

    public static void addMapping(Class<? extends Component> aClass, String tagName) {
        tagMapping.put(aClass, tagName);
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
