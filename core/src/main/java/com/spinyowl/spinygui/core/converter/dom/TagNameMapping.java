package com.spinyowl.spinygui.core.converter.dom;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.converter.dom.annotations.Tag;
import com.spinyowl.spinygui.core.node.base.Node;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

/**
 * Node mapping - contains node to tag mapping for {@link NodeConverter}.
 */
public final class TagNameMapping {
    private static final BiMap<Class<? extends Node>, String> tagMapping = HashBiMap.create();
    private static final BiMap<String, String> tagNameMapping = HashBiMap.create();

    static {
        var scanResult = new ClassGraph().enableAllInfo().scan();

        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(Tag.class.getName())) {
            if (classInfo.extendsSuperclass(Node.class.getName())) {
                Class<Node> clazz = (Class<Node>) classInfo.loadClass();
                var name = clazz.getAnnotation(Tag.class).value();
                if (name.isEmpty()) {
                    name = clazz.getSimpleName().toLowerCase();
                }
                addMapping(clazz, name);
            }
        }

    }
    private TagNameMapping() {
    }

    public static void addMapping(Class<? extends Node> aClass, String tagName) {
        tagMapping.put(aClass, tagName);
        tagNameMapping.put(tagName.toUpperCase(), tagName);
    }

    public static boolean containsKey(Class<? extends Node> componentClass) {
        return tagMapping.containsKey(componentClass);
    }

    public static String get(Class<? extends Node> componentClass) {
        return tagMapping.get(componentClass);
    }

    public static boolean containsTag(String name) {
        return tagNameMapping.containsKey(name.toUpperCase());
    }

    public static Class<? extends Node> getByTag(String tag) {
        return tagMapping.inverse().get(tagNameMapping.get(tag.toUpperCase()));
    }

    public static void removeMapping(Class<? extends Node> componentClass) {
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
