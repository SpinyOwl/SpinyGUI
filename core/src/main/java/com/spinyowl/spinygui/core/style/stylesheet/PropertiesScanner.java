package com.spinyowl.spinygui.core.style.stylesheet;

import com.spinyowl.spinygui.core.style.stylesheet.annotation.Priority;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesScanner {

  public static void fillPropertyStore(@NonNull PropertyStore propertyStore) {
    var scanResult = new ClassGraph().enableAllInfo().scan();
    var propertiesToAdd = new HashMap<String, List<PropertyPriority>>();

    for (ClassInfo classInfo : scanResult.getSubclasses(Property.class.getName())) {
      @SuppressWarnings("unchecked")
      Class<Property> clazz = (Class<Property>) classInfo.loadClass();
      Priority annotation = clazz.getAnnotation(Priority.class);
      var priority = 0;
      if (annotation != null) {
        priority = annotation.value();
      }
      Property property = null;
      try {
        property = clazz.getConstructor().newInstance();

      } catch (Exception e) {
        log.error("Can't initialize property handler {}.", clazz.getName());
        if (log.isDebugEnabled()) {
          log.debug(e.getMessage(), e);
        }
      }
      if (property != null) {
        propertiesToAdd
            .computeIfAbsent(property.name(), p -> new ArrayList<>())
            .add(new PropertyPriority(property, priority));
      }
    }

    for (var entry : propertiesToAdd.entrySet()) {
      String name = entry.getKey();
      var list = entry.getValue();
      if (list.size() > 1) {
        list.sort(Comparator.comparingInt(o -> -o.priority()));
        log.warn(
            "Found several tag mappings for tag {} : {}. Using {}",
            name,
            Arrays.toString(list.stream().map(c -> c.property().getClass().getName()).toArray()),
            list.get(0).property().getClass().getName());
      }
      propertyStore.addProperty(name, list.get(0).property());
    }
  }

  @Data
  private static final class PropertyPriority {
    private final Property property;
    private final int priority;
  }
}
