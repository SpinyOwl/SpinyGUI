package com.spinyowl.spinygui.core.converter.css.parser;

import com.spinyowl.spinygui.core.converter.css.parser.annotations.PseudoSelector;
import com.spinyowl.spinygui.core.converter.css.selector.StyleSelector;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to load pseudo selectors.
 */
public final class StyleReflectionHandler {

  private static final Map<String, Class<?>> pseudoSelectors = new HashMap<>();

  static {

    var scanResult = new ClassGraph()
        .enableAllInfo()
        /*.whitelistModules("com.spinyowl.spinygui.core")*/
        .scan();

    ClassInfoList result = scanResult.getClassesWithAnnotation(PseudoSelector.class.getName());
    for (ClassInfo classInfo : result) {
      if (classInfo.extendsSuperclass(StyleSelector.class.getName())) {
        Class<?> clazz = classInfo.loadClass();
        var name = clazz.getAnnotation(PseudoSelector.class).value();
        if (name.isEmpty()) {
          name = clazz.getSimpleName().toLowerCase();
        }
        pseudoSelectors.put(name, clazz);
      }
    }

  }

  private StyleReflectionHandler() {
  }

  public static Class<?> getPseudoSelector(String name) {
    return pseudoSelectors.get(name);
  }
}
