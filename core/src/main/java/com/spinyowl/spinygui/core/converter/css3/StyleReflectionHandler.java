package com.spinyowl.spinygui.core.converter.css3;

import com.spinyowl.spinygui.core.converter.css3.annotations.PseudoSelector;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

import java.util.HashMap;
import java.util.Map;

public class StyleReflectionHandler {

    public static Map<String, Class<?>> pseudoSelectors = new HashMap<String, Class<?>>();

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
    }

    public static Class<?> getPseudoSelector(String name) {
        return pseudoSelectors.get(name);
    }
}
