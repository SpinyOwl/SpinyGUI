package com.spinyowl.spinygui.core.converter.css.parser;

import com.spinyowl.spinygui.core.converter.css.parser.annotations.PseudoSelector;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import java.util.HashMap;
import java.util.Map;

public final class StyleReflectionHandler {

    private static Map<String, Class<?>> pseudoSelectors = new HashMap<>();

    static {

        var scanResult = new ClassGraph()
            .enableAllInfo()
            /*.whitelistModules("com.spinyowl.spinygui.core")*/
            .scan();

        for (ClassInfo classInfo : scanResult
            .getClassesWithAnnotation(PseudoSelector.class.getName())) {
            Class<?> clazz = classInfo.loadClass();
            var name = clazz.getAnnotation(PseudoSelector.class).value();
            if (name.isEmpty()) {
                name = clazz.getSimpleName().toLowerCase();
            }
            pseudoSelectors.put(name, clazz);
        }

    }

    private StyleReflectionHandler() {
    }

    public static Class<?> getPseudoSelector(String name) {
        return pseudoSelectors.get(name);
    }
}
