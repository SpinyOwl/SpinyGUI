package com.spinyowl.spinygui.core.converter.dom;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.converter.dom.annotations.Tag;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node mapping - contains node to tag mapping for {@link NodeConverter}.
 */
public final class TagNameMapping {
    private static final Logger                                  LOGGER     = LoggerFactory.getLogger(TagNameMapping.class);
    private static final BiMap<String, Class<? extends Element>> tagMapping = HashBiMap.create();

    static {
        var scanResult = new ClassGraph()
                .whitelistPackages("com.spinyowl.spinygui")
                .enableAllInfo()
                .scan();

        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(Tag.class.getName())) {
            if (classInfo.extendsSuperclass(Element.class.getName())) {
                Class<Element> clazz = (Class<Element>) classInfo.loadClass();
                var name = clazz.getAnnotation(Tag.class).value().toLowerCase();
                if (name.isEmpty()) {
                    name = clazz.getSimpleName().toLowerCase();
                }
                addMapping(name, clazz);
            } else {
                LOGGER.warn("{} class is annotated with {} annotation. " +
                                "Tag annotation is allowed to use with {} children.",
                        classInfo.getName(), Tag.class.getName(), Element.class.getName())
                ;
            }
        }
    }

    private TagNameMapping() {
    }

    public static void addMapping(String tagName, Class<? extends Element> aClass) {
        tagMapping.put(tagName, aClass);
    }

    public static boolean containsElement(Class<? extends Element> componentClass) {
        return tagMapping.containsValue(componentClass);
    }

    public static String getTagName(Class<? extends Element> componentClass) {
        return tagMapping.inverse().get(componentClass);
    }

    public static boolean containsTag(String name) {
        return tagMapping.containsKey(name.toLowerCase());
    }

    public static Class<? extends Element> getElement(String tag) {
        return tagMapping.get(tag.toLowerCase());
    }

    public static void removeMapping(Class<? extends Element> componentClass) {
        tagMapping.inverse().remove(componentClass);
    }

    public static void removeMapping(String tag) {
        tagMapping.remove(tag.toLowerCase());
    }
}
