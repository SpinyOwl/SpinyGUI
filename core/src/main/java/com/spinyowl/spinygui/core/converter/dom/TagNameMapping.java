package com.spinyowl.spinygui.core.converter.dom;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.converter.annotation.Priority;
import com.spinyowl.spinygui.core.converter.dom.annotations.Tag;
import com.spinyowl.spinygui.core.node.Element;
import io.github.classgraph.ClassGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Node mapping - contains node to tag mapping for {@link NodeConverter}.
 * <p>
 * By default all tags added with 0 priority. To override priority you can use {@link Priority}
 * annotation.
 * <p>
 * If there are several tags with same name found - will be used one of them.
 * <p>
 * Also you can override any property using {@link #addMapping(String, Class)} method.
 */
@Slf4j
public final class TagNameMapping {

    private static final BiMap<String, Class<? extends Element>> tagMapping = HashBiMap.create();

    static {
        var scanResult = new ClassGraph()
            .whitelistPackages("com.spinyowl.spinygui")
            .enableAllInfo()
            .scan();

        var tagsToAdd = new HashMap<String, List<TagToAdd>>();

        for (var classInfo : scanResult.getSubclasses(Element.class.getName())) {
            var clazz = (Class<Element>) classInfo.loadClass();

            Priority annotation = clazz.getAnnotation(Priority.class);

            int priority = 0;
            if (annotation != null) {
                priority = annotation.value();
            }

            Tag tag = clazz.getAnnotation(Tag.class);
            var name = clazz.getSimpleName().toLowerCase();
            if (tag != null) {
                name = tag.value().toLowerCase();
            }

            tagsToAdd.computeIfAbsent(name, n -> new ArrayList<>())
                .add(TagToAdd.of(clazz, priority));
        }

        for (var entry : tagsToAdd.entrySet()) {
            var value = entry.getValue();
            String name = entry.getKey();
            if (value.size() > 1) {
                value.sort(Comparator.comparingInt(o -> -o.priority));
                LOGGER.warn("Found several tag mappings for tag {} : {}. Using {}", name,
                    Arrays.toString(value.stream().map(c -> c.element.getName()).toArray()),
                    value.get(0).element.getName());
            }
            addMapping(name, value.get(0).element);
        }

    }

    private TagNameMapping() {
    }

    public static void addMapping(String name, Class<? extends Element> aClass) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(aClass);

        if (LOGGER.isWarnEnabled() && tagMapping.containsKey(name)) {
            LOGGER.warn(
                "There is already exist tag mapping for {} : {}. Would be replaced by {}.",
                name, tagMapping.get(name).getName(), aClass.getName());
        }

        tagMapping.put(name, aClass);
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

    @AllArgsConstructor(staticName = "of")
    private static class TagToAdd {

        private final Class<? extends Element> element;
        private final int priority;
    }
}
