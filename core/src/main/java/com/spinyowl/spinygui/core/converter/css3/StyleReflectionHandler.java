package com.spinyowl.spinygui.core.converter.css3;

import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.converter.css3.annotations.PseudoSelector;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

import java.util.HashMap;
import java.util.Map;

public class StyleReflectionHandler {

    public static Map<String, Class<?>> pseudoSelectors = new HashMap<String, Class<?>>();
    public static Map<String, Class<?>> typeSelectors = new HashMap<String, Class<?>>();

    static boolean initialized = false;


    static {
        var scanResult = new ClassGraph().enableAllInfo().whitelistModules("com.spinyowl.spinygui.core").scan();
        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(PseudoSelector.class.getName())) {
            Class<?> clazz = classInfo.loadClass();
            var name = clazz.getAnnotation(PseudoSelector.class).value();
            if (name.isEmpty())
                name = clazz.getSimpleName().toLowerCase();
            pseudoSelectors.put(name, clazz);
        }
        for (ClassInfo subclass : scanResult.getSubclasses(Component.class.getName())) {
            Class<?> clazz = subclass.loadClass();
            typeSelectors.put(clazz.getSimpleName().toLowerCase(), clazz);
        }
    }

    public static Class<?> getPseudoSelector(String name) {


        return pseudoSelectors.get(name);
    }

    public static Class<?> getTypeSelector(String name) {
        return typeSelectors.get(name);
    }
}
