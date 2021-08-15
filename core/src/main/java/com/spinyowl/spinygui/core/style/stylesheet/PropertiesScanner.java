package com.spinyowl.spinygui.core.style.stylesheet;

import static java.util.Comparator.comparingInt;
import com.spinyowl.spinygui.core.style.stylesheet.annotation.Priority;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    findPropertiesToAdd(scanResult, propertiesToAdd);
    fillInPropertyStore(propertyStore, propertiesToAdd);
  }

  private static void findPropertiesToAdd(
      ScanResult scanResult, HashMap<String, List<PropertyPriority>> propertiesToAdd) {
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
        if (log.isErrorEnabled()) {
          log.error("Can't initialize property handler {}.", clazz.getName());
        }
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
  }

  private static void fillInPropertyStore(
      PropertyStore propertyStore, HashMap<String, List<PropertyPriority>> propertiesToAdd) {
    for (var entry : propertiesToAdd.entrySet()) {
      String name = entry.getKey();
      var list = entry.getValue();
      Property property;
      if (list.size() > 1) {
        property = Collections.max(list, comparingInt(o -> -o.priority())).property;
        if (log.isWarnEnabled()) {
          log.warn(
              "Found several tag mappings for tag {} : {}. Using {}",
              name,
              Arrays.toString(list.stream().map(c -> c.property().getClass().getName()).toArray()),
              property.getClass().getName());
        }
      } else {
        property = list.get(0).property;
      }
      propertyStore.addProperty(name, property);
    }
  }

  @Data
  private static final class PropertyPriority {

    private final Property property;
    private final int priority;
  }
}
