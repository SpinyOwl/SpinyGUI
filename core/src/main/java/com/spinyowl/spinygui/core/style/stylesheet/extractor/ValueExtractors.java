package com.spinyowl.spinygui.core.style.stylesheet.extractor;

import com.spinyowl.spinygui.core.style.stylesheet.annotation.Priority;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Class keeps all existing value extractors. */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValueExtractors {

  @SuppressWarnings("rawtypes")
  private static final Map<Class, ValueExtractor> valueExtractorMap = new HashMap<>();

  static {
    initialize();
  }

  private static void initialize() {
    var scanResult = new ClassGraph().enableAllInfo().scan();
    var extractorsToAdd = new HashMap<Class<?>, List<ValueExtractorPriority>>();
    findExtractors(scanResult, extractorsToAdd);
    fillInExtractorMap(extractorsToAdd);
  }

  private static void findExtractors(
      ScanResult scanResult, HashMap<Class<?>, List<ValueExtractorPriority>> extractorsToAdd) {
    for (ClassInfo classInfo : scanResult.getClassesImplementing(ValueExtractor.class.getName())) {
      @SuppressWarnings("unchecked")
      Class<ValueExtractor<?>> clazz = (Class<ValueExtractor<?>>) classInfo.loadClass();
      Priority annotation = clazz.getAnnotation(Priority.class);
      var priority = 0;
      if (annotation != null) {
        priority = annotation.value();
      }
      ValueExtractor<?> extractor = null;
      try {
        extractor = clazz.getConstructor().newInstance();

      } catch (Exception e) {
        if (log.isErrorEnabled()) {
          log.error("Can't initialize value extractor {}.", clazz.getName());
        }
        if (log.isDebugEnabled()) {
          log.debug(e.getMessage(), e);
        }
      }
      if (extractor != null) {
        extractorsToAdd
            .computeIfAbsent(extractor.getType(), p -> new ArrayList<>())
            .add(new ValueExtractorPriority(extractor, priority));
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static void fillInExtractorMap(
      HashMap<Class<?>, List<ValueExtractorPriority>> extractorsToAdd) {
    for (var entry : extractorsToAdd.entrySet()) {
      var clazz = entry.getKey();
      var list = entry.getValue();
      @SuppressWarnings("rawtypes")
      ValueExtractor extractor;
      if (list.size() > 1) {
        extractor = Collections.max(list, Comparator.comparingInt(o -> -o.priority())).extractor;
        if (log.isWarnEnabled()) {
          log.warn(
              "Found several tag mappings for tag {} : {}. Using {}",
              clazz,
              Arrays.toString(list.stream().map(c -> c.extractor().getClass().getName()).toArray()),
              extractor.getClass().getName());
        }
      } else {
        extractor = list.get(0).extractor;
      }
      add(clazz, extractor);
    }
  }

  public static <T> void add(Class<T> targetValueClass, ValueExtractor<T> valueExtractor) {
    valueExtractorMap.put(targetValueClass, valueExtractor);
  }

  public static void remove(Class<?> targetValueClass) {
    valueExtractorMap.remove(targetValueClass);
  }

  @SuppressWarnings("unchecked")
  public static <T> ValueExtractor<T> of(Class<T> targetValueClass) {
    return valueExtractorMap.get(targetValueClass);
  }

  @Data
  private static final class ValueExtractorPriority {

    private final ValueExtractor<?> extractor;
    private final int priority;
  }
}
